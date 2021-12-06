package com.javed.todo.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserServices {
    @GET("582695f5100000560464ca40")
    Call<List<TodoResponse>> todo();

}