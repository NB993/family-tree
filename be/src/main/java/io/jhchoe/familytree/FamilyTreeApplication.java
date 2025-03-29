package io.jhchoe.familytree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class FamilyTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyTreeApplication.class, args);
    }

}
