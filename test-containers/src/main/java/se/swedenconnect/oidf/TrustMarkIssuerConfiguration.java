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

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Configuration class for trust mark issuer.
 *
 * @author Felix Hellman
 */
@Builder
@AllArgsConstructor
public class TrustMarkIssuerConfiguration implements PropertyCustomizer {

  private final EntityProperties properties;
  private final String alias;
  private Duration trustMarkValidityDuration = Duration.of(30, ChronoUnit.MINUTES);
  private List<TrustMarkProperties> trustMarkProperties = List.of();

  @Override
  public void customize(final OpenIdFederationServiceContainer container, final int index) {
    container.withEnv(PropertyPath.trustMarkIssuer(index).entityIdentifier(), this.properties.getIssuer());
    container.withEnv(PropertyPath.trustMarkIssuer(index).alias(), this.alias);

    container.withEnv(PropertyPath.trustMarkIssuer(index).trustMarkValidityDuration(),
        this.trustMarkValidityDuration.toString());

    container.customize(this.properties);

    for (int x = 0; x < this.trustMarkProperties.size(); x++) {
      final TrustMarkProperties properties = this.trustMarkProperties.get(x);
      container.withEnv(PropertyPath.trustMarkIssuer(index).trustMark(x).trustMarkId(), properties.trustMarkId);
    }
  }

  /**
   * Properties for trust mark.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  @Builder
  public static class TrustMarkProperties {
    private final String trustMarkId;
  }
}
