package exam_project.artigiani_webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ArtigianiWebappApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ArtigianiWebappApplication.class, args);
          BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
          System.out.println(encoder.encode("$2a$10$WPQYb2unD5Bu6S.wEZJpnes7jnA/0HS0G8O21vHWkrktDuWzOx6YC"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ArtigianiWebappApplication.class);
    }

}
