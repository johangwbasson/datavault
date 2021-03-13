package net.johanbasson.datavault.system.commandbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandInvoker {

	private final Object target;
	private final Method method;

	public CommandInvoker(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	public Object invoke(Object cmd) {
		try {
			return method.invoke(target, cmd);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
