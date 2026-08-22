package com.example.cspsri;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceMinificationTests {

    private String computeSha384(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-384");
        byte[] hash = digest.digest(bytes);
        return "sha384-" + Base64.getEncoder().encodeToString(hash);
    }

    @Test
    @DisplayName("Verify static CSS is minified and matches template SRI hash")
    void verifyCssMinificationAndSriHash() throws Exception {
        ClassPathResource cssResource = new ClassPathResource("static/css/style.css");
        assertThat(cssResource.exists()).isTrue();

        byte[] cssBytes;
        try (InputStream in = cssResource.getInputStream()) {
            cssBytes = in.readAllBytes();
        }
        String cssContent = new String(cssBytes, StandardCharsets.UTF_8);

        // Verify minification: single line or absence of block comments and extra indentations
        assertThat(cssContent).doesNotContain("/*");
        assertThat(cssContent.lines().count()).isLessThanOrEqualTo(3);

        String computedCssHash = computeSha384(cssBytes);

        // Verify template integrity
        ClassPathResource htmlResource = new ClassPathResource("templates/main.html");
        assertThat(htmlResource.exists()).isTrue();

        String htmlContent;
        try (InputStream in = htmlResource.getInputStream()) {
            htmlContent = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Must not contain unreplaced placeholders
        assertThat(htmlContent).doesNotContain("@STYLE_SRI_HASH@");

        // Must contain computed hash in <link rel="stylesheet"> and <link rel="preload">
        Pattern linkPattern = Pattern.compile("<link[^>]+href=\"/css/style\\.css\"[^>]+integrity=\"([^\"]+)\"");
        Matcher matcher = linkPattern.matcher(htmlContent);
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(1)).isEqualTo(computedCssHash);
    }

    @Test
    @DisplayName("Verify static JS is minified and matches template SRI hash")
    void verifyJsMinificationAndSriHash() throws Exception {
        ClassPathResource jsResource = new ClassPathResource("static/js/app.js");
        assertThat(jsResource.exists()).isTrue();

        byte[] jsBytes;
        try (InputStream in = jsResource.getInputStream()) {
            jsBytes = in.readAllBytes();
        }
        String jsContent = new String(jsBytes, StandardCharsets.UTF_8);

        // Verify minification: comments stripped and condensed
        assertThat(jsContent).doesNotContain("// Trusted Types policy");
        assertThat(jsContent).doesNotContain("// 1. Payload test buttons");

        String computedJsHash = computeSha384(jsBytes);

        // Verify template integrity
        ClassPathResource htmlResource = new ClassPathResource("templates/main.html");
        assertThat(htmlResource.exists()).isTrue();

        String htmlContent;
        try (InputStream in = htmlResource.getInputStream()) {
            htmlContent = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Must not contain unreplaced placeholders
        assertThat(htmlContent).doesNotContain("@SCRIPT_SRI_HASH@");

        // Must contain computed hash in <script src="/js/app.js">
        Pattern scriptPattern = Pattern.compile("<script[^>]+src=\"/js/app\\.js\"[^>]+integrity=\"([^\"]+)\"");
        Matcher matcher = scriptPattern.matcher(htmlContent);
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.group(1)).isEqualTo(computedJsHash);
    }
}
