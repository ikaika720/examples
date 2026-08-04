package hoge.exp.jose.server.config;

import com.nimbusds.jose.jwk.OctetKeyPair;
import hoge.exp.jose.common.crypto.KeyPairGeneratorUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerCryptoConfig {

    @Bean
    public OctetKeyPair serverX25519KeyPair(KeyPairGeneratorUtil keyPairGeneratorUtil) {
        OctetKeyPair keyPair = keyPairGeneratorUtil.generateX25519KeyPair("server-x25519-key");
        System.out.println("[SERVER CRYPTO] Generated X25519 Encryption Key Pair. Public JWK: " + keyPair.toPublicJWK().toJSONString());
        return keyPair;
    }

    @Bean
    public OctetKeyPair serverEd25519KeyPair(KeyPairGeneratorUtil keyPairGeneratorUtil) {
        OctetKeyPair keyPair = keyPairGeneratorUtil.generateEd25519KeyPair("server-ed25519-key");
        System.out.println("[SERVER CRYPTO] Generated Ed25519 Signature Key Pair. Public JWK: " + keyPair.toPublicJWK().toJSONString());
        return keyPair;
    }
}
