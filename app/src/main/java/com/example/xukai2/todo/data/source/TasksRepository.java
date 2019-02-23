package com.example.xukai2.todo.data.source;

import android.support.annotation.NonNull;

import com.example.xukai2.todo.data.Task;

import java.util.ArrayList;
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

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        if (mCachedTasks != null && !mCachedIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        if (mCachedIsDirty) {
            //If the cache is dirty wo need to fetch new data from network
            getTaskFromRemoteDataSource(callback);
        }
    }

    private void getTaskFromRemoteDataSource(@NonNull LoadTasksCallback callback) {
        mTaskRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null)
            mCachedTasks = new LinkedHashMap<>();
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getmId(), task);
        }
        mCachedIsDirty = false;
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTasksCallBack callBack) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask() {

    }
}
