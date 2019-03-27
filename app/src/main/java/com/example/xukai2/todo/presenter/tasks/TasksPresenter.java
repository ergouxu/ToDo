package com.example.xukai2.todo.presenter.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.xukai2.todo.component.TasksFilterType;
import com.example.xukai2.todo.data.Task;
import com.example.xukai2.todo.data.source.TasksDataSource;
import com.example.xukai2.todo.data.source.TasksRepository;
import com.example.xukai2.todo.utils.EspressoIdlingResource;
import com.example.xukai2.todo.view.addedittask.AddEditTaskActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TasksContract.View mTasksView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null");

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode)
            mTasksView.showSuccessfullySavedMessage();
    }

    @Override
    public void loadTasks(boolean forceUpdate) {

    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI)
            mTasksView.setLoadingIndicator(true);
        if (forceUpdate)
            mTasksRepository.refreshTasks();

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the request is handled.
        EspressoIdlingResource.increment(); //App is busy until further notice.

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<Task>();

                // This callback may be called twice, once for cache and once for loading
                // the data from the  service API, so wo check before decrementing, otherwise
                // it throw "Counter has been corrupted!" exception.
                if (EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); //Set app as idle.
                }

                for (Task task : tasks) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive())
                                tasksToShow.add(task);
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted())
                                tasksToShow.add(task);
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                //
                if (!mTasksView.isActive())
                    return;
                if (showLoadingUI)
                    mTasksView.setLoadingIndicator(false);

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTasksView.isActive())
                    return;
                mTasksView.showLoadingTasksError();
            }
        });
    }

    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that  filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks.
            mTasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTasks() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestTask) {
        checkNotNull(requestTask, "requestTask can't be null!");
        mTasksView.showTaskDetailsUi(requestTask.getId());
    }

    @Override
    public void completedTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask can't be null!");
        mTasksRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedCompleted();
        loadTasks(false, false);
    }

    @Override
    public void activeTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask can't be null!");
        mTasksRepository.activateTask(activeTask);
        mTasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTask() {
        mTasksRepository.clearCompletedTasks();
        mTasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    /**
     * sets the current task filtering type
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#ACTIVE_TASKS}, or
     *                    {@link TasksFilterType#COMPLETED_TASKS}
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
