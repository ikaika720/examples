package com.example.cspsri.service;

import com.example.cspsri.model.Post;
import com.example.cspsri.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void initSampleData() {
        if (postRepository.count() == 0) {
            savePost("Alice", "Welcome to Secure Message Board", "This application is protected with Strict Nonce CSP, SRI, and Single-layer HTML Escaping.");
            savePost("Bob", "Safe Text Rendering Test", "Inputs like <b>hello</b> or <script>alert(1)</script> are safely rendered as literal text nodes without executing scripts.");
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post savePost(String author, String title, String content) {
        Post post = Post.of(author, title, content);
        return postRepository.save(post);
    }

    public void clearAll() {
        postRepository.deleteAll();
    }
}
