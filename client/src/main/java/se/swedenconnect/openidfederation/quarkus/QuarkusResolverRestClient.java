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
package se.swedenconnect.openidfederation.quarkus;

import se.swedenconnect.openidfederation.DiscoveryRequest;
import se.swedenconnect.openidfederation.ResolverRequest;
import se.swedenconnect.openidfederation.ResolverRestClient;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper class for quarkus rest client.
 *
 * @author Felix Hellman
 */
public class QuarkusResolverRestClient implements ResolverRestClient {
  private final QuarkusInternalRestClient client;

  /**
   * Constructor.
   *
   * @param client to use
   */
  public QuarkusResolverRestClient(final QuarkusInternalRestClient client) {
    this.client = client;
  }

  @Override
  public String resolve(final ResolverRequest request) {
    return Optional.ofNullable(request.type())
        .map(t -> this.client.resolve(request.subject().getValue(), request.trustAnchor().getValue(), t.getValue()))
        .orElseGet(
            () -> this.client.resolve(request.subject().getValue(), request.trustAnchor().getValue())
        );
  }

  @Override
  public List<String> discovery(final DiscoveryRequest request) {
    return this.client
        .discovery(
            request.trustAnchor(),
            request.types(),
            request.trustMarkIds()
        );
  }
}
