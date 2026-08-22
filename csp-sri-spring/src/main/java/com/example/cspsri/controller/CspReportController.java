package com.example.cspsri.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class CspReportController {

    private static final Logger log = LoggerFactory.getLogger(CspReportController.class);
    private final ObjectMapper objectMapper;

    public CspReportController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/csp-report", consumes = {
            "application/reports+json",
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<Void> receiveReport(@RequestBody String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            if (root.isArray()) {
                for (JsonNode item : root) {
                    JsonNode body = item.has("body") ? item.get("body") : item;
                    logViolation(body);
                }
            } else {
                JsonNode reportNode = root.has("csp-report") ? root.get("csp-report") : root;
                logViolation(reportNode);
            }
        } catch (Exception e) {
            log.warn("[CSP VIOLATION DETECTED] Raw Body: {}", rawBody);
        }
        return ResponseEntity.ok().build();
    }

    private void logViolation(JsonNode reportNode) {
        String blockedUri = getTextOrDefault(reportNode, "blocked-uri", "blockedURL", "blockedURI");
        String violatedDirective = getTextOrDefault(reportNode, "violated-directive", "effectiveDirective", "violatedDirective");
        String documentUri = getTextOrDefault(reportNode, "document-uri", "documentURL", "documentURI");

        // Output to server-side standard logger (SLF4J / Console Log)
        log.warn("[CSP VIOLATION DETECTED] Blocked URI: '{}', Violated Directive: '{}', Document: '{}'",
                blockedUri, violatedDirective, documentUri);
    }

    private String getTextOrDefault(JsonNode node, String... fieldNames) {
        for (String field : fieldNames) {
            if (node.has(field)) {
                return node.get(field).asString();
            }
        }
        return "-";
    }
}
