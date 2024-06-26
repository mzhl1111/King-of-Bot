package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("pergou"));
        System.out.println(passwordEncoder.matches("pergou",
                "$2a$10$K2a0ZYuGnacwyle56B1bPOBArhk..cJZh/7V0HZE3wt/FbihafLVy"));
    }

}
