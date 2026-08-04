package hoge.exp.jose.common.model;

public record BalanceResponse(
    String accountId,
    long balance,
    String timestamp
) {}
