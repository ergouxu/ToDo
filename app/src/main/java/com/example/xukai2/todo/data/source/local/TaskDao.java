package com.example.xukai2.todo.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.xukai2.todo.data.Task;

import java.util.List;

/**
 *Data Access Object for the tasks table
 */
@Dao
public interface TaskDao {

    /**
     * Select all tasks from the tasks table
     *
     * @return all tasks
     */
    @Query("SELECT * FROM Tasks")
    List<Task> getTasks();

    /**
     * Select task by id
     *
     * @param taskId the task id.
     * @return the task with id.
     */
    @Query("SELECT * FROM Tasks WHERE task_id = :taskId")
    Task getTaskById(String taskId);

    /**
     * Insert a task in the database. If the task already exist, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    /**
     * Update a task
     *
     * @param task task to update.
     * @return the number of task updated. This should always be 1.
     */
    @Update
    int uptdateTask(Task task);

    /**
     * Update the completed status of a task.
     *
     * @param taskId id of the task.
     * @param completed status to be update.
     */
    @Query("UPDATE tasks SET completed = :completed WHERE task_id = :taskId")
    void updateCompleted(String taskId, boolean completed);

    /**
     * Delete a task by id.
     *
     * @param taskId id of the task.
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM tasks WHERE task_id = :taskId")
    int deleteTaskById(String taskId);

    /**
     * Deleted all tasks.
     */
    @Query("DELETE FROM tasks")
    void deleteTasks();

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("DELETE FROM tasks WHERE completed = 1")
    int deleteCompletedTasks();
}
