package com.javed.todo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.javed.todo.API.Api;
import com.javed.todo.API.TodoResponse;
import com.javed.todo.Adapters.FragmentAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;
    List<TodoResponse> todoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tlMain);
        viewPager2 = findViewById(R.id.vpMain);
        connectNetwork();

        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Later"));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.Dark_blue));
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey), getResources().getColor(R.color.teal_700));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }

    private void connectNetwork() {
        try {

            Call<List<TodoResponse>> call = Api.getUserService().todo();
            call.enqueue(new Callback<List<TodoResponse>>() {
                @Override
                public void onResponse(Call<List<TodoResponse>> call, Response<List<TodoResponse>> response) {
                    if (response.isSuccessful()) {
                        todoList = response.body();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), todoList);
                        viewPager2.setAdapter(fragmentAdapter);
                        Log.d("TAG2", "onResponse:connectNetwork " + todoList.size());
                    }
                }


                @Override
                public void onFailure(Call<List<TodoResponse>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "An error has occured  " + t.toString(), Toast.LENGTH_LONG).show();
                    Log.d("TAG2", "onFailureconnectNetwork: " + t.toString());
                }

            });
        } catch (
                Exception e) {
            Log.d("TAG2", "connectNetwork Exception" + e.toString());
        }

    }
}