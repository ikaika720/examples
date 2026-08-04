package hoge.exp.jose.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import hoge.exp.jose.client.crypto.JoseVisualizer;
import hoge.exp.jose.common.crypto.JweService;
import hoge.exp.jose.common.crypto.JwsService;
import hoge.exp.jose.common.model.AdminRegisterRequest;
import hoge.exp.jose.common.model.BalanceResponse;
import hoge.exp.jose.common.model.Transaction;
import hoge.exp.jose.common.model.TransferRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class BankApiClient {

    public static final MediaType JOSE_MEDIA_TYPE = MediaType.parseMediaType("application/jose");

    public record ServerJwks(OctetKeyPair x25519Key, OctetKeyPair ed25519Key) {}

    private final RestClient restClient;
    private final JwsService jwsService;
    private final JweService jweService;
    private final ObjectMapper objectMapper;

    public BankApiClient(@Value("${server.baseUrl:http://localhost:8080}") String baseUrl,
                         JwsService jwsService,
                         JweService jweService) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.jwsService = jwsService;
        this.jweService = jweService;
        this.objectMapper = new ObjectMapper();
    }

    public ServerJwks fetchServerJwks() {
        String jwksJson = restClient.get()
                .uri("/.well-known/jwks.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        try {
            JWKSet jwkSet = JWKSet.parse(jwksJson);

            OctetKeyPair x25519Key = jwkSet.getKeys().stream()
                    .filter(OctetKeyPair.class::isInstance)
                    .map(OctetKeyPair.class::cast)
                    .filter(k -> KeyUse.ENCRYPTION.equals(k.getKeyUse()))
                    .findFirst()
                    .orElse(null);

            OctetKeyPair ed25519Key = jwkSet.getKeys().stream()
                    .filter(OctetKeyPair.class::isInstance)
                    .map(OctetKeyPair.class::cast)
                    .filter(k -> KeyUse.SIGNATURE.equals(k.getKeyUse()))
                    .findFirst()
                    .orElse(null);

            if (x25519Key == null || ed25519Key == null) {
                throw new IllegalStateException("JWKS must contain both X25519 encryption key and Ed25519 signature key");
            }

            return new ServerJwks(x25519Key, ed25519Key);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse server JWKS: " + e.getMessage(), e);
        }
    }

    public Map<?, ?> adminRegisterAccount(String accountId, OctetKeyPair clientPublicSigJwk, OctetKeyPair clientPublicEncJwk, long initialBalance) {
        AdminRegisterRequest req = new AdminRegisterRequest(
                accountId,
                clientPublicSigJwk.toPublicJWK().toJSONString(),
                clientPublicEncJwk.toPublicJWK().toJSONString(),
                initialBalance
        );

        return restClient.post()
                .uri("/api/admin/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(Map.class);
    }

    public List<Map<String, Object>> fetchAdminAccounts() {
        return restClient.get()
                .uri("/api/admin/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReferenceImpl<>());
    }

    public List<Transaction> fetchAdminTransactions() {
        return restClient.get()
                .uri("/api/admin/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReferenceImplTransactions<>());
    }

    private void verifyResponseClaims(String jwsCompact) {
        JWTClaimsSet claims = jwsService.parseClaims(jwsCompact);
        if (claims.getExpirationTime() != null && Instant.now().isAfter(claims.getExpirationTime().toInstant())) {
            throw new SecurityException("Server response JWS token expired at " + claims.getExpirationTime().toInstant());
        }
    }

    public BalanceResponse getBalanceJoseProtected(String accountId, OctetKeyPair clientPrivateKeyPair, OctetKeyPair clientEncryptionPrivateKeyPair, ServerJwks serverJwks) {
        try {
            String jsonPayload = "{}";

            String jwsCompact = jwsService.signWithClaims(jsonPayload, clientPrivateKeyPair, 60);
            String jweCompact = jweService.encrypt(jwsCompact, serverJwks.x25519Key());

            JoseVisualizer.printJoseFlow("HTTP POST /api/accounts/balance Request", jsonPayload, jwsCompact, jweCompact);

            String rawJweResponse = restClient.post()
                    .uri("/api/accounts/balance")
                    .contentType(JOSE_MEDIA_TYPE)
                    .accept(JOSE_MEDIA_TYPE)
                    .body(jweCompact)
                    .retrieve()
                    .body(String.class);

            String resJwsCompact = jweService.decrypt(rawJweResponse, clientEncryptionPrivateKeyPair);
            boolean isSignatureValid = jwsService.verify(resJwsCompact, serverJwks.ed25519Key());
            String resPlainJson = jwsService.extractPayload(resJwsCompact);

            JoseVisualizer.printJoseResponseFlow("HTTP POST /api/accounts/balance Response", rawJweResponse, resJwsCompact, resPlainJson, isSignatureValid);

            if (!isSignatureValid) {
                throw new SecurityException("Server JWS signature verification failed for balance response");
            }
            verifyResponseClaims(resJwsCompact);

            return objectMapper.readValue(resPlainJson, BalanceResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute JOSE balance inquiry: " + e.getMessage(), e);
        }
    }

    public List<Transaction> getTransactionsJoseProtected(String accountId, OctetKeyPair clientPrivateKeyPair, OctetKeyPair clientEncryptionPrivateKeyPair, ServerJwks serverJwks) {
        try {
            String jsonPayload = "{}";

            String jwsCompact = jwsService.signWithClaims(jsonPayload, clientPrivateKeyPair, 60);
            String jweCompact = jweService.encrypt(jwsCompact, serverJwks.x25519Key());

            JoseVisualizer.printJoseFlow("HTTP POST /api/accounts/transactions Request", jsonPayload, jwsCompact, jweCompact);

            String rawJweResponse = restClient.post()
                    .uri("/api/accounts/transactions")
                    .contentType(JOSE_MEDIA_TYPE)
                    .accept(JOSE_MEDIA_TYPE)
                    .body(jweCompact)
                    .retrieve()
                    .body(String.class);

            String resJwsCompact = jweService.decrypt(rawJweResponse, clientEncryptionPrivateKeyPair);
            boolean isSignatureValid = jwsService.verify(resJwsCompact, serverJwks.ed25519Key());
            String resPlainJson = jwsService.extractPayload(resJwsCompact);

            JoseVisualizer.printJoseResponseFlow("HTTP POST /api/accounts/transactions Response", rawJweResponse, resJwsCompact, resPlainJson, isSignatureValid);

            if (!isSignatureValid) {
                throw new SecurityException("Server JWS signature verification failed for transactions response");
            }
            verifyResponseClaims(resJwsCompact);

            return objectMapper.readValue(resPlainJson, new TypeReference<List<Transaction>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute JOSE transaction history inquiry: " + e.getMessage(), e);
        }
    }

    public Map<?, ?> transferJoseProtected(TransferRequest transferRequest, OctetKeyPair clientPrivateKeyPair, OctetKeyPair clientEncryptionPrivateKeyPair, ServerJwks serverJwks) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(transferRequest);

            String jwsCompact = jwsService.signWithClaims(jsonPayload, clientPrivateKeyPair, 60);
            String jweCompact = jweService.encrypt(jwsCompact, serverJwks.x25519Key());

            JoseVisualizer.printJoseFlow("HTTP POST /api/transfer Request", jsonPayload, jwsCompact, jweCompact);

            String rawJweResponse = restClient.post()
                    .uri("/api/transfer")
                    .contentType(JOSE_MEDIA_TYPE)
                    .accept(JOSE_MEDIA_TYPE)
                    .body(jweCompact)
                    .retrieve()
                    .body(String.class);

            String resJwsCompact = jweService.decrypt(rawJweResponse, clientEncryptionPrivateKeyPair);
            boolean isSignatureValid = jwsService.verify(resJwsCompact, serverJwks.ed25519Key());
            String resPlainJson = jwsService.extractPayload(resJwsCompact);

            JoseVisualizer.printJoseResponseFlow("HTTP POST /api/transfer Response", rawJweResponse, resJwsCompact, resPlainJson, isSignatureValid);

            if (!isSignatureValid) {
                throw new SecurityException("Server JWS signature verification failed for transfer response");
            }
            verifyResponseClaims(resJwsCompact);

            return objectMapper.readValue(resPlainJson, Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute JOSE transfer: " + e.getMessage(), e);
        }
    }

    private static class ParameterizedTypeReferenceImpl<T> extends org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>> {}
    private static class ParameterizedTypeReferenceImplTransactions<T> extends org.springframework.core.ParameterizedTypeReference<List<Transaction>> {}
}
