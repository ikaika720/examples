package hoge.exp.jose.common.crypto;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyPairGeneratorUtil {

    private final DrbgRandomSupplier drbgRandomSupplier;

    public KeyPairGeneratorUtil(DrbgRandomSupplier drbgRandomSupplier) {
        this.drbgRandomSupplier = drbgRandomSupplier;
    }

    public OctetKeyPair generateEd25519KeyPair(String keyId) {
        Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
        generator.init(new Ed25519KeyGenerationParameters(drbgRandomSupplier.getSecureRandom()));
        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();

        Ed25519PublicKeyParameters pub = (Ed25519PublicKeyParameters) keyPair.getPublic();
        Ed25519PrivateKeyParameters priv = (Ed25519PrivateKeyParameters) keyPair.getPrivate();

        String kid = keyId != null ? keyId : "ed25519-sig-" + UUID.randomUUID().toString().substring(0, 8);

        return new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(pub.getEncoded()))
                .d(Base64URL.encode(priv.getEncoded()))
                .keyID(kid)
                .keyUse(KeyUse.SIGNATURE)
                .build();
    }

    public OctetKeyPair generateX25519KeyPair(String keyId) {
        X25519KeyPairGenerator generator = new X25519KeyPairGenerator();
        generator.init(new X25519KeyGenerationParameters(drbgRandomSupplier.getSecureRandom()));
        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();

        X25519PublicKeyParameters pub = (X25519PublicKeyParameters) keyPair.getPublic();
        X25519PrivateKeyParameters priv = (X25519PrivateKeyParameters) keyPair.getPrivate();

        String kid = keyId != null ? keyId : "x25519-enc-" + UUID.randomUUID().toString().substring(0, 8);

        return new OctetKeyPair.Builder(Curve.X25519, Base64URL.encode(pub.getEncoded()))
                .d(Base64URL.encode(priv.getEncoded()))
                .keyID(kid)
                .keyUse(KeyUse.ENCRYPTION)
                .build();
    }
}
