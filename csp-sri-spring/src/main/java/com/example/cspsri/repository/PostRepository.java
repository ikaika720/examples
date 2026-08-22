package com.example.cspsri.repository;

import com.example.cspsri.model.Post;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends ListCrudRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
}
