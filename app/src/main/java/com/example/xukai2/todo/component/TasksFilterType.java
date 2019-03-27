package com.example.xukai2.todo.component;

/**
 * Used with the filter spinner in the tasks list.
 */
public enum TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filter only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filter only the completed tasks.
     */
    COMPLETED_TASKS
}
