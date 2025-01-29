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

package se.swedenconnect.openidfederation;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import org.springframework.web.client.RestClient;
import se.swedenconnect.openidfederation.quarkus.QuarkusResolverRestClient;
import se.swedenconnect.openidfederation.spring.SpringResolverRestClient;

/**
 * Factory class for creating {@link ResolverClient}.
 *
 * @author Felix Hellman
 */
public class ResolverRestClientFactory {
  /**
   * Creates a spring compatible resolver client.
   * @param client
   * @param resolver
   * @param verificationKeys
   * @return new instance
   */
  public ResolverClient create(final RestClient client, final EntityID resolver, final JWKSet verificationKeys) {
    return new ResolverClient(new SpringResolverRestClient(client, resolver), verificationKeys);
  }

  /**
   * Creates a quarkus compatible resolver client
   * @param client
   * @param verificationKeys
   * @return new instance
   */
  public ResolverClient create(final QuarkusResolverRestClient client, final JWKSet verificationKeys) {
    return new ResolverClient(client, verificationKeys);
  }
}
