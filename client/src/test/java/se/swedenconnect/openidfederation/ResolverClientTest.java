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
package se.swedenconnect.openidfederation;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Body;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import se.swedenconnect.openidfederation.quarkus.QuarkusInternalRestClient;
import se.swedenconnect.openidfederation.quarkus.QuarkusResolverRestClient;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class ResolverClientTest {
  static {
    final WireMockServer wireMockServer = new WireMockServer(11000);
    wireMockServer.start();
    WireMock.configureFor(11000);
  }

  @Test
  void testSpringResolver() throws JOSEException {
    final RSAKey signKey = generateKey();
    WireMock.stubFor(WireMock.get(urlPathEqualTo("/resolve"))
        .withQueryParam("sub", WireMock.matching(".*"))
        .withQueryParam("trust_anchor", WireMock.matching(".*"))
        .willReturn(new ResponseDefinitionBuilder().withResponseBody(new Body(new ResolverResponseFactory().generateResolverResponse(signKey)))));
    final ResolverClient resolver = new ResolverRestClientFactory().create(RestClient.builder().build(), new EntityID("http://localhost:11000"),
        new JWKSet(signKey.toPublicJWK()));

    final Body body = new Body("[\"%s\"]".formatted("http://subject.test"));
    final ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder()
        .withHeader("Content-Type", "application/json")
        .withResponseBody(body);
    WireMock.stubFor(WireMock.get(urlPathEqualTo("/discovery"))
        .willReturn(responseDefinitionBuilder));
    resolver.discovery(new DiscoveryRequest("http://trustanchor.test", null, null));
    final ResolverResponse response = resolver.resolve(new ResolverRequest(new EntityID("http://trustanchor.test"),
        new EntityID("http://subject.test"), null));
    Assertions.assertTrue(Objects.nonNull(response.getOpenIdProvider().get().getLogoUri()));
    Assertions.assertTrue(Objects.nonNull(response.getOpenIdProvider().get().getAuthEndpoint()));
  }

  private static RSAKey generateKey() throws JOSEException {
    return new RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .issueTime(new Date())
        .generate();
  }
}
