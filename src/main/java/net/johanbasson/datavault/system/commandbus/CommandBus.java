package net.johanbasson.datavault.system.commandbus;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.context.ApplicationContext;
import org.yaml.snakeyaml.TypeDescription;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@Slf4j
public class CommandBus {

    private final Map<Class, CommandInvoker> handlerMap;

    private CommandBus(Map<Class, CommandInvoker> handlers){
        this.handlerMap = handlers;
    }

    @Slf4j
    public static class Builder  {
        private ApplicationContext applicationContext;
        private List<Object> contenders;

        public Builder applicationContext(ApplicationContext context) {
            this.applicationContext = context;
            return this;
        }

        public Builder registerCommandHandlers(String basePackage) {
            if (applicationContext == null) {
                throw new RuntimeException("Call applicationContext First");
            }
            contenders = find(basePackage, CommandHandler.class);
            return this;
        }

        public CommandBus build() {
            Map<Class, CommandInvoker> handlerMap = new HashMap<>();
            contenders.forEach(contender -> {
                Set<Method> handlingMethods = getAllMethods(contender.getClass(), withAnnotation(CommandHandler.class));

                handlingMethods.forEach(method -> {
                    List<TypeDescription> params = Arrays.stream(method.getParameters())
                            .map(param -> new TypeDescription(param.getType(), param.getName()))
                            .collect(toList());

                    // Only one parameter supported - AuthenticatedCommand
                    if (params.size() == 1 && Command.class.isAssignableFrom(params.get(0).getType())) {
                        log.info("Found handler '{}' in object '{}'", method.getName(), contender.getClass().getName());
                        handlerMap.put(params.get(0).getType(), new CommandInvoker(contender, method));
                    }
                });
            });
            return new CommandBus(handlerMap);
        }

        private List<Object> find(String basePackage, Class<? extends Annotation> annotationClass) {
            return new Reflections(basePackage, new MethodAnnotationsScanner())
                    .getMethodsAnnotatedWith(annotationClass).stream()
                    .map(Method::getDeclaringClass)
                    .distinct()
                    .map(this::findBean)
                    .filter(Objects::nonNull)
                    .collect(toList());
        }

        private Object findBean(Class beanClass) {
            try {
                //noinspection unchecked
                return applicationContext.getBean(beanClass);
            } catch (Exception e) {
                log.error("Invocation error", e);
                return null;
            }
        }
    }

    public <R> R execute(Object command) {
        CommandInvoker commandInvoker = handlerMap.get(command.getClass());
        if (commandInvoker == null) {
            log.error("There is no command handler registered for {}", command.getClass().getName());
            throw new IllegalArgumentException(format("Command handler for command %s not found", command.getClass().getName()));
        }

        return (R) commandInvoker.invoke(command);
    }

    public <R> CompletableFuture<R> executeAsync(Object command) {
        return CompletableFuture.supplyAsync(() -> execute(command));
    }
}