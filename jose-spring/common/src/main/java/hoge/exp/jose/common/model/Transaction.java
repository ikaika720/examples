package hoge.exp.jose.common.model;

public record Transaction(
    String transactionId,
    String fromAccount,
    String toAccount,
    long amount,
    String timestamp,
    String status
) {}
