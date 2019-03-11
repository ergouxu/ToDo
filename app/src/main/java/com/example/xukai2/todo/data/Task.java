package com.example.xukai2.todo.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

@Entity(tableName = "tasks")
public final class Task {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "task_id")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private final String mTitle;

    @NonNull
    @ColumnInfo(name = "description")
    private final String mDescription;

    @NonNull
    @ColumnInfo(name = "completed")
    private final boolean mCompleted;

    /**
     * Use this constructor to create a new active Task
     *
     * @param mTitle       title of the task
     * @param mDescription description of the task
     */
    @Ignore
    public Task(@NonNull String mTitle, @NonNull String mDescription) {
        this(UUID.randomUUID().toString(), mTitle, mDescription, false);
    }

    /**
     * Use this constructor to create an active Task if the Task already has an id (copy the another task)
     *
     * @param mId          id of the task
     * @param mTitle       title of the task
     * @param mDescription description of the task
     */
    @Ignore
    public Task(@NonNull String mId, @NonNull String mTitle, @NonNull String mDescription) {
        this(mId, mTitle, mDescription, false);
    }

    /**
     * Use this constructor to create a new completed Task
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   completed of the task
     */
    @Ignore
    public Task(@NonNull String title, @NonNull String description, @NonNull boolean completed) {
        this(UUID.randomUUID().toString(), title, description, completed);
    }


    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param mId          id of the task
     * @param mTitle       title of the task
     * @param mDescription description of the task
     * @param mCompleted   completed of the task
     */
    public Task(@NonNull String mId, @NonNull String mTitle, @NonNull String mDescription, @NonNull boolean
            mCompleted) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mCompleted = mCompleted;
    }

    @NonNull
    public String getmId() {
        return mId;
    }

    @NonNull
    public String getmTitle() {
        return mTitle;
    }

    @NonNull
    public String getmDescription() {
        return mDescription;
    }

    @NonNull
    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    @NonNull
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) && Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equal(mId, task.mId) &&
                Objects.equal(mTitle, task.mTitle) &&
                Objects.equal(mDescription, task.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Task with Title " + mTitle;
    }
}
