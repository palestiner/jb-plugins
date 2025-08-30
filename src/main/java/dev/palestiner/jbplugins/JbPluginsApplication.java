package dev.palestiner.jbplugins;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JbPluginsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(JbPluginsApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}
