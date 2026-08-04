package hoge.exp.jose.common.model;

public record TransferRequest(
    String toAccount,
    long amount
) {}
