package com.javed.todo.Adapters;

import com.javed.todo.API.TodoResponse;

import java.util.List;

public interface TodoAdapterToFragmentCommunicator {
    void onItemClick(String id, String milliseconds, String dialogueTodoDescription, String status);
}
