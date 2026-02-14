package top.leonam.hotbctgamess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;

@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
@EnableScheduling
public class HotbctGamessApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotbctGamessApplication.class, args);
    }

    @Bean
    public Random random() {
        return new Random();
    }

}
