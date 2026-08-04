package hoge.exp.jose.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JwksController {

    private final OctetKeyPair serverX25519KeyPair;
    private final OctetKeyPair serverEd25519KeyPair;
    private final ObjectMapper objectMapper;

    public JwksController(OctetKeyPair serverX25519KeyPair, OctetKeyPair serverEd25519KeyPair) {
        this.serverX25519KeyPair = serverX25519KeyPair;
        this.serverEd25519KeyPair = serverEd25519KeyPair;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJwks() throws Exception {
        JWKSet jwkSet = new JWKSet(List.of(
                serverX25519KeyPair.toPublicJWK(),
                serverEd25519KeyPair.toPublicJWK()
        ));
        return objectMapper.writeValueAsString(jwkSet.toJSONObject());
    }
}
