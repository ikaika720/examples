package hoge.exp.jose.common.crypto;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.DrbgParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Component
public class DrbgRandomSupplier {

    private final SecureRandom secureRandom;
    private final String personalizationString;

    public DrbgRandomSupplier() {
        this.personalizationString = buildPersonalizationString();
        this.secureRandom = initDrbg(this.personalizationString);
    }

    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    public String getPersonalizationString() {
        return this.personalizationString;
    }

    private static String buildPersonalizationString() {
        String appName = "JoseApp";
        String purpose = "Crypto-Operations";
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostName = "unknown-host";
        }
        long pid = ProcessHandle.current().pid();
        String nanosecondInstant = Instant.now().toString();
        long nanoTimeJitter = System.nanoTime();

        return String.format("%s:%s:%s:%d:%s:%d",
                appName, purpose, hostName, pid, nanosecondInstant, nanoTimeJitter);
    }

    private static SecureRandom initDrbg(String personalizationStr) {
        try {
            byte[] personalizationBytes = personalizationStr.getBytes(StandardCharsets.UTF_8);

            DrbgParameters.Instantiation drbgParams = DrbgParameters.instantiation(
                    256,
                    DrbgParameters.Capability.PR_AND_RESEED,
                    personalizationBytes
            );

            return SecureRandom.getInstance("DRBG", drbgParams);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("DRBG algorithm (NIST SP 800-90A) is required by security policy but not available", e);
        }
    }
}
