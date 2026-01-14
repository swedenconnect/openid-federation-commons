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

import java.util.List;

/**
 * Discovery request towards resolver discovery endpoint.
 * @param trustAnchor to search via
 * @param types to search for
 * @param trustMarkIds to search for
 *
 * @author Felix Hellamn
 */
public record DiscoveryRequest(String trustAnchor, List<String> types, List<String> trustMarkIds) {
}
