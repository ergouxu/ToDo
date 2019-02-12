package com.example.xukai2.todo.base;

/**
 * The interface Base presenter
 *
 * @param <T> the type parameter
 */
public interface BasePresenter<T extends BaseView> {

    void start();

    /**
     * Attach view
     *
     * @param view view
     */
    void attachView(T view);

    /**
     * Detach view
     */
    void detachView();
}
