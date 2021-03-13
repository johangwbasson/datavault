package net.johanbasson.datavault.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.johanbasson.datavault.system.commandbus.Command;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandEnvelope {

    private Class<? extends Command> clazz;
    private String payload;
}
