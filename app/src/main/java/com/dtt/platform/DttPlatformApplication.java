package com.dtt.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dtt", "ug.daes"})
@EntityScan(basePackages = {
        "com.dtt.model",
        "com.dtt.organization.model",
        "ug.daes.onboarding.model",
        "ug.daes.ra.model",
        "ug.daes.ra.request.entity"
})
@EnableJpaRepositories(
        basePackages = {
                "com.dtt.repo",
                "com.dtt.organization.repository",
                "ug.daes.onboarding.repository",
                "ug.daes.ra.repository"
        },
        entityManagerFactoryRef = "entityManagerFactory"
)
public class DttPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DttPlatformApplication.class, args);
    }
}

