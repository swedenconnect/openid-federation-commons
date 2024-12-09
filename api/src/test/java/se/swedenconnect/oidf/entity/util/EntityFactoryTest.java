/*
 * Copyright 2024 Sweden Connect.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.swedenconnect.oidf.entity.util;

import org.junit.jupiter.api.Test;
import se.swedenconnect.oidf.registry.api.model.EntityRecord;
import se.swedenconnect.oidf.registry.api.model.EntityRecordHostedRecord;
import se.swedenconnect.oidf.registry.api.model.EntityRecordHostedRecordTrustMarkSourcesInner;
import se.swedenconnect.oidf.registry.api.model.EntityRecordJwks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The EntityFactoryTest class contains unit tests for the methods in the EntityFactory class.
 * It verifies the behavior of entity creation methods in various scenarios.
 *
 * @author David Goldring
 */
public class EntityFactoryTest {

  /**
   * Tests the creation of a default entity using the EntityFactory.
   * <p>
   * The method verifies:
   *  - The created entity is not null.
   *  - The default subject of the entity is set to the expected default value defined in EntityFactory.
   */
  @Test
  public void testCreateDefaultEntity() {
    final EntityRecord entity = EntityTestFactory.createDefaultEntity();

    assertNotNull(entity);

    assertEquals(EntityTestFactory.SUBJECT_DEFAULT, entity.getSubject());
  }

  @Test
  void validateEntityRecordFields() throws Exception {

    final EntityRecord entityRecord = EntityTestFactory.createDefaultEntity();

    assertNotNull(entityRecord.getEntityRecordId(), "entityRecordId should not be null");
    assertTrue(entityRecord.getEntityRecordId().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"), "entityRecordId should be a valid UUID");

    assertNotNull(entityRecord.getIssuer(), "issuer should not be null");
    assertFalse(entityRecord.getIssuer().isEmpty(), "issuer should not be empty");

    assertNotNull(entityRecord.getSubject(), "subject should not be null");
    assertFalse(entityRecord.getSubject().isEmpty(), "subject should not be empty");

    assertNotNull(entityRecord.getPolicyRecordId(), "policyRecordId should not be null");
    assertTrue(entityRecord.getPolicyRecordId().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"), "policyRecordId should be a valid UUID");

    if (entityRecord.getHostedRecord() != null) {
      EntityRecordHostedRecord hostedRecord = entityRecord.getHostedRecord();

      assertNotNull(hostedRecord.getMetadata(), "metadata should not be null");

      assertNotNull(hostedRecord.getAuthorityHints(), "authorityHints should not be null");
      assertFalse(hostedRecord.getAuthorityHints().isEmpty(), "authorityHints should not be empty");

      assertNotNull(hostedRecord.getTrustMarkSources(), "trustMarkSources should not be null");
      for (EntityRecordHostedRecordTrustMarkSourcesInner trustMarkSource : hostedRecord.getTrustMarkSources()) {
      }
    }

    if (entityRecord.getJwks() != null) {
      EntityRecordJwks jwks = entityRecord.getJwks();
      assertNotNull(jwks.getKeys(), "keys should not be null");
      assertFalse(jwks.getKeys().isEmpty(), "keys should not be empty");
    }
  }

  /**
   * Tests the creation of an entity using EntityFactory with a specified subject.
   * <p>
   * The method validates that:
   * - An entity is successfully created and is not null.
   * - The entity's subject is set to the expected subject.
   */
  @Test
  public void testCreateDefaultEntityWithSubject() {
    final String subject = EntityTestFactory.SUBJECT_2;

    final EntityRecord entity = EntityTestFactory.createDefaultEntity(subject);

    assertNotNull(entity);

    assertEquals(subject, entity.getSubject());
  }

}