package com.example.xukai2.todo.view.detail;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.xukai2.todo.R;
import com.example.xukai2.todo.presenter.taskdetail.TaskDetailContract;
import com.example.xukai2.todo.view.addedittask.AddEditTaskActivity;
import com.example.xukai2.todo.view.addedittask.AddEditTaskFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the task detail screen.
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View {

    @NonNull
    private static final String ARGUMENT_TASK_ID = "TASK";

    private static final int REQUEST_EDIT_TASK = 1;

    private TaskDetailContract.Presenter mPresenter;

    private TextView mDetailTitle;

    private TextView mDetailDescription;

    private CheckBox mDetailCompleteStatus;

    public static TaskDetailFragment newInstance(@NonNull String taskId) {
        Bundle argument = new Bundle();
        argument.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_task_detail, container, false);
        setHasOptionsMenu(true);
        mDetailTitle = (TextView) root.findViewById(R.id.task_detail_title);
        mDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);
        mDetailCompleteStatus = (CheckBox) root.findViewById(R.id.task_detail_completed);

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editTask();
            }
        });
        return root;
    }

    @Override
    public void setPresenter(@NonNull TaskDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mDetailTitle.setText("");
            mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(@NonNull String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showCompletionStatus(final boolean complete) {
        checkNotNull(mDetailCompleteStatus);

        mDetailCompleteStatus.setChecked(complete);
        mDetailCompleteStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPresenter.completeTask();
                } else {
                    mPresenter.activateTask();
                }
            }
        });
    }

    @Override
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(getView(), getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(getView(), getString(R.string.task_marked_active), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (requestCode == Activity.RESULT_OK)
                getActivity().finish();
        }
    }

    @Override
    public void showTitle(String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
