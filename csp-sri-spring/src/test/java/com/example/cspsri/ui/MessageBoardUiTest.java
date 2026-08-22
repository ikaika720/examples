package com.example.cspsri.ui;

import com.example.cspsri.AbstractPostgreSqlTest;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageBoardUiTest extends AbstractPostgreSqlTest {

    private static final Path SCREENSHOT_DIR = Paths.get("build", "reports", "screenshots");

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;
    private List<String> consoleErrors;
    private AtomicBoolean dialogTriggered;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        try {
            Files.createDirectories(SCREENSHOT_DIR);
        } catch (IOException ignored) {
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "posts");
        consoleErrors = new ArrayList<>();
        dialogTriggered = new AtomicBoolean(false);

        // Ignore HTTPS certificate errors because local Spring Boot runs with self-signed certificate
        context = browser.newContext(new Browser.NewContextOptions().setIgnoreHTTPSErrors(true));
        page = context.newPage();

        page.onConsoleMessage(msg -> {
            if ("error".equalsIgnoreCase(msg.type())) {
                consoleErrors.add(msg.text());
            }
        });

        page.onDialog(dialog -> {
            dialogTriggered.set(true);
            dialog.dismiss();
        });
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    private String getBaseUrl() {
        return "https://localhost:" + port;
    }

    private void captureScreenshot(String filename) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(SCREENSHOT_DIR.resolve(filename))
                .setFullPage(true));
    }

    @Test
    @DisplayName("Main page loads with 200 OK, valid SRI/CSP assets, and no console errors")
    void mainPageLoadsSuccessfullyWithValidAssets() {
        Response response = page.navigate(getBaseUrl());
        org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);

        // Title and main headers
        assertThat(page).hasTitle("Message Board");
        assertThat(page.locator(".brand-title")).hasText("Message Board");
        assertThat(page.locator(".brand-sub")).hasText("CSP / SRI / CSRF Verification");

        // Verify CSP Header on the HTTP response
        String cspHeader = response.headers().get("content-security-policy");
        org.assertj.core.api.Assertions.assertThat(cspHeader)
                .isNotNull()
                .contains("script-src 'nonce-")
                .contains("'strict-dynamic'");

        // Verify HTML text contains the nonce attribute and SRI attributes
        String html = response.text();
        org.assertj.core.api.Assertions.assertThat(html)
                .contains("nonce=")
                .contains("integrity=\"sha384-");

        // Script and style tags are present with integrity attribute
        Locator scriptTag = page.locator("script[src='/js/app.js']");
        assertThat(scriptTag).hasAttribute("integrity", java.util.regex.Pattern.compile("^sha384-.+"));

        // No CSP / SRI integrity rejection errors or JavaScript errors in the browser console
        org.assertj.core.api.Assertions.assertThat(consoleErrors).isEmpty();

        // Capture screenshot of initial load
        captureScreenshot("01-main-page-loaded.png");
    }

    @Test
    @DisplayName("Create post via form and verify it appears on timeline")
    void submitNewPostDisplaysOnTimeline() {
        page.navigate(getBaseUrl());

        page.locator("input[name='author']").fill("Alice");
        page.locator("input[name='title']").fill("Playwright Test");
        page.locator("textarea[name='content']").fill("Automated browser testing with Playwright.");
        page.locator("button[type='submit']").click();

        // After submit and redirect, verify post is rendered in the timeline
        Locator postItem = page.locator(".post-item").first();
        assertThat(postItem.locator(".post-author")).hasText("Alice");
        assertThat(postItem.locator(".post-title")).hasText("Playwright Test");
        assertThat(postItem.locator(".post-body")).hasText("Automated browser testing with Playwright.");

        // Capture screenshot of timeline with new post
        captureScreenshot("02-new-post-created.png");
    }

    @Test
    @DisplayName("Quick payload buttons populate inputs, and XSS payload is safely rendered without executing script")
    void quickPayloadButtonAndXssSanitization() {
        page.navigate(getBaseUrl());

        // Click "Test script" button (invokes app.js event handler)
        page.locator("button[data-payload='script']").click();

        // Verify inputs are populated by JavaScript
        assertThat(page.locator("#post-title")).hasValue("Script Tag Test");
        assertThat(page.locator("#post-content")).hasValue("<script>alert(\"XSS Test\");</script><p>Post content</p>");

        captureScreenshot("03-payload-input-injected.png");

        // Submit the XSS payload post
        page.locator("button[type='submit']").click();

        // Verify post rendered safely as text without executing alert dialog
        Locator postItem = page.locator(".post-item").first();
        assertThat(postItem.locator(".post-title")).hasText("Script Tag Test");
        assertThat(postItem.locator(".post-body")).hasText("<script>alert(\"XSS Test\");</script><p>Post content</p>");

        // Verify no alert dialog was triggered (CSP & Thymeleaf text escaping prevented execution)
        org.assertj.core.api.Assertions.assertThat(dialogTriggered.get()).isFalse();

        captureScreenshot("04-xss-rendered-safely.png");
    }

    @Test
    @DisplayName("Clear button sends fetch request with CSRF header and empties timeline")
    void clearButtonEmptiesTimeline() {
        page.navigate(getBaseUrl());

        // First, add a post
        page.locator("input[name='title']").fill("To be deleted");
        page.locator("textarea[name='content']").fill("Will be cleared");
        page.locator("button[type='submit']").click();

        assertThat(page.locator(".post-item")).hasCount(1);

        // Click Clear button (triggers fetch to /api/posts/clear with meta CSRF header and reload)
        page.locator("#btn-clear-posts").click();

        // Wait for timeline to be empty
        assertThat(page.locator(".post-item")).hasCount(0);

        captureScreenshot("05-timeline-cleared.png");
    }
}
