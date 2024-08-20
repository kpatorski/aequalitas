package com.result;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A monad that represents the result of an operation, which can either be a success or a failure.
 */
public class Result<Success, Failure> {
    private final Success success;
    private final Failure failure;

    private Result(Success success, Failure failure) {
        this.success = success;
        this.failure = failure;
    }

    /**
     * Creates a new success result.
     *
     * @param <Success> the type of the success result
     * @param <Failure> the type of the failure result
     * @param success   the success result value
     * @return a new Result instance representing success
     */
    public static <Success, Failure> Result<Success, Failure> success(Success success) {
        return new Result<>(success, null);
    }

    /**
     * Creates a new failure result.
     *
     * @param <Success> the type of the success result
     * @param <Failure> the type of the failure result
     * @param failure   the failure result value
     * @return a new Result instance representing failure
     */
    public static <Success, Failure> Result<Success, Failure> failure(Failure failure) {
        return new Result<>(null, failure);
    }

    /**
     * Gets the success result, or null if this is a failure.
     *
     * @return the success result, or null if this is a failure
     */
    public Success success() {
        return success;
    }

    /**
     * Gets the failure result, or null if this is a success.
     *
     * @return the failure result, or null if this is a success
     */
    public Failure failure() {
        return failure;
    }

    /**
     * If this is a success result, applies the given consumer to the success value.
     *
     * @param consumer the consumer to apply to the success value
     * @return this Result instance
     */
    public Result<Success, Failure> ifSuccess(Consumer<Success> consumer) {
        if (isSuccess()) {
            consumer.accept(success);
        }
        return this;
    }

    /**
     * Checks if this is a failure result.
     *
     * @return true if this is a failure result, false if it is a success
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Checks if this is a success result.
     *
     * @return true if this is a success result, false if it is a failure
     */
    public boolean isSuccess() {
        return success != null;
    }

    /**
     * If this is a failure result, applies the given consumer to the failure value.
     *
     * @param consumer the consumer to apply to the failure value
     * @return this Result instance
     */
    public Result<Success, Failure> ifFailure(Consumer<Failure> consumer) {
        if (!isSuccess()) {
            consumer.accept(failure);
        }
        return this;
    }

    /**
     * Applies the appropriate function to the success or failure value and returns the result.
     *
     * @param <Value>       the type of the return value
     * @param successMapper the function to apply to the success value if this is a success
     * @param failureMapper the function to apply to the failure value if this is a failure
     * @return the result of applying the appropriate function
     */
    public <Value> Value get(Function<Success, Value> successMapper, Function<Failure, Value> failureMapper) {
        return isSuccess() ? successMapper.apply(success) : failureMapper.apply(failure);
    }

    /**
     * Maps the success value to a new success value of type {@code <NewSuccess>} using the given mapper function, if this is a success result.
     * If it is a failure result, the original failure is retained.
     *
     * @param <NewSuccess> the type of the new success value
     * @param mapper       the function to map the success value
     * @return a new Result instance with the mapped success value, or the original failure
     */
    public <NewSuccess> Result<NewSuccess, Failure> mapSuccess(Function<Success, NewSuccess> mapper) {
        return isSuccess() ? success(mapper.apply(this.success)) : failure(failure);
    }

    /**
     * Maps the failure value to a new failure value of type {@code <NewFailure>} using the given mapper function, if this is a failure result.
     * If it is a success result, the original success is retained.
     *
     * @param <NewFailure> the type of the new failure value
     * @param mapper       the function to map the failure value
     * @return a new Result instance with the original success or the mapped failure value
     */
    public <NewFailure> Result<Success, NewFailure> mapFailure(Function<Failure, NewFailure> mapper) {
        return isSuccess() ? success(success) : failure(mapper.apply(this.failure));
    }

    /**
     * Maps both the success and failure values to new values using the given mapper functions,
     * depending on whether this is a success or failure result.
     *
     * @param <NewSuccess>  the type of the new success value
     * @param <NewFailure>  the type of the new failure value
     * @param successMapper the function to map the success value
     * @param failureMapper the function to map the failure value
     * @return a new Result instance with the mapped success or failure values
     */
    public <NewSuccess, NewFailure> Result<NewSuccess, NewFailure> map(Function<Success, NewSuccess> successMapper,
                                                                       Function<Failure, NewFailure> failureMapper) {
        return isSuccess() ? success(successMapper.apply(this.success)) : failure(failureMapper.apply(this.failure));
    }

    /**
     * Flat maps the success value to a new Success {@code <NewSuccess>} the given mapper function,
     * if this is a success result. If it is a failure result, the original failure is retained.
     *
     * @param <NewSuccess> the type of the new success value
     * @param mapper       the function to flat map the success value
     * @return the result of applying the mapper function if this is a success, or the original failure
     */
    public <NewSuccess> Result<NewSuccess, Failure> flatSuccess(Function<Success, Result<NewSuccess, Failure>> mapper) {
        return isSuccess() ? mapper.apply(this.success) : failure(failure);
    }

    /**
     * Flat maps the failure value to a new failure {@code <NewFailure>} using the given mapper function,
     * if this is a failure result. If it is a success result, the original success is retained.
     *
     * @param <NewFailure> the type of the new failure value
     * @param mapper       the function to flat map the failure value
     * @return the result of applying the mapper function if this is a failure, or the original success
     */
    public <NewFailure> Result<Success, NewFailure> flatFailure(Function<Failure, Result<Success, NewFailure>> mapper) {
        return isFailure() ? mapper.apply(this.failure) : success(success);
    }

    /**
     * Flat maps both the success and failure values to new values using the given mapper functions,
     * depending on whether this is a success or failure result.
     *
     * @param <NewSuccess>  the type of the new success value
     * @param <NewFailure>  the type of the new failure value
     * @param successMapper the function to flat map the success value if this is a success
     * @param failureMapper the function to flat map the failure value if this is a failure
     * @return the result of applying the appropriate mapper function
     */
    public <NewSuccess, NewFailure> Result<NewSuccess, NewFailure> flat(Function<Success, Result<NewSuccess, NewFailure>> successMapper,
                                                                        Function<Failure, Result<NewSuccess, NewFailure>> failureMapper) {
        return isSuccess() ? successMapper.apply(this.success) : failureMapper.apply(this.failure);
    }
}
