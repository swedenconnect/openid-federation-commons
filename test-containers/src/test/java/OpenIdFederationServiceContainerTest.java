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
import se.swedenconnect.oidf.OpenIdFederationServiceContainer;

import java.util.Arrays;
import java.util.List;

public class OpenIdFederationServiceContainerTest {

  private static final Logger log = LoggerFactory.getLogger(OpenIdFederationServiceContainer.class);

  @Test
  void testStartup() {
    final String trustAnchor = "https://myentity.test/ta";
    final OpenIdFederationServiceContainer container = new OpenIdFederationServiceContainer()
        .withTrustAnchor(trustAnchor, "ta", OpenIdFederationServiceContainer.TRUST_ANCHOR_DEFAULT_METADATA)
        .withLogConsumer(new Slf4jLogConsumer(log))
        .withAccessToHost(true);

    final List<String> expectedEntities = List.of(
        "https://first.test",
        "https://second.test",
        "https://third.test"
    );

    expectedEntities.forEach(entity -> container.withEntityStatement(entity, trustAnchor));

    container.start();

    final List<String> listing = Arrays.asList(RestAssured
        .get("http://localhost:%d/ta/subordinate_listing".formatted(container.getPort()))
        .as(String[].class));

    log.info("Got subordinate listing from container {}", listing);
    Assertions.assertTrue(listing.containsAll(expectedEntities));
  }
}
