package com.example.xukai2.todo.presenter.addedittask;

import com.example.xukai2.todo.base.BasePresenter;
import com.example.xukai2.todo.base.BaseView;

public interface AddEditTaskContract {

    interface View extends BaseView {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter<View> {

        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
