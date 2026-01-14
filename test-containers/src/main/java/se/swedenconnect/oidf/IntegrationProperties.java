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
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Properties for configuring registry.
 *
 * @author Felix Hellman
 */
@Getter
@Setter
@Builder
public class IntegrationProperties implements PropertyCustomizer {
  private final UUID instanceId;
  private final String basePath;

  /**
   * Constructor.
   * @param instanceId
   * @param basePath
   */
  public IntegrationProperties(final UUID instanceId, final String basePath) {
    this.instanceId = instanceId;
    this.basePath = basePath;
  }

  @Override
  public void customize(final OpenIdFederationServiceContainer container, final int index) {
    container
        .withEnv(PropertyPath.integration().instanceId(), this.instanceId.toString())
        .withEnv(PropertyPath.integration().endpointsBasePath(), this.basePath);
  }
}
