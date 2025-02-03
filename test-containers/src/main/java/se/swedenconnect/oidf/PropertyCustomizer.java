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

/**
 * Customizer interface for applying properties to the container.
 *
 * @author Felix Hellman
 */
public interface PropertyCustomizer {
  /**
   * Applies property to container
   * @param container to apply configuration to
   * @param index of the property
   */
  void customize(final OpenIdFederationServiceContainer container, int index);
}
