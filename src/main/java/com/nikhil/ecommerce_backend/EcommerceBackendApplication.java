package com.nikhil.ecommerce_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableScheduling
public class EcommerceBackendApplication {

	public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        System.setProperty("MAIL_USERNAME", dotenv.get("SPRING_MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("APP_BASE_URL", dotenv.get("APP_BASE_URL"));
        System.setProperty("FILE_UPLOAD_DIR", dotenv.get("FILE_UPLOAD_DIR"));
        System.setProperty("ADMIN_MAIL", dotenv.get("ADMIN_MAIL"));
        System.setProperty("ADMIN_PASSWORD", dotenv.get("ADMIN_PASSWORD"));

		SpringApplication.run(EcommerceBackendApplication.class, args);

	}

}
