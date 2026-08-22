package com.example.cspsri.controller;

import com.example.cspsri.AbstractPostgreSqlTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class PostControllerIntegrationTest extends AbstractPostgreSqlTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcClient jdbcClient;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("GET / should return 200 OK, render main template, and contain posts and nonce in model")
    void indexPageReturnsMainView() throws Exception {
        // Direct SQL insert to provide baseline data independently
        jdbcTemplate.update("INSERT INTO posts (author, title, content, created_at) VALUES (?, ?, ?, ?)",
                "SqlAuthor", "SqlTitle", "SqlContent", LocalDateTime.now());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("cspNonce"));
    }

    @Test
    @DisplayName("POST /posts with CSRF token should persist row to PostgreSQL table and redirect to /")
    void createPostWithCsrfSavesToDatabase() throws Exception {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "posts");

        mockMvc.perform(post("/posts")
                        .with(csrf())
                        .param("author", "Integration Tester")
                        .param("title", "Integration Test Post")
                        .param("content", "Integration Test Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Directly verify table row count using Spring's JdbcTestUtils
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isEqualTo(1);

        // Directly query table columns using JdbcClient
        Map<String, Object> savedRow = jdbcClient.sql("SELECT author, title, content, created_at FROM posts")
                .query()
                .singleRow();

        assertThat(savedRow.get("author")).isEqualTo("Integration Tester");
        assertThat(savedRow.get("title")).isEqualTo("Integration Test Post");
        assertThat(savedRow.get("content")).isEqualTo("Integration Test Content");
        assertThat(savedRow.get("created_at")).isNotNull();
    }

    @Test
    @DisplayName("POST /posts without CSRF token should be rejected with 403 and leave table unmodified")
    void createPostWithoutCsrfIsForbidden() throws Exception {
        int initialCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts");

        mockMvc.perform(post("/posts")
                        .param("author", "Attacker")
                        .param("title", "CSRF Attack")
                        .param("content", "Malicious content"))
                .andExpect(status().isForbidden());

        // Verify direct table row count has not changed
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isEqualTo(initialCount);
    }

    @Test
    @DisplayName("POST /api/posts/clear with CSRF token should wipe table rows and return JSON ok")
    void clearPostsWithCsrfClearsDatabase() throws Exception {
        // Direct SQL insert
        jdbcTemplate.update("INSERT INTO posts (author, title, content, created_at) VALUES (?, ?, ?, ?)",
                "Alice", "To be cleared", "Message", LocalDateTime.now());
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isGreaterThanOrEqualTo(1);

        mockMvc.perform(post("/api/posts/clear")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        // Directly verify table is empty using Spring's JdbcTestUtils
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isZero();
    }
}
