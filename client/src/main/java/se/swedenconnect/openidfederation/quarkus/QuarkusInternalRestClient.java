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

package se.swedenconnect.openidfederation.quarkus;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Quarkus rest client interface.
 *
 * @author Felix Hellman
 */
@Path("/resolve")
@RegisterRestClient
public interface QuarkusInternalRestClient {

  /**
   * Resolve entity via a resolver.
   * @param subject
   * @param trustAnchor
   * @return signed jwt
   */
  @GET
  String resolve(@QueryParam("sub") final String subject, @QueryParam("trust_anchor") final String trustAnchor);

  /**
   * Resolve entity via a resolver.
   * @param subject
   * @param trustAnchor
   * @param entityType
   * @return signed jwt
   */
  @GET
  String resolve(@QueryParam("sub") final String subject, @QueryParam("trust_anchor") final String trustAnchor,
                 @QueryParam("entity_type") String entityType);

  /**
   * Discovery entities via resolver discovery endpoint.
   * @param trustAnchor
   * @param types
   * @param trustMarkIds
   * @return list of entities
   */
  @GET
  List<String> discovery(
      @QueryParam("trust_anchor") final String trustAnchor,
      @QueryParam("entity_type") final List<String> types,
      @QueryParam("trust_mark_id") final List<String> trustMarkIds);
}
