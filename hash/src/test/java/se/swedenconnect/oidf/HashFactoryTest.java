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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.swedenconnect.oidf.hash.HashException;
import se.swedenconnect.oidf.hash.HashFactory;

import java.util.Map;

class HashFactoryTest {

  @Test
  void testJsonHash() throws HashException {
    final String minimized = HashFactory.sha256FromJson("""
        {"test":"test"}
        """);

    final String whitespaced = HashFactory.sha256FromJson("""
         { "test"  :  
           
                    "test" }
         """);

    Assertions.assertEquals(minimized, whitespaced);
    Assertions.assertEquals("3e80b3778b3b03766e7be993131c0af2ad05630c5d96fb7fa132d05b77336e04", minimized);
  }

  @Test
  void testObjectHash() throws HashException {
    final Map<String, String> test = Map.of("test", "test");

    final String minimized = HashFactory.sha256FromJson("""
        {"test":"test"}
        """);

    final String fromObject = HashFactory.sha256FromObject(test);

    Assertions.assertEquals(minimized, fromObject);
    Assertions.assertEquals("3e80b3778b3b03766e7be993131c0af2ad05630c5d96fb7fa132d05b77336e04", fromObject);
  }

}