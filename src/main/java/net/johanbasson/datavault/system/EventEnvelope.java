package net.johanbasson.datavault.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.johanbasson.datavault.system.eventbus.Event;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventEnvelope {

    private Class<? extends Event> clazz;
    private String payload;
}
