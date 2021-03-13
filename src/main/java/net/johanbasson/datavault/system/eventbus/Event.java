package net.johanbasson.datavault.system.eventbus;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class Event {

    private final long timestamp = System.currentTimeMillis();

}
