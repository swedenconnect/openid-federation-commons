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

/**
 * Properties class for trust mark source.
 *
 * @author Felix Hellman
 */
@Builder
@Getter
public class TrustMarkSourceProperties {

  /**
   * Constructor.
   * @param issuer of trust mark
   * @param trustMarkId of trust mark
   */
  public TrustMarkSourceProperties(final String issuer, final String trustMarkId) {
    this.issuer = issuer;
    this.trustMarkId = trustMarkId;
  }

  private String issuer;
  private String trustMarkId;
}
