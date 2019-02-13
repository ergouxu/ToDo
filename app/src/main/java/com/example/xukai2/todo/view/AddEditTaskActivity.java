package com.example.xukai2.todo.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.xukai2.todo.R;

public class AddEditTaskActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private AddEditTaskFragment addEditTaskFragmrnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        //Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        addEditTaskFragmrnt = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById
                (R.id.contentFrame);
    }
}
