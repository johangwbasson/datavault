package net.johanbasson.datavault.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import net.johanbasson.datavault.Constants;
import net.johanbasson.datavault.system.eventbus.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.ExecutorService;

@Slf4j
public class EventMessageListener {
    private final ExecutorService executorService;
    private final EventBus eventBus;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventMessageListener(@Qualifier("eventExecutorService") ExecutorService executorService, EventBus eventBus, ObjectMapper objectMapper) {
        this.eventBus = eventBus;
        this.executorService = executorService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = Constants.Queues.EVENT)
    public void handle(byte[] data) throws Exception {
        EventEnvelope envelope = objectMapper.readValue(data, EventEnvelope.class);
        Event event = objectMapper.readValue(envelope.getPayload(), envelope.getClazz());
        executorService.execute(() -> {
            eventBus.post(event);
        });
    }
}
