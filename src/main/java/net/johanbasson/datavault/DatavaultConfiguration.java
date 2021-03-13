package net.johanbasson.datavault;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import net.johanbasson.datavault.security.SecurityService;
import net.johanbasson.datavault.system.*;
import net.johanbasson.datavault.system.commandbus.CommandBus;
import net.johanbasson.datavault.system.eventbus.EnableGuavaEventBus;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.jms.ConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableConfigurationProperties({RabbitMqProperties.class})
@Configuration
@EnableGuavaEventBus(modules = "main")
public class DatavaultConfiguration {

    @Bean
    public ConnectionFactory jmsConnectionFactory(RabbitMqProperties rabbitMqProperties) {
        RMQConnectionFactory factory =  new RMQConnectionFactory();
        factory.setUsername(rabbitMqProperties.getUsername());
        factory.setPassword(rabbitMqProperties.getPassword());
//        factory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        factory.setHost(rabbitMqProperties.getHost());
        factory.setPort(rabbitMqProperties.getPort());
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        jmsTemplate.setReceiveTimeout(5000);
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

    @Bean
    public CommandBus commandBus(ApplicationContext context) {
        return new CommandBus.Builder().applicationContext(context).registerCommandHandlers("net.johanbasson.datavault").build();
    }

    @Bean
    Queue commandQueue() {
        return new Queue(Constants.Queues.COMMAND);
    }

    @Bean
    Queue eventQueue() {
        return new Queue(Constants.Queues.EVENT);
    }

    @Bean
    public CommandDispatcher commandDispatcher(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        return new CommandDispatcher(jmsTemplate, Constants.Queues.COMMAND, objectMapper);
    }

    @Bean
    public CommandMessageListener commandMessageListener(@Qualifier("commandExecutorService") ExecutorService executorService, CommandBus commandBus, ObjectMapper objectMapper) {
        return new CommandMessageListener(executorService, commandBus, objectMapper);
    }

    @Bean("commandExecutorService")
    public ExecutorService commandExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public EventDispatcher eventDispatcher(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        return new EventDispatcher(jmsTemplate, Constants.Queues.EVENT, objectMapper);
    }

    @Bean("eventExecutorService")
    public ExecutorService eventExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public EventMessageListener eventMessageListener(@Qualifier("eventExecutorService") ExecutorService executorService, EventBus eventBus, ObjectMapper objectMapper) {
        return new EventMessageListener(executorService, eventBus, objectMapper);
    }

    @Bean
    public SecurityService securityService() {
        return new SecurityService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
