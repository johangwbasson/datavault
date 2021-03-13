package net.johanbasson.datavault;

import net.johanbasson.datavault.common.ErrorMessage;
import net.johanbasson.datavault.security.AuthenticateRequest;
import net.johanbasson.datavault.security.JwtToken;
import net.johanbasson.datavault.system.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.okhttp3.*;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {ServerTest.Initializer.class})
public class ServerTest {

    @LocalServerPort
    private int port;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ApplicationContext applicationContext;

    @Container
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgres"))
            .withDatabaseName("test")
            .withPassword("test")
            .withUsername("test");

    @Container
    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3"));

    public static class Initializer  implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            System.out.println("WAITING FOR CONTAINERS");
            postgreSQLContainer.waitingFor(
                    Wait.forLogMessage(".*database system is ready to accept connections", 1)
            );
            rabbitMQContainer.waitingFor(
                    Wait.forLogMessage(".*Server startup complete;.*", 1)
            );

            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "rabbitmq.host=" + rabbitMQContainer.getHost()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private String getUrl(String uri) {
        return String.format("http://localhost:%s/%s", port, uri);
    }

    @Nested
    class Authenticate {

        @Test
        @DisplayName("Login with valid credentials")
        public void validCredentials() throws IOException {
            Result<JwtToken, ErrorMessage> result = postAnonymous(getUrl("authenticate"), new AuthenticateRequest("admin", "admin"), JwtToken.class);
            assertTrue(result.isSuccess());
        }

        @Test
        @DisplayName("Login with empty username")
        public void emptyUsername() throws IOException {
            Result<JwtToken, ErrorMessage> result = postAnonymous(getUrl("authenticate"), new AuthenticateRequest("", "admin"), JwtToken.class);
            assertTrue(result.isFailure());
            assertEquals("Email cannot be empty", result.getError().getMessage());
        }

        @Test
        @DisplayName("Login with empty password")
        public void emptyPassword() throws IOException {
            Result<JwtToken, ErrorMessage> result = postAnonymous(getUrl("authenticate"), new AuthenticateRequest("admin", ""), JwtToken.class);
            assertTrue(result.isFailure());
            assertEquals("Password cannot be empty", result.getError().getMessage());
        }

        @Test
        @DisplayName("Login with wrong username")
        public void wrongUsername() throws IOException {
            Result<JwtToken, ErrorMessage> result = postAnonymous("http://localhost:7123/authenticate", new AuthenticateRequest("admin1", "admin"), JwtToken.class);
            assertTrue(result.isFailure());
            assertEquals("Invalid email or password specified", result.getError().getMessage());
        }

        @Test
        @DisplayName("Login with wrong password")
        public void wrongPassword() throws IOException {
            Result<JwtToken, ErrorMessage> result = postAnonymous("http://localhost:7123/authenticate", new AuthenticateRequest("admin", "admin1"), JwtToken.class);
            assertTrue(result.isFailure());
            assertEquals("Invalid email or password specified", result.getError().getMessage());
        }
    }

    @Test
    public void test() {
        // So junit would not complain
    }


    private <T> Result<T, ErrorMessage> postAnonymous(String url, Object payload, Class<T> okType) throws IOException {
        RequestBody body = RequestBody.create(MediaType.get("application/json"), objectMapper.writeValueAsString(payload));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();
            if (response.isSuccessful()) {
                return Result.success(objectMapper.readValue(responseBody, okType));
            } else {
                return Result.failure(objectMapper.readValue(responseBody, ErrorMessage.class));
            }
        }
    }
}
