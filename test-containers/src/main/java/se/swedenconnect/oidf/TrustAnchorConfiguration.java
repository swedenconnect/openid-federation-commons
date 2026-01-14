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
package se.swedenconnect.oidf;

import lombok.Builder;

import java.util.List;

/**
 * Configuration class for trust anchor.
 *
 * @author Felix Hellman
 */
@Builder
public class TrustAnchorConfiguration implements PropertyCustomizer {
  private final EntityProperties properties;
  private final String alias;
  private final List<String> subjectEntityIds;

  /**
   * Constructor.
   * @param properties
   * @param alias
   * @param subjectEntityIds
   */
  public TrustAnchorConfiguration(final EntityProperties properties,
                                  final String alias,
                                  final List<String> subjectEntityIds) {
    this.properties = properties;
    this.alias = alias;
    this.subjectEntityIds = subjectEntityIds;
  }

  @Override
  public void customize(final OpenIdFederationServiceContainer container,
                        final int index) {
    //Configure module
    container.withEnv(PropertyPath.trustAnchor(index).entityId(), this.properties.getIssuer());
    container.withEnv(PropertyPath.trustAnchor(index).alias(), this.alias);
    //Sub-configuration entity
    container.customize(this.properties);

    //Sub-configuration entity-statements (children)
    this.subjectEntityIds.stream()
        .map(sub -> EntityProperties.builder()
            .issuer(this.properties.getIssuer())
            .subject(sub)
            .keys(container.getKeyNames())
            .build())
        .forEach(container::customize);
  }
}
