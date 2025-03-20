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
package se.swedenconnect.openidfederation;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResolverResponseFactory {
  public String generateResolverResponse(final JWK signKey) throws JOSEException {
    final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
        .issuer("http://resolver.test")
        .subject("http://subject.test")
        .issueTime(Date.from(Instant.now()))
        .expirationTime(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
        .claim("metadata", Map.of("openid_provider",
            Map.of("logo_uri", "https://logo.test", "authorization_endpoint", "https://auth.test")
        ))
        .claim("trust_marks", List.of())
        .claim("trust_chain", List.of());

    final SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(signKey.toPublicJWK()).build(),
        builder.build());

    signedJWT.sign(new RSASSASigner(signKey.toRSAKey()));

    return signedJWT.serialize();
  }
}
