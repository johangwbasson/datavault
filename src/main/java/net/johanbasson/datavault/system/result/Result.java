package net.johanbasson.datavault.system.result;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Result<V, E> {

    public static <V, E> Result<V, E> failure(final E error) {
        assertParameterNotNull(error, "Error");
        return new Failure<>(error);
    }

    public static <V, E> Result<V, E> success(final V value) {
        assertParameterNotNull(value, "Value");
        return new Success<>(value);
    }

    protected static void assertParameterNotNull(final Object parameter, final String name) {
        if (parameter == null) {
            throw new IllegalArgumentException(String.format("%s may not be null.", name));
        }
    }

    public abstract boolean isFailure();

    public final boolean isSuccess() {
        return !isFailure();
    }

    public abstract V getValue();

    public abstract E getError();

    public abstract Result<?, E> combine(final Result<?, E> otherResult);

    public abstract <T> Result<T, E> onSuccess(final Supplier<Result<T, E>> function);

    public abstract Result<V, E> onSuccess(final Consumer<V> function);

    public abstract Result<V, E> onFailure(final Runnable function);

    public abstract Result<V, E> onFailure(final Consumer<E> function);

    public abstract <T> Result<T, E> flatMap(final Function<V, Result<T, E>> function);

    public abstract <T> Result<T, E> map(final Function<V, T> function);
}
