package net.johanbasson.datavault.users;

import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.johanbasson.datavault.common.ApiError;
import net.johanbasson.datavault.system.CommandDispatcher;
import net.johanbasson.datavault.system.CreateResult;
import net.johanbasson.datavault.system.EventDispatcher;
import net.johanbasson.datavault.system.commandbus.CommandHandler;
import net.johanbasson.datavault.system.eventbus.EventBusSubscriber;
import net.johanbasson.datavault.system.result.Result;
import net.johanbasson.datavault.users.commands.CreateUserCommand;
import net.johanbasson.datavault.users.events.UserCreatedEvent;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@EventBusSubscriber(module = "main")
public class UserService {

    private final CommandDispatcher commandDispatcher;
//    private final PasswordEncoder passwordEncoder;
    private final EventDispatcher eventDispatcher;

    public Result<CreateResult, ApiError> create(String email, String password, Role role) {
        // TODO validation

        UUID id = UUID.randomUUID();
        commandDispatcher.dispatch(new CreateUserCommand(id, email, password, role));
        return Result.success(new CreateResult(id));
    }

    @CommandHandler
    public void handle(CreateUserCommand cmd) {
//        String encoded = passwordEncoder.encode(cmd.getPassword());
        eventDispatcher.dispatch(new UserCreatedEvent(cmd.getId(), cmd.getEmail(), cmd.getPassword(), cmd.getRole()));
    }

    @Subscribe
    public void handle(UserCreatedEvent event) {
        log.info("Persist user to db : {} ", event);
    }
}
