package com.example.xukai2.todo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.xukai2.todo.utils.AppExecutors;
import com.example.xukai2.todo.data.FakeTasksRemoteDataSource;
import com.example.xukai2.todo.data.source.TasksDataSource;
import com.example.xukai2.todo.data.source.TasksRepository;
import com.example.xukai2.todo.data.source.local.TaskLocalDataSource;
import com.example.xukai2.todo.data.source.local.ToDoDatabase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enable injection of mock implementation for {@link TasksDataSource} at complie
 * time. This is useful testing, since it allows us to use a fake instance of the class to isolate the dependencies
 * and run a test hermetically.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        ToDoDatabase database = ToDoDatabase.getInstance(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(), TaskLocalDataSource.getInstance
                (new AppExecutors(), database.taskDao()));
    }
}
