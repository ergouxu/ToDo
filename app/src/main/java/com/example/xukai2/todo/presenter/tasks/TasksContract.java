package com.example.xukai2.todo.presenter.tasks;

import android.support.annotation.NonNull;

import com.example.xukai2.todo.base.BasePresenter;
import com.example.xukai2.todo.base.BaseView;
import com.example.xukai2.todo.component.TasksFilterType;
import com.example.xukai2.todo.data.Task;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TasksContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedCompleted();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopupMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTasks();

        void openTaskDetails(@NonNull Task requestTask);

        void completedTask(@NonNull Task completedTask);

        void activeTask(@NonNull Task activeTask);

        void clearCompletedTask();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }
}
