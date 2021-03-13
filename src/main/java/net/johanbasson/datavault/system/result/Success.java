package net.johanbasson.datavault.system.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Success<V, E> extends Result<V, E> {

    private final Optional<V> value;

    public Success(V value) {
        this.value = Optional.of(Objects.requireNonNull(value));
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public V getValue() {
        return value.get();
    }

    @Override
    public E getError() {
        return null;
    }

    @Override
    public Result<?, E> combine(final Result<?, E> otherResult) {
        if (otherResult.isFailure()) {
            return otherResult;
        }
        return this;
    }

    @Override
    public <T> Result<T, E> onSuccess(Supplier<Result<T, E>> function) {
        return function.get();
    }

    @Override
    public Result<V, E> onSuccess(Consumer<V> function) {
        function.accept(getValue());
        return this;
    }

    @Override
    public Result<V, E> onFailure(Runnable function) {
        return this;
    }

    @Override
    public Result<V, E> onFailure(Consumer<E> function) {
        return this;
    }

    @Override
    public <T> Result<T, E> flatMap(Function<V, Result<T, E>> function) {
        return function.apply(getValue());
    }

    @Override
    public <T> Result<T, E> map(Function<V, T> function) {
        return flatMap(function.andThen(Success::new));
    }
}
