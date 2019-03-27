package com.example.xukai2.todo.presenter.statistics;

import android.support.annotation.NonNull;

import com.example.xukai2.todo.data.Task;
import com.example.xukai2.todo.data.source.TasksDataSource;
import com.example.xukai2.todo.data.source.TasksRepository;
import com.example.xukai2.todo.utils.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final StatisticsContract.View mStatisticsView;

    public StatisticsPresenter(@NonNull TasksRepository tasksRepository,
                               @NonNull StatisticsContract.View statisticsView) {
        this.mTasksRepository = checkNotNull(tasksRepository, "tasksRepository can't be null");
        this.mStatisticsView = checkNotNull(statisticsView, "statistics can't be null");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); //App is busy until further notice

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int activeTasks = 0;
                int completedTasks = 0;

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow())
                    EspressoIdlingResource.decrement(); // Set app is idle.

                // We calculate number of active and completed tasks
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }
                // The view may not be able to handle UI update anymore
                if (!mStatisticsView.isActive())
                    return;
                mStatisticsView.setProgressIndicator(false);

                mStatisticsView.showStatistics(activeTasks, completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                //The view may not be able to handle UI update anymore
                if (!mStatisticsView.isActive())
                    return;
                mStatisticsView.showLoadingStatisticsError();
            }
        });

    }
}
