package com.example.xukai2.todo.presenter.taskdetail;

import android.support.annotation.NonNull;

import com.example.xukai2.todo.data.Task;
import com.example.xukai2.todo.data.source.TasksDataSource;
import com.example.xukai2.todo.data.source.TasksRepository;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TaskDetailContract.View mTaskDetailView;

    @NonNull
    private String mTaskId;

    public TaskDetailPresenter(@NonNull String taskId,
                               @NonNull TasksRepository tasksRepository,
                               @NonNull TaskDetailContract.View taskDetailView) {
        mTasksRepository = checkNotNull(tasksRepository, "taskRepository cannot be null");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView can't be null");
        mTaskId = taskId;

        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        openTask();
    }

    private void openTask() {
        if (isNullOrEmptyOfTaskId())
            return;

        mTaskDetailView.setLoadingIndicator(true);
        mTasksRepository.getTask(mTaskId, new TasksDataSource.GetTasksCallBack() {

            @Override
            public void onTasksLoaded(Task task) {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive())
                    return;
                mTaskDetailView.setLoadingIndicator(false);
                if (null == task) {
                    mTaskDetailView.showMissingTask();
                } else {
                    showTask(task);
                }
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive())
                    return;
                mTaskDetailView.showMissingTask();
            }
        });
    }

    @Override
    public void editTask() {
        if (isNullOrEmptyOfTaskId())
            return;
        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        if (isNullOrEmptyOfTaskId())
            return;
        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (isNullOrEmptyOfTaskId())
            return;
        mTasksRepository.completeTask(mTaskId);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (isNullOrEmptyOfTaskId())
            return;
        mTasksRepository.activateTask(mTaskId);
        mTaskDetailView.showTaskMarkedActive();
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }

    private boolean isNullOrEmptyOfTaskId() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return true;
        } else {
            return false;
        }
    }
}
