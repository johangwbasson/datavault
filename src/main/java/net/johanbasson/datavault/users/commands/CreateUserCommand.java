package net.johanbasson.datavault.users.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.johanbasson.datavault.system.commandbus.Command;
import net.johanbasson.datavault.users.Role;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand extends Command {

    private UUID id;
    private String email;
    private String password;
    private Role role;
}
