package hoge.exp.jose.server.controller;

import hoge.exp.jose.common.model.BalanceResponse;
import hoge.exp.jose.common.model.Transaction;
import hoge.exp.jose.server.model.Account;
import hoge.exp.jose.server.repository.TransactionRepository;
import hoge.exp.jose.server.service.ServerJoseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final String JOSE_MEDIA_TYPE_VALUE = "application/jose";

    private final ServerJoseService serverJoseService;
    private final TransactionRepository transactionRepository;

    public AccountController(ServerJoseService serverJoseService,
                             TransactionRepository transactionRepository) {
        this.serverJoseService = serverJoseService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping(value = "/balance", consumes = JOSE_MEDIA_TYPE_VALUE, produces = JOSE_MEDIA_TYPE_VALUE)
    public ResponseEntity<?> getBalanceJoseProtected(@RequestBody String jweCompact) {
        try {
            String jwsCompact = serverJoseService.decryptRequestJwe(jweCompact);
            Account account = serverJoseService.authenticateSenderFromJws(jwsCompact);

            System.out.println("[SERVER] Balance inquiry verified for " + account.getAccountId() + ": " + account.getBalance());

            BalanceResponse res = new BalanceResponse(
                    account.getAccountId(),
                    account.getBalance(),
                    Instant.now().toString()
            );

            String resJoseCompact = serverJoseService.createJoseResponse(res, account.getEncryptionKeyJwk());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(JOSE_MEDIA_TYPE_VALUE)).body(resJoseCompact);
        } catch (IllegalArgumentException | SecurityException e) {
            System.err.println("[SERVER AUTH ERROR] " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("[SERVER ERROR] Failed to process JOSE balance inquiry: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process JOSE balance inquiry: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/transactions", consumes = JOSE_MEDIA_TYPE_VALUE, produces = JOSE_MEDIA_TYPE_VALUE)
    public ResponseEntity<?> getTransactionsJoseProtected(@RequestBody String jweCompact) {
        try {
            String jwsCompact = serverJoseService.decryptRequestJwe(jweCompact);
            Account account = serverJoseService.authenticateSenderFromJws(jwsCompact);

            List<Transaction> userTransactions = transactionRepository.findByAccountId(account.getAccountId());
            System.out.println("[SERVER] Transaction history inquiry verified for " + account.getAccountId() + " (found " + userTransactions.size() + " items)");

            String resJoseCompact = serverJoseService.createJoseResponse(userTransactions, account.getEncryptionKeyJwk());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(JOSE_MEDIA_TYPE_VALUE)).body(resJoseCompact);
        } catch (IllegalArgumentException | SecurityException e) {
            System.err.println("[SERVER AUTH ERROR] " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("[SERVER ERROR] Failed to process JOSE transactions inquiry: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process JOSE transactions inquiry: " + e.getMessage()));
        }
    }
}
