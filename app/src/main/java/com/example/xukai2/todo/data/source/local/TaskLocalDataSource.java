package com.example.xukai2.todo.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.example.xukai2.todo.utils.AppExecutors;
import com.example.xukai2.todo.data.Task;
import com.example.xukai2.todo.data.source.TasksDataSource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskLocalDataSource implements TasksDataSource {

    private static volatile TaskLocalDataSource INSTANCE;

    private TaskDao mTaskDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    public TaskLocalDataSource(@NonNull TaskDao mTaskDao, @NonNull AppExecutors mAppExecutors) {
        this.mTaskDao = mTaskDao;
        this.mAppExecutors = mAppExecutors;
    }

    public static TaskLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull TaskDao taskDao) {
        if (INSTANCE == null) {
            synchronized (TaskLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskLocalDataSource(taskDao, appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if database is doesn't exist or the table is empty.
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = mTaskDao.getTasks();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()) {
                            //This will be called if the table is new or empty
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if the {@link Task} isn't found.
     */
    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTaskDao.getTaskById(taskId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task == null) {
                            callBack.onTasksLoaded(task);
                        } else {
                            callBack.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveTask(@NonNull final Task task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.insertTask(task);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void completeTask(@NonNull final Task task) {
        Runnable completedRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.updateCompleted(task.getId(), true);
            }
        };
        mAppExecutors.diskIO().execute(completedRunnable);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateTask(@NonNull final Task task) {
        Runnable activityRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.updateCompleted(task.getId(), false);
            }
        };
        mAppExecutors.diskIO().execute(activityRunnable);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedTasks() {
        Runnable clearRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteCompletedTasks();
            }
        };
        mAppExecutors.diskIO().execute(clearRunnable);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        Runnable deleteAllRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteTasks();
            }
        };
        mAppExecutors.diskIO().execute(deleteAllRunnable);
    }

    @Override
    public void deleteTask(@NonNull final String taskId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTaskDao.deleteTaskById(taskId);
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
