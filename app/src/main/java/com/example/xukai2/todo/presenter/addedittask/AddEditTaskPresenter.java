package com.example.xukai2.todo.presenter.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.xukai2.todo.data.Task;
import com.example.xukai2.todo.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditTaskPresenter implements AddEditTaskContract.Presenter, TasksDataSource.GetTasksCallBack {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddEditTaskContract.View mAddTaskView;

    @Nullable
    private String mTaskId;

    private boolean mIsDataMissing;

    /**
     * creates a presenter for the add/edit view.
     *
     * @param taskId          ID of the task to edit or null for a new task.
     * @param tasksRepository  repository of data for tasks.
     * @param addTaskView     the add/edit view.
     * @param shouldLoadDataFromRepo   whether data needs to be loaded or not (for config change)
     */
    public AddEditTaskPresenter(@Nullable String taskId, @NonNull TasksDataSource tasksRepository, @NonNull
            AddEditTaskContract.View addTaskView, boolean shouldLoadDataFromRepo) {
        this.mTasksRepository = checkNotNull(tasksRepository);
        this.mAddTaskView = checkNotNull(addTaskView);
        this.mTaskId = taskId;
        this.mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void start() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }

    @Override
    public void onTasksLoaded(Task task) {
        // The view may not be able to handle UI updates anymore.
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.saveTask(new Task(mTaskId, title, description));
        mAddTaskView.showTasksList();// After an edit, go back to the list.
    }
}
