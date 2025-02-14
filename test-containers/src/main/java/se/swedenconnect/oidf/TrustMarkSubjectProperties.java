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

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Properties for trust mark subject.
 *
 * @author Felix Hellman
 */
@AllArgsConstructor
@Builder
public class TrustMarkSubjectProperties implements PropertyCustomizer {
  private final String sub;
  private final String iss;
  private final String tmi;
  private final LocalDateTime granted;
  private final LocalDateTime expires;
  private final Boolean revoked;

  @Override
  public void customize(final OpenIdFederationServiceContainer container, final int index) {
    container
        .withEnv(PropertyPath.trustMarkSubjectPath(index).iss(), this.iss)
        .withEnv(PropertyPath.trustMarkSubjectPath(index).tmi(), this.tmi)
        .withEnv(PropertyPath.trustMarkSubjectPath(index).sub(), this.sub)
        .withEnvIfPresent(
            PropertyPath.trustMarkSubjectPath(index).expires(),
            Optional.ofNullable(this.expires)
                .map(exp -> exp.format(DateTimeFormatter.ISO_INSTANT)))
        .withEnvIfPresent(
            PropertyPath.trustMarkSubjectPath(index).granted(),
            Optional.ofNullable(this.granted)
                .map(g -> g.format(DateTimeFormatter.ISO_INSTANT)))
        .withEnvIfPresent(PropertyPath.trustMarkSubjectPath(index).revoked(),
            Optional.ofNullable(this.revoked).map(Object::toString));
  }
}
