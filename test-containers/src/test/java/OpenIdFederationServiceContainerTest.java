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

import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import se.swedenconnect.oidf.EntityProperties;
import se.swedenconnect.oidf.OpenIdFederationServiceContainer;
import se.swedenconnect.oidf.TrustAnchorConfiguration;
import se.swedenconnect.oidf.TrustMarkIssuerConfiguration;
import se.swedenconnect.oidf.TrustMarkSourceProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class OpenIdFederationServiceContainerTest {

  private static final Logger log = LoggerFactory.getLogger(OpenIdFederationServiceContainer.class);

  @Test
  void testStartup() {
    final List<String> expectedEntities = List.of(
        "https://first.test",
        "https://second.test",
        "https://third.test"
    );
    final String trustAnchorEntityId = "https://myentity.test/ta";
    final List<TrustMarkIssuerConfiguration.TrustMarkSubjectProperties> trustMarkSubjects = List.of(
        TrustMarkIssuerConfiguration.TrustMarkSubjectProperties.builder()
            .sub("https://myentity.test/ta")
            .build()
    );
    final String trustMarkId = "https://trustmark.test/tmi/certificed";
    final OpenIdFederationServiceContainer container = new OpenIdFederationServiceContainer()
        .customize(TrustAnchorConfiguration.builder()
            .alias("ta")
            .properties(EntityProperties.builder()
                .issuer(trustAnchorEntityId)
                .subject(trustAnchorEntityId)
                .trustMarkSourceProperties(List.of(TrustMarkSourceProperties.builder()
                        .issuer("https://trustmark.test/tmi")
                        .trustMarkId(trustMarkId)
                    .build()))
                .jsonMetadata(OpenIdFederationServiceContainer.TRUST_ANCHOR_DEFAULT_METADATA)
                .build())
            .subjectEntityIds(expectedEntities)
            .build())
        .customize(TrustMarkIssuerConfiguration.builder()
            .alias("tmi")
            .properties(EntityProperties.builder()
                .issuer("https://trustmark.test/tmi")
                .subject("https://trustmark.test/tmi")
                .jsonMetadata(OpenIdFederationServiceContainer.TRUST_MARK_ISSUER_DEFAULT_METADATA)
                .build())

            .trustMarkValidityDuration(Duration.of(15, ChronoUnit.MINUTES))
            .trustMarkProperties(List.of(TrustMarkIssuerConfiguration.TrustMarkProperties.builder()
                    .trustMarkId(trustMarkId)
                    .subjects(trustMarkSubjects)
                .build()))
            .build())
        .withLogConsumer(new Slf4jLogConsumer(log))
        .withAccessToHost(true);

    container.start();

    final List<String> listing = Arrays.asList(RestAssured
        .get("http://localhost:%d/ta/subordinate_listing".formatted(container.getPort()))
        .as(String[].class));

    log.info("Got subordinate listing from container {}", listing);
    Assertions.assertTrue(listing.containsAll(expectedEntities));

    final List<String> trustMarkSubjectsResponse = Arrays.asList(RestAssured
        .get("http://localhost:%d/tmi/trust_mark_listing?trust_mark_id=%s".formatted(
            container.getPort(),
            trustMarkId
        ))
        .as(String[].class));
    log.info("Got subordinate listing from container {}", listing);

    Assertions.assertTrue(trustMarkSubjectsResponse.contains("https://myentity.test/ta"));
    Assertions.assertEquals(1, trustMarkSubjectsResponse.size());
    log.info("Got trust mark subject listing from container {}", trustMarkSubjectsResponse);
  }
}
