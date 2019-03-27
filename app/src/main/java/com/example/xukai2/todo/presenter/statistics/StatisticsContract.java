package com.example.xukai2.todo.presenter.statistics;

import com.example.xukai2.todo.base.BasePresenter;
import com.example.xukai2.todo.base.BaseView;

/**
 * This specifies the contract between the view and presenter.
 */
public interface StatisticsContract {

    interface View extends BaseView<Presenter>{

        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTask, int numberOfCompletedTask);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{}
}
