package hoge.exp.jose.common.model;

public record AdminRegisterRequest(
    String accountId,
    String publicKeyJwkJson,
    String encryptionKeyJwkJson,
    long initialBalance
) {}
