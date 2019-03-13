package com.example.xukai2.todo.data.source;

import android.support.annotation.NonNull;

import com.example.xukai2.todo.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTaskRemoteDataSource;

    private final TasksDataSource mTaskLocalDataSource;


    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> mCachedTasks;

    /**
     * make the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCachedIsDirty = false;

    //Private direct instantiation.


    public TasksRepository(@NonNull TasksDataSource mTaskRemoteDataSource,
                           @NonNull TasksDataSource mTaskLocalDataSource) {
        this.mTaskRemoteDataSource = mTaskRemoteDataSource;
        this.mTaskLocalDataSource = mTaskLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param taskRemoteDataSource the backend data source
     * @param taskLocalDataSource  the device storage data source.
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource taskRemoteDataSource, TasksDataSource
            taskLocalDataSource) {
        if (INSTANCE == null)
            INSTANCE = new TasksRepository(taskRemoteDataSource, taskLocalDataSource);
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TasksDataSource, TasksDataSource)} to create a new instance
     * next time it is called
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Get tasks from cache, local data source (SQLite) or remove data source, whichever is available first.
     * <p>
     * Note:{@link LoadTasksCallback#onDataNotAvailable()} is fired if all data source fail to get the data.
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        //Respond immediately with cache if available nd not dirty
        if (mCachedTasks != null && !mCachedIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if (mCachedIsDirty) {
            //If the cache is dirty wo need to fetch new data from network
            getTasksFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mTaskRemoteDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTaskRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCachedIsDirty = false;
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it user the
     * network data source. This is done to simplify the sample.
     * <p>
     * Note:{@link GetTasksCallBack#onDataNotAvailable()} is fired id both data source fail to get the data.
     */
    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTasksCallBack callBack) {
        checkNotNull(taskId);
        checkNotNull(callBack);

        Task cachedTask = getTaskWithId(taskId);

        //Respond immediately with cache if available
        if (cachedTask != null) {
            callBack.onTasksLoaded(cachedTask);
        }

        // Load from server/persisted if needed.

        //Is the task in the local data source? If not, query the network.
        mTaskLocalDataSource.getTask(taskId, new GetTasksCallBack() {
            @Override
            public void onTasksLoaded(Task task) {
                //Do in memory cache update to keep the app UI up to date
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(), task);
                callBack.onTasksLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTaskRemoteDataSource.getTask(taskId, new GetTasksCallBack() {
                    @Override
                    public void onTasksLoaded(Task task) {
                        //Do in memory cache update to keep the app UI up to date
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(), task);
                        callBack.onTasksLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callBack.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskRemoteDataSource.saveTask(task);
        mTaskLocalDataSource.saveTask(task);

        //Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskLocalDataSource.completeTask(task);
        mTaskRemoteDataSource.completeTask(task);

        Task completedTask = new Task(task.getId(), task.getTitle(), task.getDescription(), true);

        //Do in memory cache update to keep the app UI up ro data
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }


    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskRemoteDataSource.activateTask(task);
        mTaskLocalDataSource.activateTask(task);

        Task activityTask = new Task(task.getId(), task.getTitle(), task.getDescription());

        //Do in memory cache to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activityTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTaskLocalDataSource.clearCompletedTasks();
        mTaskRemoteDataSource.clearCompletedTasks();

        //Do in memory cache update to keep the app UI to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCachedIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTaskRemoteDataSource.deleteAllTasks();
        mTaskLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTaskRemoteDataSource.deleteTask(taskId);
        mTaskLocalDataSource.deleteTask(taskId);

        mCachedTasks.remove(taskId);
    }


    private void refreshLocalDataSource(List<Task> tasks) {
        mTaskLocalDataSource.deleteAllTasks();
        for (Task task : tasks) {
            mTaskLocalDataSource.saveTask(task);
        }
    }

    @NonNull
    private Task getTaskWithId(@NonNull String taskId) {
        checkNotNull(taskId);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }
    }
}
