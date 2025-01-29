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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.swedenconnect.openidfederation.metadata.EntityConfigurationFactory;

/**
 * Spring RestController for publishing EntityConfiguration.
 *
 * @author Felix Hellman
 */
@RestController
public class EntityConfigurationEndpoint {

  /**
   * Constructor.
   * @param factory for creating entity configuration
   */
  public EntityConfigurationEndpoint(final EntityConfigurationFactory factory) {
    this.factory = factory;
  }

  private final EntityConfigurationFactory factory;

  /**
   * @return entity configuration as signed jwt
   * @throws ParseException
   * @throws JOSEException
   */
  @GetMapping("/.well-known/openid-federation")
  public String getEntityConfiguration() throws ParseException, JOSEException {
    return this.factory.getEntityConfiguration().serialize();
  }
}
