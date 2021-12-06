package com.javed.todo.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.javed.todo.API.TodoResponse;
import com.javed.todo.LaterFragment;
import com.javed.todo.TodayFragment;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    List<TodoResponse> todoList;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<TodoResponse>todoList) {
        super(fragmentManager, lifecycle);
        this.todoList=todoList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1) {
            return new LaterFragment(todoList);
        }
        return new TodayFragment(todoList);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
