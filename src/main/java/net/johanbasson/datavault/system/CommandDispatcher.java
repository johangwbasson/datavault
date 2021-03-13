package net.johanbasson.datavault.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import net.johanbasson.datavault.system.commandbus.Command;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandDispatcher {

    @NonNull
    private final JmsTemplate jmsTemplate;
    @NonNull
    private final String queue;
    @NonNull
    private final ObjectMapper objectMapper;

    private final RetryTemplate retryTemplate;

    private static final Map<Class<? extends Throwable>, RetryPolicy> exceptionsToRetryMap = new HashMap<Class<? extends Throwable>, RetryPolicy>() {
        {
            final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(10);
            put(JMSException.class, retryPolicy);
            put(JmsException.class, retryPolicy);
        }
    };

    public CommandDispatcher(JmsTemplate jmsTemplate, String queue, @NonNull ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
        this.objectMapper = objectMapper;

        retryTemplate = new RetryTemplate();
        final ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryPolicy.setPolicyMap(exceptionsToRetryMap);
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());
    }

    public void dispatch(Command command) {
        sendToQueue(command);
    }


    private void sendToQueue(Command command) {
        try {
            log.debug("Sending {} to RabbitMQ", command.getClass().getName());
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                CommandEnvelope envelope = new CommandEnvelope(command.getClass(), objectMapper.writeValueAsString(command));
                jmsTemplate.convertAndSend(queue, objectMapper.writeValueAsBytes(envelope));
                return null;
            });
        } catch (Exception ex) {
            log.error("Could not send command to RabbitMQ", ex);
        }
    }

}
