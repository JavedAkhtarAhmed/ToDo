package com.javed.todo.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.javed.todo.API.TodoResponse;
import com.javed.todo.R;

import java.util.List;

public class TodoCompletedAdapter extends RecyclerView.Adapter<TodoCompletedAdapter.MyViewHolder> {

    private List<TodoResponse> todoCompletedList;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDate,tvTime;
        LinearLayout linear_share;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTodoTitle);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
        }
    }


    public TodoCompletedAdapter(List<TodoResponse> todoCompletedList) {
        this.todoCompletedList = todoCompletedList;
        Log.d("TAG2", "TodoCompletedAdapter: "+todoCompletedList.size());
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTitle.setText(todoCompletedList.get(position).getDescription());

        String milliSeconds = todoCompletedList.get(position).getScheduledDate();
        String year = "";
        String month = "";
        String date = "";
        String time = "";
        String monthName;
        String hours="";
        String minutes="";
        String[] monthNames = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Log.d("TAG2", "onBindViewHolder TodoCompleted adapter: ");
        for (int i = 0; i < 4; i++) {
            year = year + milliSeconds.charAt(i);
        }

        for (int i = 4; i < 6; i++) {
            month = month + milliSeconds.charAt(i);
        }
        ;
        for (int i = 6; i < 8; i++) {
            date = date + milliSeconds.charAt(i);
        }
        for (int i = 8; i < 12; i++) {
            if(i<10){
                hours = hours + milliSeconds.charAt(i);
            }
            else{
                minutes = minutes+milliSeconds.charAt(i);
            }
        }
        time = hours+":"+minutes;
        monthName = monthNames[Integer.parseInt(month) - 1];
        holder.tvDate.setText(date + "" + "," + monthName + "" + "," + year + "\n");
        holder.tvTime.setText(time);

    }


    @Override
    public int getItemCount() {
        return todoCompletedList.size();
    }
}