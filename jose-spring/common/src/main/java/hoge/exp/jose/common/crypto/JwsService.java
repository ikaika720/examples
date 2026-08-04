package hoge.exp.jose.common.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwsService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String signWithClaims(String jsonPayload, OctetKeyPair signerKeyPair, long validitySeconds) {
        try {
            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(validitySeconds);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("data", jsonPayload)
                    .build();

            JWSSigner signer = new Ed25519Signer(signerKeyPair);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                    .keyID(signerKeyPair.getKeyID())
                    .type(JOSEObjectType.JOSE)
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWS payload with claims", e);
        }
    }

    public String sign(String jsonPayload, OctetKeyPair signerKeyPair) {
        return signWithClaims(jsonPayload, signerKeyPair, 60);
    }

    public boolean verify(String jwsCompact, OctetKeyPair signerPublicJwk) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(jwsCompact);

        // Algorithm confusion defense
        if (!JWSAlgorithm.EdDSA.equals(jwsObject.getHeader().getAlgorithm())) {
            throw new SecurityException("Unexpected JWS algorithm: " + jwsObject.getHeader().getAlgorithm() + " (Expected EdDSA)");
        }

        JWSVerifier verifier = new Ed25519Verifier(signerPublicJwk.toPublicJWK());
        return jwsObject.verify(verifier);
    }

    public JWTClaimsSet parseClaims(String jwsCompact) {
        try {
            JWSObject jwsObject = JWSObject.parse(jwsCompact);
            return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWS claims payload format", e);
        }
    }

    public String extractPayload(String jwsCompact) {
        try {
            JWTClaimsSet claimsSet = parseClaims(jwsCompact);
            Object data = claimsSet.getClaim("data");
            if (data != null) {
                return data instanceof String ? (String) data : data.toString();
            }
            return objectMapper.writeValueAsString(claimsSet.toJSONObject());
        } catch (Exception e) {
            try {
                JWSObject jwsObject = JWSObject.parse(jwsCompact);
                return jwsObject.getPayload().toString();
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid JWS compact string format", ex);
            }
        }
    }
}
