package kz.ne.railways.tezcustoms.service;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableJpaRepositories({"kz.ne.railways.tezcustoms.service"})
public class TezCustomsServiceApp {
    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(TezCustomsServiceApp.class);
        springApplication.setAddCommandLineProperties(false);

        springApplication.setBannerMode(Banner.Mode.CONSOLE);

        ApplicationContext applicationContext = springApplication.run(args);
        applicationContext.getId();
    }
}
