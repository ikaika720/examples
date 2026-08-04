package hoge.exp.jose.client.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nimbusds.jose.jwk.OctetKeyPair;
import hoge.exp.jose.client.crypto.KeyStorageService;
import hoge.exp.jose.client.service.BankApiClient;
import hoge.exp.jose.common.crypto.DrbgRandomSupplier;
import hoge.exp.jose.common.crypto.KeyPairGeneratorUtil;
import hoge.exp.jose.common.model.BalanceResponse;
import hoge.exp.jose.common.model.Transaction;
import hoge.exp.jose.common.model.TransferRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JoseClientRunner implements CommandLineRunner {

    private final DrbgRandomSupplier drbgRandomSupplier;
    private final KeyPairGeneratorUtil keyPairGeneratorUtil;
    private final KeyStorageService keyStorageService;
    private final BankApiClient bankApiClient;
    private final ObjectMapper objectMapper;

    public JoseClientRunner(DrbgRandomSupplier drbgRandomSupplier,
                            KeyPairGeneratorUtil keyPairGeneratorUtil,
                            KeyStorageService keyStorageService,
                            BankApiClient bankApiClient) {
        this.drbgRandomSupplier = drbgRandomSupplier;
        this.keyPairGeneratorUtil = keyPairGeneratorUtil;
        this.keyStorageService = keyStorageService;
        this.bankApiClient = bankApiClient;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args == null || args.length == 0 || "demo".equalsIgnoreCase(args[0])) {
            runFullDemo();
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "admin-register" -> handleAdminRegister(args);
            case "admin-accounts" -> handleAdminAccounts();
            case "admin-transactions" -> handleAdminTransactions();
            case "balance" -> handleBalance(args);
            case "history" -> handleHistory(args);
            case "transfer" -> handleTransfer(args);
            default -> printHelp();
        }
    }

    private KeyStorageService.ClientKeySet getOrCreateKeySet(String accountId) {
        Optional<KeyStorageService.ClientKeySet> existing = keyStorageService.loadKeySet(accountId);
        if (existing.isPresent() && existing.get().signatureKeyPair() != null && existing.get().encryptionKeyPair() != null) {
            System.out.println("[CLIENT] Using existing local key set for '" + accountId + "'");
            return existing.get();
        } else {
            OctetKeyPair sigKey = keyPairGeneratorUtil.generateEd25519KeyPair(accountId + "-sig");
            OctetKeyPair encKey = keyPairGeneratorUtil.generateX25519KeyPair(accountId + "-enc");
            keyStorageService.saveKeySet(accountId, sigKey, encKey);
            return new KeyStorageService.ClientKeySet(sigKey, encKey);
        }
    }

    private void handleAdminRegister(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ./gradlew :client:bootRun --args=\"admin-register <accountId> [initialBalance]\"");
            return;
        }
        String accountId = args[1];
        long initialBalance = args.length >= 3 ? Long.parseLong(args[2]) : 1_000_000L;

        KeyStorageService.ClientKeySet keySet = getOrCreateKeySet(accountId);

        Map<?, ?> result = bankApiClient.adminRegisterAccount(accountId, keySet.signatureKeyPair().toPublicJWK(), keySet.encryptionKeyPair().toPublicJWK(), initialBalance);
        System.out.println("[ADMIN REGISTER] " + result);
    }

    private void handleAdminAccounts() throws Exception {
        List<Map<String, Object>> accounts = bankApiClient.fetchAdminAccounts();
        System.out.println("================================================================================");
        System.out.println(" [ADMIN] Registered Accounts List (" + accounts.size() + " accounts)");
        System.out.println("================================================================================");
        System.out.println(objectMapper.writeValueAsString(accounts));
    }

    private void handleAdminTransactions() throws Exception {
        List<Transaction> transactions = bankApiClient.fetchAdminTransactions();
        System.out.println("================================================================================");
        System.out.println(" [ADMIN] Transaction History Log (" + transactions.size() + " transactions)");
        System.out.println("================================================================================");
        System.out.println(objectMapper.writeValueAsString(transactions));
    }

    private void handleBalance(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ./gradlew :client:bootRun --args=\"balance <accountId>\"");
            return;
        }
        String accountId = args[1];
        KeyStorageService.ClientKeySet keySet = keyStorageService.loadKeySet(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Key set for account '" + accountId + "' not found in local key storage. Register first with 'admin-register'."));

        BankApiClient.ServerJwks serverJwks = bankApiClient.fetchServerJwks();
        BalanceResponse res = bankApiClient.getBalanceJoseProtected(accountId, keySet.signatureKeyPair(), keySet.encryptionKeyPair(), serverJwks);

        System.out.println("================================================================================");
        System.out.println(" [JOSE BALANCE] Account: " + res.accountId() + " | Balance: " + res.balance());
        System.out.println("================================================================================");
    }

    private void handleHistory(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: ./gradlew :client:bootRun --args=\"history <accountId>\"");
            return;
        }
        String accountId = args[1];
        KeyStorageService.ClientKeySet keySet = keyStorageService.loadKeySet(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Key set for account '" + accountId + "' not found in local key storage."));

        BankApiClient.ServerJwks serverJwks = bankApiClient.fetchServerJwks();
        List<Transaction> history = bankApiClient.getTransactionsJoseProtected(accountId, keySet.signatureKeyPair(), keySet.encryptionKeyPair(), serverJwks);

        System.out.println("================================================================================");
        System.out.println(" [JOSE USER HISTORY] Transactions for " + accountId + " (" + history.size() + " records)");
        System.out.println("================================================================================");
        System.out.println(objectMapper.writeValueAsString(history));
    }

    private void handleTransfer(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: ./gradlew :client:bootRun --args=\"transfer <fromAccount> <toAccount> <amount>\"");
            return;
        }
        String fromAccount = args[1];
        String toAccount = args[2];
        long amount = Long.parseLong(args[3]);

        KeyStorageService.ClientKeySet fromKeySet = keyStorageService.loadKeySet(fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("Key set for sender '" + fromAccount + "' not found in local key storage."));

        BankApiClient.ServerJwks serverJwks = bankApiClient.fetchServerJwks();

        TransferRequest req = new TransferRequest(toAccount, amount);

        Map<?, ?> response = bankApiClient.transferJoseProtected(req, fromKeySet.signatureKeyPair(), fromKeySet.encryptionKeyPair(), serverJwks);
        System.out.println("================================================================================");
        System.out.println(" [JOSE TRANSFER RESULT]");
        System.out.println("================================================================================");
        System.out.println(objectMapper.writeValueAsString(response));
    }

    private void runFullDemo() throws Exception {
        System.out.println("================================================================================");
        System.out.println(" JOSE (JWS / JWE / JWK) Full End-to-End Demo - Spring Boot");
        System.out.println(" Cryptography: Ed25519 (EdDSA) + X25519 (ECDH-ES+A256KW) + AES-256-GCM");
        System.out.println(" CSPRNG      : NIST SP 800-90A 256-bit DRBG (Capability: PR_AND_RESEED)");
        System.out.println(" Target      : http://localhost:8080");
        System.out.println("================================================================================");
        System.out.println();

        System.out.println("[STEP 1] DRBG & Fetch Server Public JWKs (X25519 & Ed25519)");
        System.out.println("  DRBG Personalization: " + drbgRandomSupplier.getPersonalizationString());
        BankApiClient.ServerJwks serverJwks = bankApiClient.fetchServerJwks();
        System.out.println("  Fetched Server X25519 Enc Key : " + serverJwks.x25519Key().toPublicJWK().toJSONString());
        System.out.println("  Fetched Server Ed25519 Sig Key : " + serverJwks.ed25519Key().toPublicJWK().toJSONString());

        System.out.println("\n[STEP 2] Admin Account & Key Provisioning (Alice & Bob)");
        OctetKeyPair aliceSigKey = keyPairGeneratorUtil.generateEd25519KeyPair("alice-sig");
        OctetKeyPair aliceEncKey = keyPairGeneratorUtil.generateX25519KeyPair("alice-enc");
        OctetKeyPair bobSigKey = keyPairGeneratorUtil.generateEd25519KeyPair("bob-sig");
        OctetKeyPair bobEncKey = keyPairGeneratorUtil.generateX25519KeyPair("bob-enc");

        keyStorageService.saveKeySet("Alice", aliceSigKey, aliceEncKey);
        keyStorageService.saveKeySet("Bob", bobSigKey, bobEncKey);

        bankApiClient.adminRegisterAccount("Alice", aliceSigKey.toPublicJWK(), aliceEncKey.toPublicJWK(), 1_000_000L);
        bankApiClient.adminRegisterAccount("Bob", bobSigKey.toPublicJWK(), bobEncKey.toPublicJWK(), 500_000L);
        System.out.println("  Registered Alice (1,000,000) & Bob (500,000)");

        System.out.println("\n[STEP 3] Check Balances Before Transfer (JOSE Protected POST)");
        BalanceResponse aliceBal1 = bankApiClient.getBalanceJoseProtected("Alice", aliceSigKey, aliceEncKey, serverJwks);
        BalanceResponse bobBal1 = bankApiClient.getBalanceJoseProtected("Bob", bobSigKey, bobEncKey, serverJwks);
        System.out.println("  Alice Balance: " + aliceBal1.balance());
        System.out.println("  Bob Balance  : " + bobBal1.balance());

        System.out.println("\n[STEP 4] Execute JOSE Secure Transfer (Alice -> Bob: 500,000)");
        TransferRequest req = new TransferRequest("Bob", 500_000L);
        Map<?, ?> transferRes = bankApiClient.transferJoseProtected(req, aliceSigKey, aliceEncKey, serverJwks);
        System.out.println("  Transfer Response:\n" + objectMapper.writeValueAsString(transferRes));

        System.out.println("\n[STEP 5] Check Balances After Transfer (JOSE Protected POST)");
        BalanceResponse aliceBal2 = bankApiClient.getBalanceJoseProtected("Alice", aliceSigKey, aliceEncKey, serverJwks);
        BalanceResponse bobBal2 = bankApiClient.getBalanceJoseProtected("Bob", bobSigKey, bobEncKey, serverJwks);
        System.out.println("  Alice Balance (After): " + aliceBal2.balance());
        System.out.println("  Bob Balance (After)  : " + bobBal2.balance());

        System.out.println("\n[STEP 6] Query User Transaction History (JOSE Protected POST)");
        List<Transaction> aliceHistory = bankApiClient.getTransactionsJoseProtected("Alice", aliceSigKey, aliceEncKey, serverJwks);
        System.out.println("  Alice Transaction History (" + aliceHistory.size() + " items):\n" + objectMapper.writeValueAsString(aliceHistory));

        System.out.println("\n[STEP 7] Query Admin Accounts & Transactions List");
        List<Map<String, Object>> adminAccounts = bankApiClient.fetchAdminAccounts();
        List<Transaction> adminTxList = bankApiClient.fetchAdminTransactions();
        System.out.println("  Admin Accounts     : " + adminAccounts.size() + " accounts registered");
        System.out.println("  Admin Transactions : " + adminTxList.size() + " transactions logged");

        System.out.println("\n================================================================================");
        System.out.println(" [SUCCESS] Full End-to-End JOSE Demo Complete!");
        System.out.println("================================================================================");
    }

    private void printHelp() {
        System.out.println("""
            JOSE Bank Client Subcommands:
              demo                                           Run full end-to-end demo
              admin-register <accountId> [initialBalance]   Provision new account & key
              admin-accounts                                List all registered accounts (Admin)
              admin-transactions                            List all transaction logs (Admin)
              balance <accountId>                           Query JOSE-protected account balance
              history <accountId>                           Query JOSE-protected user transaction history
              transfer <fromAccount> <toAccount> <amount>   Execute JOSE-protected secure transfer
            """);
    }
}
