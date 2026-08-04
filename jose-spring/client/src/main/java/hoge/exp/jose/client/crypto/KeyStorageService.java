package hoge.exp.jose.client.crypto;

import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class KeyStorageService {

    public record ClientKeySet(OctetKeyPair signatureKeyPair, OctetKeyPair encryptionKeyPair) {}

    private final Path keysDirectory;

    public KeyStorageService() {
        this.keysDirectory = Paths.get("keys");
        try {
            if (!Files.exists(keysDirectory)) {
                Files.createDirectories(keysDirectory);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize keys directory: " + keysDirectory.toAbsolutePath(), e);
        }
    }

    public void saveKeySet(String accountId, OctetKeyPair sigKeyPair, OctetKeyPair encKeyPair) {
        try {
            Path sigPath = keysDirectory.resolve(accountId + "-sig.json");
            Path encPath = keysDirectory.resolve(accountId + "-enc.json");

            Files.writeString(sigPath, sigKeyPair.toJSONString());
            Files.writeString(encPath, encKeyPair.toJSONString());
            System.out.println("[CLIENT KEY STORAGE] Saved key set for '" + accountId + "' at " + keysDirectory.toAbsolutePath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save key set for account " + accountId, e);
        }
    }

    public Optional<ClientKeySet> loadKeySet(String accountId) {
        Path sigPath = keysDirectory.resolve(accountId + "-sig.json");
        Path encPath = keysDirectory.resolve(accountId + "-enc.json");

        if (!Files.exists(sigPath) || !Files.exists(encPath)) {
            return Optional.empty();
        }

        try {
            OctetKeyPair sigKey = OctetKeyPair.parse(Files.readString(sigPath));
            OctetKeyPair encKey = OctetKeyPair.parse(Files.readString(encPath));
            return Optional.of(new ClientKeySet(sigKey, encKey));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load or parse key files for account '" + accountId + "'", e);
        }
    }
}
