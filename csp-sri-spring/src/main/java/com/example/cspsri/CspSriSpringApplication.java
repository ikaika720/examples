package com.example.cspsri;

import com.example.cspsri.service.PostService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CspSriSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CspSriSpringApplication.class, args);
    }

    @Bean
    public ApplicationRunner initData(PostService postService) {
        return args -> postService.initSampleData();
    }
}
