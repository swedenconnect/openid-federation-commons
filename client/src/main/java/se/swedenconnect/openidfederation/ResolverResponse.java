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

package se.swedenconnect.openidfederation;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import lombok.Builder;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Response class from resolver.
 * @param issuer
 * @param subject
 * @param issuedAt
 * @param expiration
 * @param metadata
 * @param trustMarks
 * @param trustChain
 *
 * @author Felix Hellman
 */
@Builder
public record ResolverResponse(EntityID issuer,
                               EntityID subject,
                               Date issuedAt,
                               Date expiration,
                               Map<String, Object> metadata,
                               List<Object> trustMarks,
                               List<String> trustChain) {

  /**
   * @return openid provider metadata
   */
  public Optional<OpenIdProvider> getOpenIdProvider() {
    final Object metadata = this.metadata.get("openid_provider");
    if (Objects.nonNull(metadata)) {
      return Optional.of(new OpenIdProvider((Map<String, Object>) metadata));
    }
    return Optional.empty();
  }

  /**
   * @return openid relying party metadata
   */
  public Optional<OpenIdRelyingParty> getOpenIdRelyingParty() {
    final Object metadata = this.metadata.get("openid_relying_party");
    if (Objects.nonNull(metadata)) {
      return Optional.of(new OpenIdRelyingParty((Map<String, Object>) metadata));
    }
    return Optional.empty();
  }

  /**
   * OpenIdProvider Metadata class.
   * @param metadata
   *
   * @author Felix Hellman
   */
  public record OpenIdProvider(Map<String, Object> metadata) {
    /**
     * @return logo uri
     */
    public String getLogoUri() {
      return (String) this.metadata.get("logo_uri");
    }

    /**
     * @return auth endpoint
     */
    public String getAuthEndpoint() {
      return (String) this.metadata.get("authorization_endpoint");
    }

    /**
     * @return token endpoint
     */
    public String getTokenEndpoint() {
      return (String) this.metadata.get("token_endpoint");
    }

    /**
     * @return display name
     */
    public String displayName() {
      return (String) this.metadata.get("display_name");
    }

    /**
     * @return jwk set
     */
    public JWKSet getJWKSet() {
      try {
        return JWKSet.parse((Map<String, Object>) (this.metadata.get("jwks")));
      } catch (final ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * OpenId relying party metadata.
   * @param metadata
   *
   * @author Felix Hellman
   */
  public record OpenIdRelyingParty(Map<String, Object> metadata) {
    /**
     * @return logo uri
     */
    public String getLogoUri() {
      return (String) this.metadata.get("logo_uri");
    }

    /**
     * @return client name
     */
    public String getClientName() {
      return (String) this.metadata.get("client_name");
    }

    /**
     * @return jwk set
     */
    public JWKSet getJWKSet() {
      try {
        return JWKSet.parse((Map<String, Object>) (this.metadata.get("jwks")));
      } catch (final ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
