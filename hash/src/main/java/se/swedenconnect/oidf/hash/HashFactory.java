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
package se.swedenconnect.oidf.hash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Factory class for creating hashes.
 *
 * @author Felix Hellman
 */
public class HashFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(new JavaTimeModule());
  }

  /**
   * Creates a sha256 hash from an object
   * @param object
   * @return sha256 hash
   * @throws HashException
   */
  public static String sha256FromObject(final Object object) throws HashException {
    try {
      final String json = HashFactory.MAPPER.writer().writeValueAsString(object);
      return HashFactory.sha256FromJson(json);
    } catch (final JsonProcessingException e) {
      throw new HashException("Failed to hash from object", e);
    }
  }

  /**
   * Creates sha256 hash from json string.
   * @param json
   * @return sha256 hash
   * @throws HashException
   */
  public static String sha256FromJson(final String json) throws HashException {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-256");
      final byte[] hash = digest.digest(json
          .replaceAll("\\s", "")
          .getBytes(StandardCharsets.UTF_8)
      );
      return new String(Hex.encode(hash));
    } catch (final NoSuchAlgorithmException e) {
      throw new HashException("Failed to hash from json", e);
    }
  }
}
