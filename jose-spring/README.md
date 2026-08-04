# JOSE Secure Banking System (Spring Boot 4)

A secure banking account management and funds transfer REST API & CLI client application built with **Spring Boot 4 / Java 25**, leveraging the **Nimbus JOSE+JWT** library, Bouncy Castle cryptography, and a NIST SP 800-90A compliant **256-bit DRBG** (CSPRNG).

---

## Key Features & Tech Stack

1. **Runtime Environment**: Java 25 (OpenJDK 25) / Spring Boot **4.1** (Tomcat 11)
2. **JWS (JSON Web Signature)**: `Ed25519` (EdDSA) for sender authentication and payload tamper resistance.
3. **JWE (JSON Web Encryption)**: `X25519` (ECDH-ES+A256KW) + `AES-256-GCM` for bidirectional payload encryption (Request & Response).
4. **Anti-Replay Attack Protection**:
   - Embeds standard `iat` (Issued At), `exp` (Expiration Time: 60s), and unique `jti` (UUID) claims into JWS tokens.
   - Server-side in-memory `jti` cache auto-detects and rejects expired or duplicate replayed requests.
5. **Strict Security Policies (No Silent Fallbacks)**:
   - **Mandatory `kid` Header**: Key ID required in JWS headers for unambiguous sender key lookup.
   - **Algorithm Confusion Attack Defense**: Explicit pre-validation of expected JWS (`EdDSA`) and JWE (`ECDH-ES+A256KW` / `A256GCM`) algorithms.
   - **Explicit Exception Throwing**: Throws `SecurityException` on verification or decryption failures without silent swallowing.
   - **Enforced DRBG Policy**: Fails startup (`IllegalStateException`) if NIST DRBG is unavailable, avoiding silent CSPRNG downgrades.
6. **HTTP Media Type**: RFC 7516 compliant **`application/jose`** content type.
7. **Cryptography Dependencies**:
   - `com.nimbusds:nimbus-jose-jwt`
   - `org.bouncycastle:bcprov-jdk18on`
   - `com.google.crypto.tink:tink` (Required internally by Nimbus for Ed25519Signer/Verifier dynamic loading)
8. **NIST DRBG (CSPRNG)**: NIST SP 800-90A 256-bit SecureRandom supplier supporting `PR_AND_RESEED`.
9. **Multi-Project Architecture**: Core cryptographic utilities (`crypto`) and DTO models (`model`) shared via the `common` subproject.

---

## Subproject Structure

* **`common`**: Shared cryptographic services (`JwsService`, `JweService`, `KeyPairGeneratorUtil`, `DrbgRandomSupplier`) and DTO record classes.
* **`server`**: Spring Boot REST API application (`ServerJoseService`, `AccountController`, `TransferController`, `AdminController`, `JwksController`).
* **`client`**: REST client (`BankApiClient`), CLI runner (`JoseClientRunner`), JOSE flow visualizer (`JoseVisualizer`), and local key storage (`KeyStorageService`).

---

## Getting Started & Usage

### 1. Launching the Server

```bash
./gradlew :server:bootRun
```

The server starts on `http://localhost:8080` and exposes its public JWKS (X25519 for encryption and Ed25519 for signature verification) at `/.well-known/jwks.json`.

### 2. Running the Client

#### Execute End-to-End Demo (Registration -> Balance -> Transfer -> Balance -> History)

```bash
./gradlew :client:bootRun
```

#### Running Specific Subcommands

```bash
# Register a new account & generate key set
./gradlew :client:bootRun --args="admin-register Alice 1000000"

# Query JOSE-protected account balance
./gradlew :client:bootRun --args="balance Alice"

# Execute JOSE-protected transfer (Alice -> Bob: 500,000)
./gradlew :client:bootRun --args="transfer Alice Bob 500000"

# Query JOSE-protected user transaction history
./gradlew :client:bootRun --args="history Alice"

# Admin: List all accounts or transaction logs
./gradlew :client:bootRun --args="admin-accounts"
./gradlew :client:bootRun --args="admin-transactions"
```

---

## Sample Console Outputs (JOSE Flow Visualization)

### 1. Account Balance Inquiry (`balance`)

```text
================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/accounts/balance Request
================================================================================
1. [Plaintext JSON Payload]
   {}

2. [JWS Signature (EdDSA with Ed25519)]
   ・JWS Header  : {kid=Alice-sig, typ=JOSE, alg=EdDSA}
   ・JWS Payload : {data={}, exp=1785852849, iat=1785852789, jti=ac62dfb1-eb65-4b66-b42d-cedbc5312e51}
   ・JWS String (3 Parts: Header.Payload.Signature):
     eyJraWQiOiJBbGljZS1zaWciLCJ0eXAiOiJKT1NFIiwiYWxnIjoiRWREU0EifQ.eyJkYXRhIjoie30iLCJleHAiOjE3ODU4NTI4NDksImlhdCI6MTc4NTg1Mjc4OSwianRpIjoiYWM2MmRmYjEtZWI2NS00YjY2LWI0MmQtY2VkYmM1MzEyZTUxIn0...

3. [JWE Encryption (ECDH-ES+A256KW + A256GCM)]
   ・JWE Header (includes Ephemeral Public Key 'epk'):
     {epk={kty=OKP, crv=X25519, x=QZYgDmiCNNnnxGWNcthDGcNJ_tDDL5KxB9LDwxp5qio}, kid=server-x25519-key, cty=JOSE, enc=A256GCM, alg=ECDH-ES+A256KW}
   ・JWE 5 Parts Breakdown:
     [Part 1: Header]       eyJlcGsiOnsia3R5IjoiT0tQIiwiY3J2IjoiWDI1NTE5I... (len=219)
     [Part 2: EncryptedKey] mqsmBdSdczJ1Ult75HkXq2p2rhfwfwBr3OAy-aA93ovLh... (len=54)
     [Part 3: IV]           SUTfW0MDjQqwF0Q_
     [Part 4: Ciphertext]   fhwXPtwBLX0uRXPAmCYvpuMGQZ71EtRD28Tzo3ol3S4Wf... (len=364)
     [Part 5: Auth Tag]     6IPfEEJ7746ricMJsRz54w

================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/accounts/balance Response
================================================================================
1. [Received HTTP Response Body (Raw JWE Compact String)]
   eyJlcGsiOnsia3R5IjoiT0tQIiwiY3J2IjoiWDI1NTE5IiwieCI6ImxET3RPbVJYcHNFN09tSXdkS2RocEdBSjFlN2ZTSE1nYUx5eU9HMWRkaXMifSwia2lkIjoiQWxpY2UtZW5jIiwiY3R5IjoiSk9TRSIsImVuYyI6IkEyNTZHQ00iLCJhbGciOiJFQ0RILUVTK0EyNTZLVyJ9...

2. [JWE Decryption Breakdown (Server -> Client)]
   ・JWE Header : {epk={kty=OKP, crv=X25519, x=lDOtOmRXpsE7OmIwdKdhpGAJ1e7fSHMgaLyyOG1ddis}, kid=Alice-enc, cty=JOSE, enc=A256GCM, alg=ECDH-ES+A256KW}

3. [JWS Signature Verification (Server Signature)]
   ・Server JWS Header  : {kid=server-ed25519-key, typ=JOSE, alg=EdDSA}
   ・Server JWS Payload : {data={"accountId":"Alice","balance":1000000,"timestamp":"2026-08-04T14:13:09.432825Z"}, exp=1785852849, iat=1785852789, jti=e50c4c64-204c-421a-bf88-f2473936e91c}
   ・JWS Verification Result: SUCCESS (Signed by Server)

4. [Decrypted Response Plaintext JSON]
   {"accountId":"Alice","balance":1000000,"timestamp":"2026-08-04T14:13:09.432825Z"}
```

---

### 2. Secure Funds Transfer (`transfer`)

```text
================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/transfer Request
================================================================================
1. [Plaintext JSON Payload]
   {"toAccount":"Bob","amount":500000}

2. [JWS Signature (EdDSA with Ed25519)]
   ・JWS Header  : {kid=Alice-sig, typ=JOSE, alg=EdDSA}
   ・JWS Payload : {data={"toAccount":"Bob","amount":500000}, exp=1785852849, iat=1785852789, jti=9b411a29-96e8-4edb-a0cf-ffb1f990bd37}
   ・JWS String (3 Parts: Header.Payload.Signature):
     eyJraWQiOiJBbGljZS1zaWciLCJ0eXAiOiJKT1NFIiwiYWxnIjoiRWREU0EifQ.eyJkYXRhIjoie1widG9BY2NvdW50XCI6XCJCb2JcIixcImFtb3VudFwiOjUwMDAwMH0iLCJleHAiOjE3ODU4NTI4NDksImlhdCI6MTc4NTg1Mjc4OSwianRpIjoiOS4uLiJ9...

3. [JWE Encryption (ECDH-ES+A256KW + A256GCM)]
   ・JWE Header (includes Ephemeral Public Key 'epk'):
     {epk={kty=OKP, crv=X25519, x=...}, kid=server-x25519-key, cty=JOSE, enc=A256GCM, alg=ECDH-ES+A256KW}

================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/transfer Response
================================================================================
1. [Received HTTP Response Body (Raw JWE Compact String)]
   ...

2. [JWE Decryption Breakdown (Server -> Client)]
   ・JWE Header : {epk={kty=OKP, crv=X25519, x=...}, kid=Alice-enc, cty=JOSE, enc=A256GCM, alg=ECDH-ES+A256KW}

3. [JWS Signature Verification (Server Signature)]
   ・Server JWS Header  : {kid=server-ed25519-key, typ=JOSE, alg=EdDSA}
   ・Server JWS Payload : {data={"amountTransferred":500000,"fromAccount":"Alice","fromNewBalance":500000,"status":"SUCCESS","timestamp":"2026-08-04T14:13:09.432825Z","toAccount":"Bob","transactionId":"tx-be9d00cc"}, exp=1785852849, iat=1785852789, jti=fa6ea7b0-2818-4a57-a778-1c593a4f8d20}
   ・JWS Verification Result: SUCCESS (Signed by Server)

4. [Decrypted Response Plaintext JSON]
   {"amountTransferred":500000,"fromAccount":"Alice","fromNewBalance":500000,"status":"SUCCESS","timestamp":"2026-08-04T14:13:09.432825Z","toAccount":"Bob","transactionId":"tx-be9d00cc"}
```

---

### 3. User Transaction History Inquiry (`history`)

```text
================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/accounts/transactions Request
================================================================================
1. [Plaintext JSON Payload]
   {}

2. [JWS Signature (EdDSA with Ed25519)]
   ・JWS Header  : {kid=Alice-sig, typ=JOSE, alg=EdDSA}
   ・JWS Payload : {data={}, exp=1785852849, iat=1785852789, jti=ac62dfb1-eb65-4b66-b42d-cedbc5312e51}

================================================================================
 [JOSE CRYPTO FLOW] HTTP POST /api/accounts/transactions Response
================================================================================
1. [Received HTTP Response Body (Raw JWE Compact String)]
   ...

2. [JWE Decryption Breakdown (Server -> Client)]
   ...

3. [JWS Signature Verification (Server Signature)]
   ・Server JWS Header  : {kid=server-ed25519-key, typ=JOSE, alg=EdDSA}
   ・Server JWS Payload : {data=[{"amount":500000,"fromAccount":"Alice","status":"SUCCESS","timestamp":"2026-08-04T14:13:09.432825Z","toAccount":"Bob","transactionId":"tx-be9d00cc"}], exp=1785852849, iat=1785852789, jti=e50c4c64-204c-421a-bf88-f2473936e91c}
   ・JWS Verification Result: SUCCESS (Signed by Server)

4. [Decrypted Response Plaintext JSON]
   [{"amount":500000,"fromAccount":"Alice","status":"SUCCESS","timestamp":"2026-08-04T14:13:09.432825Z","toAccount":"Bob","transactionId":"tx-be9d00cc"}]
```
