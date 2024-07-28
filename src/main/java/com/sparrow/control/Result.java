package com.sparrow.control;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result<Success, Failure> {
    private final Success success;
    private final Failure failure;

    private Result(Success success, Failure failure) {
        this.success = success;
        this.failure = failure;
    }

    public static <Success, Failure> Result<Success, Failure> success(Success success) {
        return new Result<>(success, null);
    }

    public static <Success, Failure> Result<Success, Failure> failure(Failure failure) {
        return new Result<>(null, failure);
    }

    public Success success() {
        return success;
    }

    public Failure failure() {
        return failure;
    }

    public Result<Success, Failure> ifSuccess(Consumer<Success> consumer) {
        if (isSuccess()) {
            consumer.accept(success);
        }
        return this;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public boolean isSuccess() {
        return success != null;
    }

    public Result<Success, Failure> ifFailure(Consumer<Failure> consumer) {
        if (!isSuccess()) {
            consumer.accept(failure);
        }
        return this;
    }

    public <Value> Value get(Function<Success, Value> successMapper, Function<Failure, Value> failureMapper) {
        return isSuccess() ? successMapper.apply(success) : failureMapper.apply(failure);
    }

    public <NewSuccess> Result<NewSuccess, Failure> mapSuccess(Function<Success, NewSuccess> mapper) {
        return isSuccess() ? success(mapper.apply(this.success)) : failure(failure);
    }

    public <NewFailure> Result<Success, NewFailure> mapFailure(Function<Failure, NewFailure> mapper) {
        return isSuccess() ? success(success) : failure(mapper.apply(this.failure));
    }

    public <NewSuccess, NewFailure> Result<NewSuccess, NewFailure> map(Function<Success, NewSuccess> successMapper,
                                                                       Function<Failure, NewFailure> failureMapper) {
        return isSuccess() ? success(successMapper.apply(this.success)) : failure(failureMapper.apply(this.failure));
    }
}
