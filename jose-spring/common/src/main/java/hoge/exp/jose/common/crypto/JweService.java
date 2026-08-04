package hoge.exp.jose.common.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.X25519Decrypter;
import com.nimbusds.jose.crypto.X25519Encrypter;
import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class JweService {

    private final DrbgRandomSupplier drbgRandomSupplier;

    public JweService(DrbgRandomSupplier drbgRandomSupplier) {
        this.drbgRandomSupplier = drbgRandomSupplier;
    }

    public String encrypt(String payloadString, OctetKeyPair recipientPublicJwk) {
        try {
            JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.ECDH_ES_A256KW, EncryptionMethod.A256GCM)
                    .keyID(recipientPublicJwk.getKeyID())
                    .contentType("JOSE")
                    .build();

            JWEObject jweObject = new JWEObject(header, new Payload(payloadString));
            X25519Encrypter encrypter = new X25519Encrypter(recipientPublicJwk.toPublicJWK());
            encrypter.getJCAContext().setSecureRandom(drbgRandomSupplier.getSecureRandom());
            jweObject.encrypt(encrypter);
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to encrypt JWE payload", e);
        }
    }

    public String decrypt(String jweCompact, OctetKeyPair recipientPrivateKeyPair) {
        try {
            JWEObject jweObject = JWEObject.parse(jweCompact);
            JWEHeader header = jweObject.getHeader();

            // Explicit Algorithm & Encryption Method Verification (Algorithm confusion defense)
            if (!JWEAlgorithm.ECDH_ES_A256KW.equals(header.getAlgorithm()) ||
                !EncryptionMethod.A256GCM.equals(header.getEncryptionMethod())) {
                throw new SecurityException("Unexpected JWE algorithm/enc method: " + header.getAlgorithm() + " / " + header.getEncryptionMethod());
            }

            X25519Decrypter decrypter = new X25519Decrypter(recipientPrivateKeyPair);
            jweObject.decrypt(decrypter);
            return jweObject.getPayload().toString();
        } catch (ParseException | JOSEException e) {
            throw new IllegalStateException("Failed to decrypt JWE compact string", e);
        }
    }
}
