package com.example.cspsri.config;

import com.example.cspsri.service.CspStateService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CspFilter implements Filter {

    private final CspStateService cspStateService;

    public CspFilter(CspStateService cspStateService) {
        this.cspStateService = cspStateService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String requestURI = httpRequest.getRequestURI();

            // All responses: Prevent MIME sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");

            if (isStaticResource(requestURI)) {
                // Static resources (CSS/JS etc.): CORP only and long-term caching
                httpResponse.setHeader("Cross-Origin-Resource-Policy", "same-origin");
                httpResponse.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            } else {
                // HTML documents only: Execution environment and form-action protection
                String nonce = cspStateService.generateNonce();
                httpRequest.setAttribute("cspNonce", nonce);

                String policy = String.format(
                        "default-src 'none'; script-src 'nonce-%s' 'strict-dynamic'; style-src 'self'; font-src 'self'; img-src 'self' data:; connect-src 'self'; form-action 'self'; base-uri 'none'; object-src 'none'; frame-src 'none'; frame-ancestors 'none'; require-trusted-types-for 'script'; trusted-types app-policy; report-to csp-endpoint",
                        nonce
                );
                httpResponse.setHeader("Content-Security-Policy", policy);
                httpResponse.setHeader("Reporting-Endpoints", "csp-endpoint=\"/api/csp-report\"");
                httpResponse.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                httpResponse.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                httpResponse.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()");
                httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticResource(String uri) {
        return uri.startsWith("/css/") || uri.startsWith("/js/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".ico") || uri.endsWith(".svg");
    }
}
