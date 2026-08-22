package com.example.cspsri.controller;

import com.example.cspsri.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/", "/home"})
    public String index(Model model, HttpServletRequest request) {
        Object nonce = request.getAttribute("cspNonce");
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("cspNonce", nonce != null ? nonce.toString() : "");
        return "main";
    }

    @PostMapping("/posts")
    public String createPost(
            @RequestParam(required = false) @Size(max = 255) String author,
            @RequestParam(required = false) @Size(max = 255) String title,
            @RequestParam(required = false) @Size(max = 10000) String content) {
        postService.savePost(author, title, content);
        return "redirect:/";
    }

    @PostMapping("/api/posts/clear")
    @ResponseBody
    public Map<String, String> clearPosts() {
        postService.clearAll();
        return Map.of("status", "ok");
    }
}
