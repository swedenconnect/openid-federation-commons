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
package se.swedenconnect.openidfederation.metadata;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

class EntityConfigurationFactoryTest {
  @Test
  void factoryCanCreateEntityConfiguration() throws JOSEException, ParseException, java.text.ParseException {
    final EntityConfigurationProperties properties = new EntityConfigurationProperties();
    properties.setEntityId("https://entityid.test");
    final RSAKey key = generateKey();
    properties.setJwks(new JWKSet(key));
    properties.setMetadata(Map.of("federation_entity", Map.of("organization_name", "test"), "openid_relying_party",
        Map.of("something", "something")));
    final SignedJWT entityConfiguration = new EntityConfigurationFactory(properties).getEntityConfiguration();
    final Map<String , Object> subsetMetadata =
        (Map<String, Object>) entityConfiguration.getJWTClaimsSet().getJSONObjectClaim(
        "metadata").get("openid_relying_party");
    Assertions.assertTrue(subsetMetadata.containsKey("jwks"));
    Assertions.assertDoesNotThrow(() -> EntityStatement.parse(entityConfiguration).verifySignatureOfSelfStatement());
  }

  private static RSAKey generateKey() throws JOSEException {
    return new RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .issueTime(new Date())
        .generate();
  }
}