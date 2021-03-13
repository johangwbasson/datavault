package net.johanbasson.datavault.system;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import net.johanbasson.datavault.system.eventbus.Event;
import net.johanbasson.datavault.system.eventbus.EventBusSubscriber;

@Slf4j
@EventBusSubscriber(module = "main")
public class SystemEventHandler {

    @Subscribe
    public void handle(Event event) {
        log.info("EVENT : {}", event);
    }
}
