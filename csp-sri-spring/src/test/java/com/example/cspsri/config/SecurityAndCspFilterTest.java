package com.example.cspsri.config;

import com.example.cspsri.AbstractPostgreSqlTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SecurityAndCspFilterTest extends AbstractPostgreSqlTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("HTML page request should receive Strict Nonce CSP and robust security headers")
    void htmlRequestContainsStrictSecurityHeaders() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin"))
                .andExpect(header().string("Cross-Origin-Embedder-Policy", "require-corp"))
                .andExpect(header().string("Reporting-Endpoints", "csp-endpoint=\"/api/csp-report\""))
                .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")))
                .andExpect(header().string("Content-Security-Policy", containsString("script-src 'nonce-")))
                .andExpect(header().string("Content-Security-Policy", containsString("'strict-dynamic'")))
                .andExpect(header().string("Content-Security-Policy", containsString("require-trusted-types-for 'script'")))
                .andExpect(header().string("Content-Security-Policy", containsString("trusted-types app-policy")))
                .andExpect(header().string("Content-Security-Policy", containsString("report-to csp-endpoint")));
    }

    @Test
    @DisplayName("Static resource request should receive CORP and immutable cache control")
    void staticResourceRequestContainsCorpAndCacheHeaders() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"))
                .andExpect(header().string("Cache-Control", "public, max-age=31536000, immutable"))
                .andExpect(header().doesNotExist("Content-Security-Policy"));
    }

    @Test
    @DisplayName("CSP report endpoint should accept standard CSP JSON without CSRF token")
    void cspReportAcceptsStandardReportWithoutCsrf() throws Exception {
        String legacyReportJson = """
                {
                    "csp-report": {
                        "document-uri": "https://localhost:8443/",
                        "blocked-uri": "inline",
                        "violated-directive": "script-src 'nonce-...'"
                    }
                }
                """;

        mockMvc.perform(post("/api/csp-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(legacyReportJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CSP report endpoint should accept Reporting API reports+json format without CSRF token")
    void cspReportAcceptsReportingApiFormatWithoutCsrf() throws Exception {
        String reportingApiJson = """
                [
                    {
                        "type": "csp-violation",
                        "url": "https://localhost:8443/",
                        "body": {
                            "documentURL": "https://localhost:8443/",
                            "blockedURL": "https://evil.com/payload.js",
                            "effectiveDirective": "script-src-elem"
                        }
                    }
                ]
                """;

        mockMvc.perform(post("/api/csp-report")
                        .contentType("application/reports+json")
                        .content(reportingApiJson))
                .andExpect(status().isOk());
    }
}
