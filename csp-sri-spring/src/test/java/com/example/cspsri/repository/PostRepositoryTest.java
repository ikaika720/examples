package com.example.cspsri.repository;

import com.example.cspsri.AbstractPostgreSqlTest;
import com.example.cspsri.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest extends AbstractPostgreSqlTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Should persist posts to PostgreSQL and verify raw table rows and ordering via direct SQL queries")
    void saveAndQueryPostsInOrder() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "posts");

        Post olderPost = new Post(null, "Alice", "First Post", "Hello World", LocalDateTime.now().minusMinutes(10));
        Post newerPost = new Post(null, "Bob", "Second Post", "Latest News", LocalDateTime.now());

        Post savedOlder = postRepository.save(olderPost);
        postRepository.save(newerPost);

        // Directly verify table count in PostgreSQL using Spring's JdbcTestUtils
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isEqualTo(2);

        // Directly inspect database table columns using JdbcClient
        Map<String, Object> olderRow = jdbcClient.sql("SELECT author, title, content, created_at FROM posts WHERE id = ?")
                .params(savedOlder.id())
                .query()
                .singleRow();

        assertThat(olderRow.get("author")).isEqualTo("Alice");
        assertThat(olderRow.get("title")).isEqualTo("First Post");
        assertThat(olderRow.get("content")).isEqualTo("Hello World");
        assertThat(olderRow.get("created_at")).isNotNull();

        // Verify repository ordering matches direct SQL ordering
        List<String> titlesFromDb = jdbcClient.sql("SELECT title FROM posts ORDER BY created_at DESC")
                .query(String.class)
                .list();

        List<Post> postsFromRepo = postRepository.findAllByOrderByCreatedAtDesc();

        assertThat(postsFromRepo).extracting(Post::title)
                .containsExactlyElementsOf(titlesFromDb)
                .containsExactly("Second Post", "First Post");
    }

    @Test
    @DisplayName("Post.of static factory should apply default values for null or blank fields")
    void postFactoryDefaultValues() {
        Post defaultPost = Post.of("", "   ", null);

        assertThat(defaultPost.author()).isEqualTo("Anonymous");
        assertThat(defaultPost.title()).isEqualTo("Untitled");
        assertThat(defaultPost.content()).isEmpty();
        assertThat(defaultPost.createdAt()).isNotNull();

        Post customPost = Post.of("Carol", "Custom Title", "Some Content");
        assertThat(customPost.author()).isEqualTo("Carol");
        assertThat(customPost.title()).isEqualTo("Custom Title");
        assertThat(customPost.content()).isEqualTo("Some Content");
    }

    @Test
    @DisplayName("deleteAll should remove all posts from database (verified by JdbcTestUtils)")
    void deleteAllPosts() {
        // Direct SQL insert to populate database independently
        jdbcTemplate.update("INSERT INTO posts (author, title, content, created_at) VALUES (?, ?, ?, ?)",
                "DirectInsert", "Direct Title", "Direct Content", LocalDateTime.now());
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isGreaterThanOrEqualTo(1);

        postRepository.deleteAll();

        // Directly verify table is empty using Spring's JdbcTestUtils
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "posts")).isZero();
    }
}
