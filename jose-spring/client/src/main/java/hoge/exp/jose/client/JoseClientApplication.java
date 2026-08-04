package hoge.exp.jose.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "hoge.exp.jose")
public class JoseClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoseClientApplication.class, args);
    }
}
