package net.johanbasson.datavault.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.johanbasson.datavault.Constants;
import net.johanbasson.datavault.system.commandbus.Command;
import net.johanbasson.datavault.system.commandbus.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;

import java.util.concurrent.ExecutorService;

@Slf4j
public class CommandMessageListener {

    private final ExecutorService executorService;
    private final CommandBus commandBus;
    private final ObjectMapper objectMapper;

    @Autowired
    public CommandMessageListener(@Qualifier("commandExecutorService") ExecutorService executorService, CommandBus commandBus, ObjectMapper objectMapper) {
        this.commandBus = commandBus;
        this.executorService = executorService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = Constants.Queues.COMMAND)
    public void handle(byte[] data) throws Exception {
        CommandEnvelope envelope = objectMapper.readValue(data, CommandEnvelope.class);
        Command command = objectMapper.readValue(envelope.getPayload(), envelope.getClazz());
        log.debug("Submitting {} to executor service", command.getClass().getName());
        executorService.execute(() -> {
            log.debug("Sending {} to command gateway", command.getClass().getName());
            commandBus.execute(command);
        });
    }
}
