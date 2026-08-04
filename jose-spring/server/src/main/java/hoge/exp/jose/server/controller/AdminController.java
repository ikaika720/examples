package hoge.exp.jose.server.controller;

import com.nimbusds.jose.jwk.OctetKeyPair;
import hoge.exp.jose.common.model.AdminRegisterRequest;
import hoge.exp.jose.common.model.Transaction;
import hoge.exp.jose.server.model.Account;
import hoge.exp.jose.server.repository.AccountRepository;
import hoge.exp.jose.server.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminController(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> registerAccount(@RequestBody AdminRegisterRequest request) {
        try {
            OctetKeyPair clientPublicJwk = OctetKeyPair.parse(request.publicKeyJwkJson());
            OctetKeyPair clientEncryptionJwk = OctetKeyPair.parse(request.encryptionKeyJwkJson());

            Account account = new Account(request.accountId(), clientPublicJwk, clientEncryptionJwk, request.initialBalance());
            accountRepository.save(account);

            System.out.println("[SERVER ADMIN] Registered Account: " + request.accountId() + " with balance: " + request.initialBalance());

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "accountId", request.accountId(),
                    "message", "Account and JWKs registered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JWK JSON or request format: " + e.getMessage()));
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Map<String, Object>>> listAllAccounts() {
        List<Map<String, Object>> accounts = accountRepository.findAll().stream()
                .map(acc -> Map.<String, Object>of(
                        "accountId", acc.getAccountId(),
                        "sigKeyId", acc.getPublicKeyJwk().getKeyID(),
                        "encKeyId", acc.getEncryptionKeyJwk().getKeyID(),
                        "balance", acc.getBalance()
                ))
                .toList();

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> listAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }
}
