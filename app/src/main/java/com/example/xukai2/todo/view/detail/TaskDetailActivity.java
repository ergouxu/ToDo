package com.example.xukai2.todo.view.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.xukai2.todo.Injection;
import com.example.xukai2.todo.R;
import com.example.xukai2.todo.presenter.taskdetail.TaskDetailPresenter;
import com.example.xukai2.todo.utils.ActivityUtils;

/**
 * Displays task details screen
 */
public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);

        //Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        // Get the requested task id
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R
                .id.contentFrame);
        if (taskDetailFragment == null){
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskDetailFragment, R.id.contentFrame);
        }

        // Create the presenter
        new TaskDetailPresenter(
                taskId,
                Injection.provideTasksRepository(getApplicationContext()),
                taskDetailFragment
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
