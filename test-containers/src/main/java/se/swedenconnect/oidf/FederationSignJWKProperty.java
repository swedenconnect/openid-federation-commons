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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.testcontainers.utility.MountableFile;

import java.util.Optional;

/**
 * Property for add sign jwk to container.
 *
 * @author Felix Hellman
 */
@Getter
@Setter
@Builder
public class FederationSignJWKProperty implements PropertyCustomizer {

  private final String classPathLocation;
  private final String password;
  private final String type;
  private final String name;
  private final String keyAlias;
  private final String keyPassword;

  @Override
  public void customize(final OpenIdFederationServiceContainer container, final int index) {
    Optional.ofNullable(this.classPathLocation).ifPresent(cpl -> {
      container.withCopyFileToContainer(MountableFile.forClasspathResource(this.classPathLocation),
          "/container/key-1.p12");
      container.withEnv("CREDENTIAL_BUNDLES_KEYSTORE_K1_LOCATION", "file:/container/key-1.p12");
    });
    container
        .withEnvIfPresent("CREDENTIAL_BUNDLES_KEYSTORE_K1_PASSWORD", Optional.ofNullable(this.password))
        .withEnvIfPresent("CREDENTIAL_BUNDLES_KEYSTORE_K1_TYPE", Optional.ofNullable(this.type))
        .withEnvIfPresent("CREDENTIAL_BUNDLES_JKS_SK_NAME", Optional.ofNullable(this.name))
        .withEnvIfPresent("CREDENTIAL_BUNDLES_JKS_SK_NAME", Optional.ofNullable(this.name))
        .withEnvIfPresent("CREDENTIAL_BUNDLES_JKS_SK_KEY_ALIAS", Optional.ofNullable(this.keyAlias))
        .withEnvIfPresent("CREDENTIAL_BUNDLES_JKS_SK_KEY_KEYPASSWORD", Optional.ofNullable(this.keyPassword));
  }
}
