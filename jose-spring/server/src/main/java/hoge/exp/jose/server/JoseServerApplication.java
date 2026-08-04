package hoge.exp.jose.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "hoge.exp.jose")
public class JoseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoseServerApplication.class, args);
    }
}
