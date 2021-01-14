package social.network;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpringSocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSocialNetworkApplication.class, args);
        log.trace("Приложение запущено!");
    }
}
