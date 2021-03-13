package net.johanbasson.datavault.users.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.johanbasson.datavault.system.eventbus.Event;
import net.johanbasson.datavault.users.Role;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreatedEvent extends Event {
    private UUID id;
    private String email;
    private String password;
    private Role role;
}
