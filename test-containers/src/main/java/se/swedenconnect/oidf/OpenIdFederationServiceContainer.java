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
package se.swedenconnect.oidf;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Container for OpenId Federation Services.
 *
 * @author Felix Hellman
 */
public class OpenIdFederationServiceContainer extends GenericContainer<OpenIdFederationServiceContainer> {

  public static final String TRUST_ANCHOR_DEFAULT_METADATA = """
      {
         "federation_entity": {
         "organization_name": "Private",
         "federation_fetch_endpoint": "https://private.local.swedenconnect.se/ta/fetch",
         "federation_list_endpoint": "https://private.local.swedenconnect.se/ta/subordinate_listing"
         }
      }
      """;

  public static final String TRUST_MARK_ISSUER_DEFAULT_METADATA = """
      {
        "federation_entity": {
          "organization_name": "Authorization"
        }
      }
      """;

  @Getter
  private Integer port = 8080;
  private final AtomicInteger entityIndex = new AtomicInteger(0);
  private final AtomicInteger trustAnchorIndex = new AtomicInteger(0);
  private final AtomicInteger trustMarkIssuerIndex = new AtomicInteger(0);

  /**
   * Default constructor.
   */
  public OpenIdFederationServiceContainer() {
    super(DockerImageName.parse("ghcr.io/swedenconnect/openid-federation-services:latest"));
    this.withCopyFileToContainer(
        MountableFile.forClasspathResource("services/application.yml"), "/container/application.yml");
    this.withEnv("SPRING_CONFIG_IMPORT", "/container/application.yml");
    this.withCopyFileToContainer(
        MountableFile.forClasspathResource("services/signkey.p12"), "/container/signkey.p12");
    this.withCopyFileToContainer(
        MountableFile.forClasspathResource("services/truststore.p12"), "/container/truststore.p12");

    this.port = 1024 + new Random().nextInt(65535 - 1024);
    this.withEnv("SERVER_PORT", "%d".formatted(this.port));
  }

  @Override
  public OpenIdFederationServiceContainer withAccessToHost(final boolean flag) {
    super.withAccessToHost(flag);
    if (flag) {
      this.withExposedPorts(this.getPort());
      this.setPortBindings(List.of("%d:%d".formatted(this.getPort(), this.getPort())));
    }
    return self();
  }

  /**
   * @param properties to apply
   * @return this
   */
  public OpenIdFederationServiceContainer customize(final EntityProperties properties) {
    properties.customize(this, this.entityIndex.getAndIncrement());
    return self();
  }

  /**
   * @param properties to apply
   * @return this
   */
  public OpenIdFederationServiceContainer customize(final TrustAnchorConfiguration properties) {
    properties.customize(this, this.trustAnchorIndex.getAndIncrement());
    return self();
  }

  /**
   * @param properties to apply
   * @return this
   */
  public OpenIdFederationServiceContainer customize(final TrustMarkIssuerConfiguration properties) {
    properties.customize(this, this.trustMarkIssuerIndex.getAndIncrement());
    return self();
  }

  /**
   * @return list of active key names
   */
  public List<String> getKeyNames() {
    return List.of("sign-key-1");
  }
}
