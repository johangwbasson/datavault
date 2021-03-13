package net.johanbasson.datavault.system.result;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Failure<V, E> extends Result<V, E> {

    private final E error;

    public Failure(E error) {
        this.error = error;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public E getError() {
        return error;
    }

    @Override
    public Result<?, E> combine(Result<?, E> otherResult) {
        return this;
    }

    @Override
    public <T> Result<T, E> onSuccess(Supplier<Result<T, E>> function) {
        return new Failure<>(getError());
    }

    @Override
    public Result<V, E> onSuccess(Consumer<V> function) {
        return this;
    }

    @Override
    public Result<V, E> onFailure(Runnable function) {
        function.run();
        return this;
    }

    @Override
    public Result<V, E> onFailure(Consumer<E> function) {
        function.accept(getError());
        return this;
    }

    @Override
    public <T> Result<T, E> flatMap(Function<V, Result<T, E>> function) {
        return new Failure<>(getError());
    }

    @Override
    public <T> Result<T, E> map(Function<V, T> function) {
        return new Failure<>(getError());
    }
}
