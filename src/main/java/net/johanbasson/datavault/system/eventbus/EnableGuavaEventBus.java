package net.johanbasson.datavault.system.eventbus;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EventBusConfiguration.class,EventBusBeanDefinitionRegistrar.class})
@Documented
public @interface EnableGuavaEventBus {
    String[] modules() default {};
}
