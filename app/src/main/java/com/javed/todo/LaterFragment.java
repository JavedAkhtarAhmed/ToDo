package com.javed.todo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javed.todo.API.TodoResponse;
import com.javed.todo.Adapters.TodoCompletedAdapter;
import com.javed.todo.Adapters.TodoPendingAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;


public class LaterFragment extends Fragment {

    List<TodoResponse> todoLaterList;
    List<TodoResponse> todoCompletedLaterList;
    List<TodoResponse> todoPendingLaterList;

    TodoCompletedAdapter todoCompletedAdapter;
    RecyclerView completedTodoRecyclerView;

    TodoPendingAdapter todoPendingAdapter;
    RecyclerView pendingTodoRecyclerView;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LaterFragment(List<TodoResponse> todoList) {
        this.todoLaterList = todoList;
        Log.d("TAG2", "LaterFragment: " + todoLaterList.size());
    }

    public LaterFragment() {

    }

    public static LaterFragment newInstance(String param1, String param2) {
        LaterFragment fragment = new LaterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_later, container, false);

        pendingTodoRecyclerView = view.findViewById(R.id.rvPending);
        pendingTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));

        completedTodoRecyclerView = view.findViewById(R.id.rvCompleted);
        completedTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        Log.d("TAG2", "onCreateView: in later fragment");

        todoPending();
        todoCompleted();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(pendingTodoRecyclerView);

        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.RIGHT) {
                TodoResponse todoResponse = todoPendingLaterList.get(position);
                todoResponse.setStatus("COMPLETED");

                todoCompletedLaterList.add(todoResponse);
                todoCompletedAdapter.notifyItemInserted(todoCompletedLaterList.size() - 1);
                todoCompletedAdapter.notifyDataSetChanged();
                completedTodoRecyclerView.scrollToPosition(todoCompletedLaterList.size() - 1);

                todoPendingLaterList.remove(position);
                todoPendingAdapter.notifyItemRemoved(position);
                todoPendingAdapter.notifyDataSetChanged();

            }


        }
    };

    private void todoPending() {
        todoPendingLaterList = new ArrayList<TodoResponse>(todoLaterList.subList(0, todoLaterList.size()));

        Log.d("TAG2", "todoPending: todoListPending.size  " + todoPendingLaterList.toString());
        ListIterator<TodoResponse> iter = todoPendingLaterList.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getStatus().equals("COMPLETED")) {
                iter.remove();
            }
        }

        Collections.sort(todoPendingLaterList, new Comparator<TodoResponse>() {
            public int compare(TodoResponse obj1, TodoResponse obj2) {
                // ## Ascending order
                return obj2.getScheduledDate().compareToIgnoreCase(obj1.getScheduledDate());
            }
        });
        Log.d("TAG2", "onResponse: todoPending " + todoPendingLaterList.size());
        todoPendingAdapter = new TodoPendingAdapter(todoPendingLaterList, getActivity());
        pendingTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingTodoRecyclerView.setHasFixedSize(true);
        pendingTodoRecyclerView.setAdapter(todoPendingAdapter);
    }

    private void todoCompleted() {

        todoCompletedLaterList = new ArrayList<TodoResponse>(todoLaterList.subList(0, todoLaterList.size()));
        ListIterator<TodoResponse> iter = todoCompletedLaterList.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getStatus().equals("PENDING")) {
                iter.remove();
            }
        }
        Collections.sort(todoCompletedLaterList, new Comparator<TodoResponse>() {
            public int compare(TodoResponse obj1, TodoResponse obj2) {
                // ## Ascending order
                return obj2.getScheduledDate().compareToIgnoreCase(obj1.getScheduledDate());
            }
        });
        todoCompletedAdapter = new TodoCompletedAdapter(todoCompletedLaterList);
        completedTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        completedTodoRecyclerView.setHasFixedSize(true);
        completedTodoRecyclerView.setAdapter(todoCompletedAdapter);
    }

}