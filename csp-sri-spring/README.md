# CSP & SRI Secure Message Board (Spring Boot 4 / Java 25)

A reference implementation of modern, defense-in-depth web application security built with **Spring Boot 4 / Java 25**, comprehensively incorporating **Strict Nonce-based Content Security Policy (CSP Level 3)**, automated **Subresource Integrity (SRI)**, **W3C Trusted Types**, **CSRF Protection**, and modern **Cross-Origin / Transport Security Headers**.

This project provides an automated build pipeline (Gradle) that minifies client-side assets (CSS/JS) and computes SHA-384 SRI hashes dynamically injected into Thymeleaf templates, full-stack browser E2E security testing with **Playwright**, containerized PostgreSQL integration tests with **Testcontainers**, and active vulnerability assessments with **OWASP ZAP**.

---

## 🌟 Key Features & Security Architecture

### 1. Strict Nonce-based Content Security Policy (CSP Level 3)
* **Elimination of Domain Whitelists & Dynamic Nonce Generation**:
  * Dynamically generates a cryptographically secure 128-bit (Base64) nonce per request using `SecureRandom`.
  * Enforces `script-src 'nonce-...' 'strict-dynamic'` to trust explicitly authorized scripts and their dynamically loaded dependencies while blocking unauthorized inline scripts and XSS payloads.
* **Directives with Least Privilege Lockdown**:
  * `default-src 'none'`
  * `style-src 'self'`, `font-src 'self'`, `img-src 'self' data:`, `connect-src 'self'`
  * `base-uri 'none'`, `object-src 'none'`, `frame-src 'none'`, `frame-ancestors 'none'`
  * `form-action 'self'` (restricts form submission destinations strictly to the same origin)
* **DOM-based XSS Defense (W3C Trusted Types)**:
  * Declares `require-trusted-types-for 'script'` and `trusted-types app-policy`.
  * Enforces safe DOM sink operations via `globalThis.trustedTypes.createPolicy('app-policy', ...)`.
* **Real-time Violation Telemetry (Dual Reporting API Support)**:
  * `Reporting-Endpoints: csp-endpoint="/api/csp-report"`
  * Ingests and logs both the modern W3C Reporting API format (`application/reports+json`) and the legacy browser CSP report format (`application/json`) at the server level.

### 2. Automated CSS/JS Minification & SRI Hash Pipeline (Gradle)
* **Build-time Optimization (`processResources`)**:
  * **JavaScript**: Strips comments and performs syntax tree optimization via Google Closure Compiler (`SIMPLE_OPTIMIZATIONS`).
  * **CSS**: Compresses redundant whitespace, newlines, and comment blocks.
* **Automated SRI (Subresource Integrity) Injection**:
  * Automatically calculates `SHA-384` cryptographic digests from the minified output files.
  * Replaces template placeholders (`@STYLE_SRI_HASH@`, `@SCRIPT_SRI_HASH@`) in Thymeleaf HTML files at build time.
  * Enables the browser to detect and block tampered assets or corrupted CDN deliveries immediately.

### 3. Defense-in-Depth Security Headers & Transport Hardening
* **Cross-Origin Isolation & Resource Guarding**:
  * `Cross-Origin-Resource-Policy: same-origin` (CORP for static assets)
  * `Cross-Origin-Opener-Policy: same-origin` (COOP - prevents cross-window reference access)
  * `Cross-Origin-Embedder-Policy: require-corp` (COEP - requires explicit resource loading opt-ins)
* **Feature Policy & Information Leak Prevention**:
  * `X-Content-Type-Options: nosniff` (prevents MIME type sniffing attacks)
  * `Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()` (disables unused hardware APIs)
  * `Referrer-Policy: strict-origin-when-cross-origin`
  * `Cache-Control: public, max-age=31536000, immutable` (long-term caching for static assets)
* **Transport Layer & Session Hardening**:
  * **HTTPS / TLS**: SSL/TLS encrypted communication using a PKCS12 keystore (Port 8443).
  * **HTTP/2**: Enabled via `server.http2.enabled: true`.
  * **Response Compression**: Gzip / Brotli compression for static resources and JSON responses.
  * **Cookie Attributes**: Enforces `Secure`, `HttpOnly`, and `SameSite=Lax` on Session and CSRF cookies.

### 4. CSRF Defense & Safe Data Persistence
* **Spring Security CSRF**:
  * Standard Form Submission: Automatically injects CSRF hidden token fields via Thymeleaf `th:action`.
  * AJAX / Fetch API: Reads `_csrf` and `_csrf_header` from `<meta>` tags and attaches the `X-XSRF-TOKEN` header.
* **Spring Data JDBC & PostgreSQL**:
  * Uses parameterized queries and prepared statements to eliminate SQL Injection vectors.

---

## 📁 Project Directory Structure

```
csp-sri-spring/
├── build.gradle.kts          # Closure Compiler, CSS/JS minification, SRI SHA-384 hash computation & injection
├── compose.yaml              # Local PostgreSQL 18 Alpine container configuration
├── zap-full-scan.yaml        # OWASP ZAP Automation Framework configuration (Spider + Active Scan)
├── src/
│   ├── main/
│   │   ├── java/com/example/cspsri/
│   │   │   ├── CspSriSpringApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CspFilter.java          # Security headers filter (CSP, CORP, COOP, COEP, Permissions-Policy)
│   │   │   │   └── SecurityConfig.java     # Spring Security, CSRF token repository & filter chain setup
│   │   │   ├── controller/
│   │   │   │   ├── CspReportController.java # REST endpoint for CSP violation telemetry ingestion
│   │   │   │   └── PostController.java      # Message board UI handler & post operations
│   │   │   ├── model/
│   │   │   │   └── Post.java               # Post data record (Spring Data JDBC)
│   │   │   ├── repository/
│   │   │   │   └── PostRepository.java     # Database repository
│   │   │   └── service/
│   │   │       ├── CspStateService.java    # CSP Nonce generation service (CSPRNG)
│   │   │       └── PostService.java        # Message board business logic
│   │   └── resources/
│   │       ├── application.yml             # HTTPS, HTTP/2, datasource, and compression configuration
│   │       ├── schema.sql                  # PostgreSQL table initialization script
│   │       ├── keystore.p12                # Local development SSL certificate
│   │       ├── static/
│   │       │   ├── css/style.css           # Raw stylesheet (automatically minified during build)
│   │       │   ├── js/app.js               # Client JavaScript (Trusted Types, automatically minified during build)
│   │       │   └── favicon.svg
│   │       └── templates/
│   │           └── main.html               # Thymeleaf template with SRI placeholder tags
│   └── test/
│       └── java/com/example/cspsri/
│           ├── AbstractPostgreSqlTest.java        # Shared Testcontainers PostgreSQL base class
│           ├── CspSriSpringApplicationTests.java  # Application context load test
│           ├── ResourceMinificationTests.java     # CSS/JS minification and SRI hash match verification
│           ├── config/
│           │   └── SecurityAndCspFilterTest.java  # MockMvc security response headers & report endpoint tests
│           ├── controller/
│           │   └── PostControllerIntegrationTest.java # Post submission & CSRF token enforcement tests
│           ├── repository/
│           │   └── PostRepositoryTest.java        # Data persistence integration tests
│           └── ui/
│               └── MessageBoardUiTest.java        # Playwright E2E browser tests (CSP, SRI, XSS, and UI flows)
```

---

## 🚀 Getting Started & Usage

### Prerequisites
* **Java 25** (OpenJDK 25)
* **Docker** or **Colima** (Required for PostgreSQL and Testcontainers)

### 1. Start Database
```bash
docker compose up -d
```

### 2. Build and Launch Application
```bash
./gradlew bootRun
```

### 3. Access in Browser
Open the following URL in your browser:
```
https://localhost:8443
```
> [!NOTE]
> The application uses a local self-signed SSL certificate (`keystore.p12`). When your browser displays a security warning, proceed by clicking **Advanced** -> **Proceed to localhost (unsafe)**.

---

## 🧪 Interactive Security Verification

The web interface includes **Quick Payload Buttons** to test security controls directly:

| Button | Injected Payload / Action | Target Security Control |
| :--- | :--- | :--- |
| **Test script** | `<script>alert("XSS Test");</script><p>Post content</p>` | **CSP Nonce & Strict Dynamic**: Unauthorized `<script>` tags without a valid nonce are completely blocked by the browser. |
| **Test onerror** | `<img src="x" onerror="alert('onerror Test')"><p>Post content</p>` | **CSP Inline Handler Blocking**: Inline event handlers (`onerror`) are blocked from execution by CSP. |
| **Test SQLi** | `'; DROP TABLE posts; -- UNION SELECT 1, ...` | **Parameterized Queries**: Spring Data JDBC binds inputs safely to prevent SQL injection. |
| **Clear** | `fetch('/api/posts/clear', { headers: { 'X-CSRF-TOKEN': ... } })` | **CSRF Header Protection**: Requests without a valid CSRF header are rejected with `403 Forbidden`. |

### Observing CSP Violation Telemetry
When an unauthorized script execution attempt occurs, the browser immediately submits a report to `/api/csp-report`, producing a server log entry:
```text
WARN [CSP VIOLATION DETECTED] Blocked URI: 'inline', Violated Directive: 'script-src-elem', Document: 'https://localhost:8443/'
```

---

## 🛡️ Security Controls Matrix

| Threat Vector / Attack | Mitigation Mechanism | Implementation Details |
| :--- | :--- | :--- |
| **Cross-Site Scripting (XSS)** | CSP Level 3 (`nonce` + `strict-dynamic`) | Blocks all inline and external scripts that lack an authorized nonce |
| **DOM-based XSS** | W3C Trusted Types | `require-trusted-types-for 'script'` restricts raw string assignment into DOM sinks |
| **Asset Tampering / Malicious CDN** | Subresource Integrity (SRI) | Computes `SHA-384` digests at build time and verifies `<link>` / `<script>` tags |
| **Cross-Site Request Forgery (CSRF)**| Spring Security CSRF + SameSite Cookie | `CookieCsrfTokenRepository` (`SameSite=Lax`, `Secure=true`) + form/AJAX header binding |
| **Clickjacking** | CSP `frame-ancestors 'none'` | Disallows framing of the application across all parent origins |
| **MIME Type Sniffing** | `X-Content-Type-Options: nosniff` | Prevents browsers from executing non-script assets as executable code |
| **Cross-Origin Data Leaks** | COOP / COEP / CORP | `Cross-Origin-Opener-Policy`, `Cross-Origin-Embedder-Policy`, `Cross-Origin-Resource-Policy` |
| **Eavesdropping / MITM Attacks** | HTTPS (TLS) + HTTP/2 | Enforces TLS encryption via PKCS12 keystore |
| **SQL Injection** | Spring Data JDBC | Uses parameterized queries across all database operations |

---

## 🧪 Running Automated Tests

Run the full suite of automated tests with:

```bash
./gradlew test
```

### 1. Playwright E2E Browser Testing (`MessageBoardUiTest`)
* Spawns a headless Chromium instance to verify HTTP 200 responses, CSP response headers, exact SRI hash matches (zero console errors), neutralization of XSS payloads, CSRF AJAX interactions, and UI workflows.
* Captures full-page screenshots stored automatically under `build/reports/screenshots/`.

### 2. Resource Minification & SRI Hash Verification (`ResourceMinificationTests`)
* Verifies that `static/css/style.css` and `static/js/app.js` are properly minified and that their computed `SHA-384` digests match the `integrity` attributes in `templates/main.html`.

### 3. Security Headers & Controller Integration Tests
* `SecurityAndCspFilterTest`: Uses `MockMvc` to validate all security response headers and the dual-format CSP reporting endpoint.
* `PostControllerIntegrationTest`: Runs against containerized PostgreSQL (Testcontainers) to test CSRF token enforcement and database state transitions.

---

## 🔍 OWASP ZAP Vulnerability Scanning

An automated full scan configuration (`zap-full-scan.yaml`) is included for the OWASP ZAP Automation Framework, covering Spider crawling, passive scanning, and active attack testing (SQLi, XSS, CSRF, header validation).

```bash
docker run --rm -v $(pwd):/zap/wrk/:rw -t ghcr.io/zaproxy/zaproxy:stable \
  zap.yaml -autorun /zap/wrk/zap-full-scan.yaml
```
The scan results are output to `zap-full-report.html`.
