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

import lombok.AllArgsConstructor;

/**
 * Property path builder.
 *
 * @author Felix Hellman
 */
public class PropertyPath {
  private static final String ENTITIES = "OPENID_FEDERATION_ENTITIES[%d]";
  private static final String TRUST_ANCHOR = "OPENID_FEDERATION_MODULES_TRUSTANCHORS[%d]";
  private static final String TRUST_MARK_ISSUER = "OPENID_FEDERATION_MODULES_TRUSTMARKISSUERS[%d]";
  private static final String INTEGRATION = "OPENID_FEDERATION_REGISTRY_INTEGRATION";
  private static final String TRUST_MARK_SUBEJCTS = "OPENID_FEDERATION_TRUSTMARKSUBJECTS[%d]";

  private final StringBuilder path = new StringBuilder();

  /**
   * Create path for entities
   * @param index of the entity
   * @return entity path builder
   */
  public static EntityPath entity(final int index) {
    final PropertyPath propertyPath = new PropertyPath();
    propertyPath.append(ENTITIES.formatted(index));
    return new EntityPath(propertyPath);
  }

  /**
   * Create path for trust anchor
   * @param index of the trust anchor
   * @return trust anchor path builder
   */
  public static TrustAnchorPath trustAnchor(final int index) {
    final PropertyPath propertyPath = new PropertyPath();
    propertyPath.append(TRUST_ANCHOR.formatted(index));
    return new TrustAnchorPath(propertyPath);
  }

  /**
   * Create path for trust mark issuer
   * @param index of the trust mark issuer
   * @return trust mark issuer path builder
   */
  public static TrustMarkIssuerPath trustMarkIssuer(final int index) {
    final PropertyPath propertyPath = new PropertyPath();
    propertyPath.append(TRUST_MARK_ISSUER.formatted(index));
    return new TrustMarkIssuerPath(propertyPath);
  }

  /**
   * Create path for integration.
   * @return integration path builder
   */
  public static IntegrationPath integration() {
    final PropertyPath propertyPath = new PropertyPath();
    propertyPath.append(INTEGRATION);
    return new IntegrationPath(propertyPath);
  }

  /**
   * Create path for trust mark subject.
   * @param index of the subject
   * @return path builder for trust mark subject
   */
  public static TrustMarkSubjectPath trustMarkSubjectPath(final int index) {
    final PropertyPath propertyPath = new PropertyPath();
    propertyPath.append(TRUST_MARK_SUBEJCTS.formatted(index));
    return new TrustMarkSubjectPath(propertyPath);
  }

  protected void append(final String content) {
    this.path.append(content);
  }

  protected String finalize(final String content) {
    this.append(content);
    return this.build();
  }

  protected String build() {
    return this.path.toString();
  }

  /**
   * Path builder class for trust anchor.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  public static class TrustAnchorPath {
    private final PropertyPath path;

    /**
     * @return path for entity-identifier
     */
    public String entityId() {
      this.path.append("_ENTITYIDENTIFIER");
      return this.path.build();
    }

    /**
     * @return path for alias
     */
    public String alias() {
      this.path.append("_ALIAS");
      return this.path.build();
    }
  }

  /**
   * Integration path builder class.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  public static class IntegrationPath {
    private final PropertyPath path;

    /**
     * @return path for instance id
     */
    public String instanceId() {
      return this.path.finalize("_INSTANCEID");
    }

    /**
     * @return path for endpoint base path
     */
    public String endpointsBasePath() {
      return this.path.finalize("_ENDPOINTS_BASEPATH");
    }
  }
  /**
   * Trust Mark Issuer path builder class.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  public static class TrustMarkIssuerPath {
    private final PropertyPath path;

    /**
     * @return path for alias
     */
    public String alias() {
      this.path.append("_ALIAS");
      return this.path.build();
    }

    /**
     * @return path for entity-identifier
     */
    public String entityIdentifier() {
      this.path.append("_ENTITYIDENTIFIER");
      return this.path.build();
    }

    /**
     * @return path for trust-mark-validity-duration
     */
    public String trustMarkValidityDuration() {
      this.path.append("_TRUSTMARKVALIDITYDURATION");
      return this.path.build();
    }

    /**
     * @param index of the trust mark
     * @return trust mark path builder
     */
    public TrustMarkPath trustMark(final int index) {
      this.path.append("_TRUSTMARKS[%d]".formatted(index));
      return new TrustMarkPath(this.path);
    }

    /**
     * Path class for Trust Mark.
     *
     * @author Felix Hellman
     */
    @AllArgsConstructor
    public static class TrustMarkPath {
      private final PropertyPath path;

      /**
       * @return path for trust-mark-id
       */
      public String trustMarkId() {
        return this.path.finalize("_TRUSTMARKID");
      }
    }
  }

  /**
   * Trust mark subject path builder class.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  public static class TrustMarkSubjectPath {
    private final PropertyPath path;

    /**
     * @return sub path
     */
    public String sub() {
      return this.path.finalize("_SUB");
    }

    /**
     * @return granted path
     */
    public String granted() {
      return this.path.finalize("_GRANTED");
    }

    /**
     * @return issuer path
     */
    public String iss() {
      return this.path.finalize("_ISS");
    }

    /**
     * @return trust mark issuer path
     */
    public String tmi() {
      return this.path.finalize("_TMI");
    }

    /**
     * @return expires path
     */
    public String expires() {
      return this.path.finalize("_EXPIRES");
    }

    /**
     * @return revoked path
     */
    public String revoked() {
      return this.path.finalize("_REVOKED");
    }
  }

  /**
   * Entity path builder class.
   *
   * @author Felix Hellman
   */
  @AllArgsConstructor
  public static class EntityPath {
    private final PropertyPath path;

    /**
     * @return subject path
     */
    public String subject() {
      this.path.append("_SUBJECT");
      return this.path.build();
    }

    /**
     * @return issuer path
     */
    public String issuer() {
      this.path.append("_ISSUER");
      return this.path.build();
    }

    /**
     * @return metadata path
     */
    public String metadata() {
      this.path.append("_HOSTEDRECORD_METADATA_JSON");
      return this.path.build();
    }

    /**
     * @param index of the key
     * @return public key path
     */
    public String key(final int index) {
      this.path.append("_PUBLICKEYS[%d]".formatted(index));
      return this.path.build();
    }

    /**
     * @param index of the trust mark source path
     * @return builder for trust mark source path
     */
    public EntityPath.TrustMarkSourcePath trustMarkSourcePath(final int index) {
      this.path.append("_HOSTEDRECORD_TRUSTMARKSOURCES[%d]".formatted(index));
      return new EntityPath.TrustMarkSourcePath(this.path);
    }

    /**
     * Trust Mark source path builder class.
     *
     * @author Felix Hellman
     */
    @AllArgsConstructor
    public static class TrustMarkSourcePath {
      private final PropertyPath path;

      /**
       * @return path for trust mark id
       */
      public String trustMarkId() {
        this.path.append("_TRUSTMARKID");
        return this.path.build();
      }

      /**
       * @return issuer of trust mark id
       */
      public String issuer() {
        this.path.append("issuer");
        return this.path.build();
      }
    }
  }
}
