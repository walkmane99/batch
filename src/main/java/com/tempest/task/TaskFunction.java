package com.tempest.task;

import java.util.Optional;
import java.util.function.Function;

import com.tempest.store.State;

import lombok.extern.log4j.Log4j2;

import static com.tempest.function.LambdaExceptionUtil.*;

/**
 * AutoTask
 */
@Log4j2
public abstract class TaskFunction<T> extends BaseTask {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(final State share) throws InterruptedException {
        Optional<T> state = this.getProperties(share);
        try {
            boolean result = state.map(rethrowFunction(st -> createTaskFunction(share).apply(st))).get();
            if (result) {
                return super.exec(share);
            }
        } catch (Exception e) {
            log.catching(e);
            if (e instanceof InterruptedException) {
                throw (InterruptedException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
        return false;
    }

    public abstract Function<T, Boolean> createTaskFunction(State share) throws Exception;

}
