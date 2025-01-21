/*
 * Copyright 2024 Sweden Connect
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

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Request class for making requests towards a resolver.
 *
 * @param trustAnchor
 * @param subject
 * @param type (optional)
 * @author Felix Hellman
 */
public record ResolverRequest(@Nonnull EntityID trustAnchor, @Nonnull EntityID subject, @Nullable EntityType type) {
}
