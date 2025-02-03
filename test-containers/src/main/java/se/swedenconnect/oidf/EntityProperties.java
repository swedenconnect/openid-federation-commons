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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Property class for entities.
 *
 * @author Felix Hellman
 */
@Getter
@Setter
@Builder
public class EntityProperties implements PropertyCustomizer {
  private final String issuer;
  private final String subject;
  private final String jsonMetadata;
  private final List<String> keys;
  private final List<TrustMarkSourceProperties> trustMarkSourceProperties;

  /**
   * Constructor.
   * @param issuer
   * @param subject
   * @param jsonMetadata
   * @param keys
   * @param trustMarkSourceProperties
   */
  public EntityProperties(
      final String issuer,
      final String subject,
      final String jsonMetadata,
      final List<String> keys,
      final List<TrustMarkSourceProperties> trustMarkSourceProperties) {
    this.issuer = issuer;
    this.subject = subject;
    this.jsonMetadata = jsonMetadata;
    if (Objects.isNull(keys)) {
      this.keys = List.of();
    } else {
      this.keys = keys;
    }
    if (Objects.isNull(trustMarkSourceProperties)) {
      this.trustMarkSourceProperties = List.of();
    } else {
      this.trustMarkSourceProperties = trustMarkSourceProperties;
    }
  }

  /**
   * Applies configuration to container
   * @param container to apply configuration for
   * @param index of the property
   */
  @Override
  public void customize(final OpenIdFederationServiceContainer container, final int index) {
    container
        .withEnv(PropertyPath.entity(index).issuer(), this.issuer)
        .withEnv(PropertyPath.entity(index).subject(), this.subject);

    Optional.ofNullable(this.jsonMetadata).ifPresent(metadata -> {
      container.withEnv(PropertyPath.entity(index).metadata(), metadata);
    });

    for (int x = 0; x < this.keys.size(); x++) {
      container.withEnv(PropertyPath.entity(index).key(x), this.keys.get(x));
    }

    for (int x = 0; x < this.trustMarkSourceProperties.size(); x++) {
      final TrustMarkSourceProperties trustMarkSourceProperties = this.trustMarkSourceProperties.get(x);
      container
          .withEnv(PropertyPath.entity(index)
              .trustMarkSourcePath(x)
              .issuer(),
              trustMarkSourceProperties.getIssuer())

          .withEnv(PropertyPath.entity(index)
              .trustMarkSourcePath(x)
              .trustMarkId(),
              trustMarkSourceProperties.getTrustMarkId());
    }
  }
}
