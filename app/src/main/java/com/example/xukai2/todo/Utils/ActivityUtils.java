package com.example.xukai2.todo.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {
    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //add不会刷新容器内容，新进的fragment会覆盖在以前的fragment上，且不允许添加同一个fragment的实例
        //replace会刷新容器内容，一直只有一个fragment显示
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
