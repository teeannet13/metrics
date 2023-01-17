package com.example.metrics;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
public class MetricsApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(MetricsApplication.class, args);
    }

//    @Bean
//    @Primary
//    @Profile("dev")
//    public DataSource inMemoryDS() throws Exception {
//        DataSource embeddedPostgresDS = EmbeddedPostgres.builder()
//                .start().getPostgresDatabase();
//
//        return embeddedPostgresDS;
//    }

}
