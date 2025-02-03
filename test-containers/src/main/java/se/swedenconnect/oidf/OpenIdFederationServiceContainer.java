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

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Objects;
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

  private Integer port = 8080;
  private final AtomicInteger entityIndex = new AtomicInteger(0);
  private final AtomicInteger trustAnchorIndex = new AtomicInteger(0);

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

  /**
   * Configures a trust anchor for this instance.
   * @param entityId for trust anchor
   * @param alias for trust anchor
   * @param trustAnchorMetadata for trust anchor
   * @return this
   */
  public OpenIdFederationServiceContainer withTrustAnchor(
      final String entityId,
      final String alias,
      final String trustAnchorMetadata) {
    this.withEnv("OPENID_FEDERATION_MODULES_TRUSTANCHORS[%d]_ENTITYIDENTIFIER"
        .formatted(this.trustAnchorIndex.get()), entityId);
    this.withEnv("OPENID_FEDERATION_MODULES_TRUSTANCHORS[%d]_ALIAS"
        .formatted(this.trustAnchorIndex.getAndIncrement()), alias);
    return this.withEntity(entityId, trustAnchorMetadata);
  }


  /**
   * Adds entity statement to this instance
   * @param entity to add
   * @param issuer of the entity
   * @return this
   */
  public OpenIdFederationServiceContainer withEntityStatement(final String entity, final String issuer) {
    return this.withEntityStatement(entity, issuer, List.of("sign-key-1"), null);
  }

  /**
   * Adds entity statement to this instance
   * @param entity to add
   * @param issuer of the entity
   * @param publicKeys of the entity
   * @param jsonMetadata for hosted records
   * @return this
   */
  public OpenIdFederationServiceContainer withEntityStatement(final String entity, final String issuer,
                                                              final List<String> publicKeys,
                                                              final String jsonMetadata) {
    this.withEnv("OPENID_FEDERATION_ENTITIES[%d]_SUBJECT".formatted(this.entityIndex.get()), entity);
    this.withEnv("OPENID_FEDERATION_ENTITIES[%d]_ISSUER".formatted(this.entityIndex.get()), issuer);
    if (Objects.nonNull(publicKeys)) {
      for (int x = 0; x < publicKeys.size(); x++) {
        this.withEnv("OPENID_FEDERATION_ENTITIES[%d]_PUBLICKEYS[%d]".formatted(this.entityIndex.get(), x),
            publicKeys.get(x));
      }
    }
    if (Objects.nonNull(jsonMetadata)) {
      this.withEnv("OPENID_FEDERATION_ENTITIES[%d]_HOSTEDRECORD_METADATA_JSON"
          .formatted(this.entityIndex.get()), jsonMetadata);
    }
    this.entityIndex.incrementAndGet();
    return self();
  }


  private OpenIdFederationServiceContainer withEntity(final String entityId, final String jsonMetadata) {
    return this.withEntityStatement(entityId, entityId, null, jsonMetadata);
  }

  /**
   * @return port of this container
   */
  public Integer getPort() {
    return this.port;
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
}
