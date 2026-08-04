package hoge.exp.jose.server.model;

import com.nimbusds.jose.jwk.OctetKeyPair;

import java.util.concurrent.atomic.AtomicLong;

public class Account {

    private final String accountId;
    private final OctetKeyPair publicKeyJwk;
    private final OctetKeyPair encryptionKeyJwk;
    private final AtomicLong balance;

    public Account(String accountId, OctetKeyPair publicKeyJwk, OctetKeyPair encryptionKeyJwk, long initialBalance) {
        this.accountId = accountId;
        this.publicKeyJwk = publicKeyJwk;
        this.encryptionKeyJwk = encryptionKeyJwk;
        this.balance = new AtomicLong(initialBalance);
    }

    public String getAccountId() {
        return accountId;
    }

    public OctetKeyPair getPublicKeyJwk() {
        return publicKeyJwk;
    }

    public OctetKeyPair getEncryptionKeyJwk() {
        return encryptionKeyJwk;
    }

    public long getBalance() {
        return balance.get();
    }

    public synchronized boolean transferTo(Account target, long amount) {
        if (amount <= 0 || balance.get() < amount) {
            return false;
        }
        balance.addAndGet(-amount);
        target.balance.addAndGet(amount);
        return true;
    }
}
