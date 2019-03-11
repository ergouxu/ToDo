package com.example.xukai2.todo.utils;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;

    private final AtomicInteger counter = new AtomicInteger(0);

    // written from main thread, read from any thread
    private volatile ResourceCallback resourceCallback;

    /**
     * Creates a SimpleCountingIdlingResource
     *
     * @param mResourceName the resource name this resource should report tp Espresso.
     */
    public SimpleCountingIdlingResource(String mResourceName) {
        this.mResourceName = checkNotNull(mResourceName);
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    /**
     * Increment the count of in-flight transaction to the resource being monitored.
     */
    public void increment() {
        counter.getAndIncrement();
    }

    /**
     * Decrement the count of in-flight transaction to the resource being monitored.
     * <p>
     * if this operation result in the counter falling below 0 - an exception is raised.
     *
     * @throws IllegalArgumentException if the counter is below 0.
     */
    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0) {
            // we've gone from non-zero. That means we're idle now! Tell espresso.
            if (null != resourceCallback) {
                resourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0)
            throw new IllegalArgumentException("Counter has been corrupted");
    }
}
