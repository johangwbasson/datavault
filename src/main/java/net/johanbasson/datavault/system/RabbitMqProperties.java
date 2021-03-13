package net.johanbasson.datavault.system;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbitmq")
@Data
@NoArgsConstructor
public class RabbitMqProperties {

    private String username;
    private String password;
    private String virtualHost;
    private String host;
    private int port;
}
