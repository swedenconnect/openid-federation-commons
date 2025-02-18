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

package se.swedenconnect.openidfederation.spring;

import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import org.springframework.web.client.RestClient;
import se.swedenconnect.openidfederation.DiscoveryRequest;
import se.swedenconnect.openidfederation.ResolverRequest;
import se.swedenconnect.openidfederation.ResolverRestClient;

import java.util.List;
import java.util.Optional;

/**
 * Spring implementation of {@link ResolverRestClient}.
 *
 * @author Felix Hellman
 */
public class SpringResolverRestClient implements ResolverRestClient {
  private final RestClient client;

  /**
   * Constructor.
   * @param client to use
   * @param resolver to use
   */
  public SpringResolverRestClient(final RestClient client, final EntityID resolver) {
    this.client = client.mutate().baseUrl(resolver.toURI()).build();
  }

  @Override
  public String resolve(final ResolverRequest request) {
    return this.client.get()
        .uri(builder -> {
          return builder.path("/resolve")
              .queryParam("trust_anchor", request.trustAnchor().getValue())
              .queryParam("sub", request.subject().getValue())
              .queryParamIfPresent("entity_type",
                  Optional.ofNullable(request.type())
                      .map(Identifier::getValue)).build();
        }).retrieve()
        .toEntity(String.class)
        .getBody();
  }

  @Override
  public List<String> discovery(final DiscoveryRequest request) {
    return this.client.get()
        .uri(builder -> {
          return builder.path("/discovery")
              .queryParam("trust_anchor", request.trustAnchor())
              .queryParam("entity_type", request.types())
              .queryParam("trust_mark_id", request.trustMarkIds())
              .build();
        })
        .retrieve()
        .toEntity(List.class)
        .getBody();
  }
}
