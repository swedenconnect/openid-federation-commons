/*
 * Copyright 2024-2026 Sweden Connect
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package se.swedenconnect.openidfederation.metadata;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Factory class for creating EntityConfigurations.
 *
 * @author Felix Hellman
 */
public class EntityConfigurationFactory {

  private final EntityConfigurationProperties properties;

  /**
   * Constructor.
   *
   * @param properties to use for entity configuration
   */
  public EntityConfigurationFactory(final EntityConfigurationProperties properties) {
    this.properties = properties;
  }

  /**
   * Creates entity configuration from properties
   *
   * @return entity configuration
   * @throws ParseException
   * @throws JOSEException
   */
  public SignedJWT getEntityConfiguration() throws ParseException, JOSEException {
    final EntityStatementClaimsSet entityStatementClaimsSet = new EntityStatementClaimsSet(
        new Issuer(this.properties.getEntityId()),
        new Subject(this.properties.getEntityId()),
        Date.from(Instant.now()),
        Date.from(Instant.now().plus(7, ChronoUnit.DAYS)),
        this.properties.getJwks().toPublicJWKSet()
    );


    final Map<String, Object> metadata = new HashMap<>(this.properties.getMetadata());
    final JWSSigner signer = this.getSigner();
    List.of(EntityType.OPENID_RELYING_PARTY, EntityType.OPENID_PROVIDER)
            .forEach(type -> {
              final Object o = metadata.get(type.getValue());
              if (Objects.nonNull(o) && o instanceof Map s) {
                final Map<String, Object> subset = s;
                final HashMap<String, Object> tmp = new HashMap<>(subset);
                tmp.put("jwks", this.properties.getJwks().toPublicJWKSet().toJSONObject());
                metadata.put(type.getValue(), tmp);
              }
            });
    entityStatementClaimsSet.setClaim("metadata", metadata);

    final JWSAlgorithm jwsAlgorithm = signer.supportedJWSAlgorithms().stream().findFirst().get();
    final SignedJWT signedJWT = new SignedJWT(new JWSHeader.
        Builder(jwsAlgorithm)
        .type(new JOSEObjectType("entity-statement+jwt"))
        .build(), entityStatementClaimsSet.toJWTClaimsSet());
    signedJWT.sign(signer);
    return signedJWT;
  }

  private JWSSigner getSigner() throws JOSEException {
    final JWK jwk = this.properties.getJwks().getKeys().getFirst();

    return switch (jwk.getKeyType().getValue()) {
      case "EC" -> new ECDSASigner(jwk.toECKey());
      case "RSA" -> new RSASSASigner(jwk.toRSAKey());
      case null, default -> throw new IllegalArgumentException("Unsupported key type");
    };
  }
}
