/*
 * Copyright 2024-2025 Sweden Connect
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

package se.swedenconnect.openidfederation;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.text.ParseException;
import java.util.List;

/**
 * Resolver client for resolving entities via a resolver.
 *
 * @author Felix Hellman
 */
@Slf4j
public class ResolverClient {

  private final ResolverRestClient restClient;
  private final JWKSet verificationKeys;

  /**
   * Constructor.
   * @param restClient to use for requests
   * @param verificationKeys to use for validating responses
   */
  public ResolverClient(final ResolverRestClient restClient, final JWKSet verificationKeys) {
    this.restClient = restClient;
    this.verificationKeys = verificationKeys;
  }

  /**
   * Resolves an entity via a resolver
   * @param request
   * @return validated data from resolver response
   */
  public ResolverResponse resolve(final ResolverRequest request) {
    try {
      final SignedJWT parse = SignedJWT.parse(this.restClient.resolve(request));
      final Key key = this.selectKey(parse);
      final JWSVerifier verifier = new DefaultJWSVerifierFactory().createJWSVerifier(parse.getHeader(), key);
      final boolean verify = parse.verify(verifier);
      if (!verify) {
        throw new IllegalArgumentException("Failed to verify jwt from resolver");
      }
      final JWTClaimsSet jwtClaimsSet = parse.getJWTClaimsSet();

      final List<Object> trustMarks = jwtClaimsSet.getListClaim("trust_marks");
      return ResolverResponse
          .builder()
          .issuer(new EntityID(jwtClaimsSet.getIssuer()))
          .subject(new EntityID(jwtClaimsSet.getSubject()))
          .expiration(jwtClaimsSet.getExpirationTime())
          .issuedAt(jwtClaimsSet.getIssueTime())
          .trustChain(jwtClaimsSet.getStringListClaim("trust_chain"))
          .metadata(jwtClaimsSet.getJSONObjectClaim("metadata"))
          .trustMarks(trustMarks)
          .build();
    } catch (final ParseException | JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Discovery through resolver.
   * @param request criteria for search
   * @return list of entities
   */
  public DiscoveryResponse discovery(final DiscoveryRequest request) {
    log.debug("Performing discovery request {}", request);
    final List<String> entities = this.restClient.discovery(request);
    log.debug("Discovered entities {}", entities);
    final List<ResolverResponse> resolvedEntities = entities.stream()
        .map(sub -> {
          final ResolverRequest resolveRequest =
              new ResolverRequest(
                  new EntityID(request.trustAnchor()),
                  new EntityID(sub),
                  null);
          log.debug("Performing resolve request {}", resolveRequest);
          final ResolverResponse response = this.resolve(resolveRequest);
          log.debug("Resolve response was {}", response);
          return response;
        })
        .toList();
    return new DiscoveryResponse(resolvedEntities);
  }

  private Key selectKey(final SignedJWT jwt) throws JOSEException {
    final JWKSelector selector = new JWKSelector(new JWKMatcher.Builder()
        .keyID(jwt.getHeader().getKeyID())
        .build());

    final JWK jwk = selector
        .select(this.verificationKeys)
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unable to resolve key for JWT with kid:'%s' "
            .formatted(jwt.getHeader().getKeyID())));

    return switch (jwk.getKeyType().getValue()) {
      case "EC" -> jwk.toECKey().toKeyPair().getPublic();
      case "RSA" -> jwk.toRSAKey().toKeyPair().getPublic();
      case null, default -> throw new IllegalArgumentException("Unsupported key type");
    };
  }
}
