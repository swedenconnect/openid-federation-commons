/*
 * Copyright 2024 Sweden Connect
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
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class ResolverClientTest {
  static {
    final WireMockServer wireMockServer = new WireMockServer(9090);
    wireMockServer.start();
    WireMock.configureFor(9090);
  }

  @Test
  void testResolver() throws JOSEException {
    final RSAKey signKey = generateKey();
    WireMock.stubFor(WireMock.get(urlPathEqualTo("/resolve"))
        .withQueryParam("sub", WireMock.matching(".*"))
        .withQueryParam("trust_anchor", WireMock.matching(".*"))
        .willReturn(new ResponseDefinitionBuilder().withResponseBody(new Body(new ResolverResponseFactory().generateResolverResponse(signKey)))));
    final ResolverClient resolver = new ResolverRestClientFactory().create(RestClient.builder().build(), new EntityID("http://localhost:9090"),
        new JWKSet(signKey.toPublicJWK()));

    final ResolverResponse response = resolver.resolve(new ResolverRequest(new EntityID("http://trustanchor.test"),
        new EntityID("http://subject.test"), null));
    System.out.println(response);
  }

  private static RSAKey generateKey() throws JOSEException {
    return new RSAKeyGenerator(2048)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .issueTime(new Date())
        .generate();
  }
}
