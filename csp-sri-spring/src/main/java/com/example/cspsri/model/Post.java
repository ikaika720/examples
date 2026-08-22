package com.example.cspsri.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("posts")
public record Post(
        @Id Long id,
        String author,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static Post of(String author, String title, String content) {
        return new Post(
                null,
                author == null || author.isBlank() ? "Anonymous" : author,
                title == null || title.isBlank() ? "Untitled" : title,
                content == null ? "" : content,
                LocalDateTime.now()
        );
    }
}
