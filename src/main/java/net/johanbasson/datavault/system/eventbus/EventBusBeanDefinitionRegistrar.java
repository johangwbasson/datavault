package net.johanbasson.datavault.system.eventbus;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

@Slf4j
public class EventBusBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    private static final String EVENT_BUS_PROVIDER_NAME = "eventBusProvider";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableGuavaEventBus.class.getName(), false);
        String[] modules = (String[]) annotationAttributes.get("modules");

        if (modules.length > 0) {
            for (String module: modules) {
                if(!registry.containsBeanDefinition(module)) {
                    log.debug("Registering EventBus Bean Definition for Module [{}]", module);

                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EventBus.class);
                    builder.setFactoryMethodOnBean("get", EVENT_BUS_PROVIDER_NAME);
                    builder.addConstructorArgValue(module);

                    registry.registerBeanDefinition(module, builder.getBeanDefinition());
                }
            }
        }

    }
}
