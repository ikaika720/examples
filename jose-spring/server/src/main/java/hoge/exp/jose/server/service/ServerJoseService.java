package hoge.exp.jose.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import hoge.exp.jose.common.crypto.JweService;
import hoge.exp.jose.common.crypto.JwsService;
import hoge.exp.jose.server.model.Account;
import hoge.exp.jose.server.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServerJoseService {

    private final JweService jweService;
    private final JwsService jwsService;
    private final AccountRepository accountRepository;
    private final OctetKeyPair serverX25519KeyPair;
    private final OctetKeyPair serverEd25519KeyPair;
    private final ObjectMapper objectMapper;

    // Cache processed JTI -> ExpirationTime for anti-replay defense
    private final Map<String, Instant> processedJtiCache = new ConcurrentHashMap<>();

    public ServerJoseService(JweService jweService,
                             JwsService jwsService,
                             AccountRepository accountRepository,
                             OctetKeyPair serverX25519KeyPair,
                             OctetKeyPair serverEd25519KeyPair) {
        this.jweService = jweService;
        this.jwsService = jwsService;
        this.accountRepository = accountRepository;
        this.serverX25519KeyPair = serverX25519KeyPair;
        this.serverEd25519KeyPair = serverEd25519KeyPair;
        this.objectMapper = new ObjectMapper();
    }

    public String decryptRequestJwe(String jweCompact) {
        return jweService.decrypt(jweCompact, serverX25519KeyPair);
    }

    public Account authenticateSenderFromJws(String jwsCompact) throws Exception {
        JWSObject jwsObject = JWSObject.parse(jwsCompact);
        String keyIdInHeader = jwsObject.getHeader().getKeyID();

        if (keyIdInHeader == null || keyIdInHeader.isBlank()) {
            throw new IllegalArgumentException("Missing 'kid' (Key ID) header in JWS signature");
        }

        Account account = accountRepository.findAll().stream()
                .filter(acc -> keyIdInHeader.equals(acc.getPublicKeyJwk().getKeyID()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No registered account found matching JWS keyID '" + keyIdInHeader + "'"));

        boolean isValid = jwsService.verify(jwsCompact, account.getPublicKeyJwk());
        if (!isValid) {
            throw new SecurityException("JWS signature verification failed for account '" + account.getAccountId() + "' (keyID: " + keyIdInHeader + ")");
        }

        // Anti-replay claims verification (iat, exp, jti)
        verifyAntiReplayClaims(jwsCompact);

        return account;
    }

    private void verifyAntiReplayClaims(String jwsCompact) {
        JWTClaimsSet claimsSet = jwsService.parseClaims(jwsCompact);

        Date issueTime = claimsSet.getIssueTime();
        Date expirationTime = claimsSet.getExpirationTime();
        String jti = claimsSet.getJWTID();

        Instant now = Instant.now();

        // 1. Validate Expiration Time (exp)
        if (expirationTime != null) {
            if (now.isAfter(expirationTime.toInstant())) {
                throw new SecurityException("Security Error: JWS request expired at " + expirationTime.toInstant() + " (Current time: " + now + ")");
            }
        }

        // 2. Validate Issued At Time (iat)
        if (issueTime != null) {
            if (now.plusSeconds(5).isBefore(issueTime.toInstant())) {
                throw new SecurityException("Security Error: JWS iat (issued at) is in the future: " + issueTime.toInstant());
            }
        }

        // 3. Validate JTI (Replay Attack Defense)
        if (jti != null && !jti.isBlank()) {
            processedJtiCache.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));

            Instant expInstant = expirationTime != null ? expirationTime.toInstant() : now.plusSeconds(60);
            if (processedJtiCache.putIfAbsent(jti, expInstant) != null) {
                throw new SecurityException("Replay Attack Detected! Duplicate JTI '" + jti + "' has already been processed.");
            }
        }
    }

    public String createJoseResponse(Object responsePayload, OctetKeyPair clientEncryptionJwk) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(responsePayload);
        String jwsCompact = jwsService.signWithClaims(jsonPayload, serverEd25519KeyPair, 60);
        return jweService.encrypt(jwsCompact, clientEncryptionJwk);
    }
}
