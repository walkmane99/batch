package com.tempest;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class ServiceExcecutorTest {
    private static void otherTask(String name) {
        System.out.println("I'm other task! " + name);
    }

    @Test
    public void test1() {

        ExecutorService service = Executors.newFixedThreadPool(5);

        // Runnable, return void, nothing, submit and run the task async
        service.submit(() -> System.out.println("I'm Runnable task."));

        // Callable, return a future, submit and run the task async
        Future<Integer> futureTask1 = service.submit(() -> {
            System.out.println("I'm Callable task.");
            return 1 + 1;
        });

        try {

            otherTask("Before Future Result");

            // block until future returned a result,
            // timeout if the future takes more than 5 seconds to return the result
            Integer result = futureTask1.get(5, TimeUnit.SECONDS);

            System.out.println("Get future result : " + result);

            otherTask("After Future Result");

        } catch (InterruptedException e) {// thread was interrupted
            e.printStackTrace();
        } catch (ExecutionException e) {// thread threw an exception
            e.printStackTrace();
        } catch (TimeoutException e) {// timeout before the future task is complete
            e.printStackTrace();
        } finally {

            // shut down the executor manually
            service.shutdown();

        }

        var executor = new ExecutorService() {

            @Override
            public void execute(Runnable command) {
                // TODO Auto-generated method stub

            }

            @Override
            public void shutdown() {
                // TODO Auto-generated method stub

            }

            @Override
            public List<Runnable> shutdownNow() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isShutdown() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isTerminated() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> Future<T> submit(Runnable task, T result) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Future<?> submit(Runnable task) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                    throws InterruptedException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                    throws InterruptedException, ExecutionException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                // TODO Auto-generated method stub
                return null;
            }

        };

    }

}
