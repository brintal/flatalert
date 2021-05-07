package club.coimz.flatalert;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "coops")
@Getter
@Setter
public class CoopsConfiguration {

    private List<CoopConfig> configs;

}
