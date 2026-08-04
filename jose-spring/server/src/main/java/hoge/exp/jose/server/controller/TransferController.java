package hoge.exp.jose.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hoge.exp.jose.common.crypto.JwsService;
import hoge.exp.jose.common.model.Transaction;
import hoge.exp.jose.common.model.TransferRequest;
import hoge.exp.jose.server.model.Account;
import hoge.exp.jose.server.repository.AccountRepository;
import hoge.exp.jose.server.repository.TransactionRepository;
import hoge.exp.jose.server.service.ServerJoseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TransferController {

    private static final String JOSE_MEDIA_TYPE_VALUE = "application/jose";

    private final ServerJoseService serverJoseService;
    private final JwsService jwsService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    public TransferController(ServerJoseService serverJoseService,
                               JwsService jwsService,
                               AccountRepository accountRepository,
                               TransactionRepository transactionRepository) {
        this.serverJoseService = serverJoseService;
        this.jwsService = jwsService;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping(value = "/transfer", consumes = JOSE_MEDIA_TYPE_VALUE, produces = JOSE_MEDIA_TYPE_VALUE)
    public ResponseEntity<?> transferJoseProtected(@RequestBody String jweCompact) {
        try {
            String jwsCompact = serverJoseService.decryptRequestJwe(jweCompact);
            Account fromAccount = serverJoseService.authenticateSenderFromJws(jwsCompact);

            String payloadJson = jwsService.extractPayload(jwsCompact);
            TransferRequest transferReq = objectMapper.readValue(payloadJson, TransferRequest.class);

            Optional<Account> toOpt = accountRepository.findById(transferReq.toAccount());
            if (toOpt.isEmpty()) {
                throw new IllegalArgumentException("Recipient account not found: " + transferReq.toAccount());
            }

            Account toAccount = toOpt.get();

            boolean success = fromAccount.transferTo(toAccount, transferReq.amount());
            if (!success) {
                throw new IllegalArgumentException("Transfer failed: insufficient balance or invalid amount");
            }

            String transactionId = "tx-" + UUID.randomUUID().toString().substring(0, 8);
            String timestamp = Instant.now().toString();

            Transaction tx = new Transaction(
                    transactionId,
                    fromAccount.getAccountId(),
                    toAccount.getAccountId(),
                    transferReq.amount(),
                    timestamp,
                    "SUCCESS"
            );
            transactionRepository.save(tx);

            System.out.println("[SERVER] Transfer SUCCESS: " + fromAccount.getAccountId() + " -> " + toAccount.getAccountId() + " (" + transferReq.amount() + ", TxID: " + transactionId + ")");

            Map<String, Object> responseMap = Map.of(
                    "status", "SUCCESS",
                    "transactionId", transactionId,
                    "fromAccount", fromAccount.getAccountId(),
                    "toAccount", toAccount.getAccountId(),
                    "amountTransferred", transferReq.amount(),
                    "fromNewBalance", fromAccount.getBalance(),
                    "timestamp", timestamp
            );

            String resJoseCompact = serverJoseService.createJoseResponse(responseMap, fromAccount.getEncryptionKeyJwk());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(JOSE_MEDIA_TYPE_VALUE)).body(resJoseCompact);
        } catch (IllegalArgumentException | SecurityException e) {
            System.err.println("[SERVER AUTH ERROR] " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("[SERVER ERROR] Failed to process JOSE transfer: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process JOSE transfer: " + e.getMessage()));
        }
    }
}
