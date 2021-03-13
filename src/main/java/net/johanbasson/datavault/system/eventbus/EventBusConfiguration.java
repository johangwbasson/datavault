package net.johanbasson.datavault.system.eventbus;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfiguration {

    /**
     * register BeanPostProcessor to handle automatically registering subscribers with an event bus
     * @return the bus subscriber bean post processor
     */
    @Bean
    public BusSubscriberBeanPostProcessor autoSubscribeEventBusListeners() {
        return new BusSubscriberBeanPostProcessor();
    }

    @Bean
    @Qualifier("EVENT_BUS_PROVIDER")
    public EventBusProvider eventBusProvider() {
        return EventBusProvider.createProvider();
    }
}
