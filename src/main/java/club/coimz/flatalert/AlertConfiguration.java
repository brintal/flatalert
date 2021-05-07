package club.coimz.flatalert;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "execution.alerts")
@Getter
@Setter
public class AlertConfiguration {

    private String token;
    private Long chatId;

}
