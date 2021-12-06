package com.javed.todo.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.javed.todo.API.TodoResponse;
import com.javed.todo.R;

import java.util.List;

public class TodoPendingAdapter extends RecyclerView.Adapter<TodoPendingAdapter.MyViewHolder> {

    private List<TodoResponse> todoPendingList;

    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDate, tvTime;
        public ConstraintLayout clPendingListItems;


        public MyViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTodoTitle);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            clPendingListItems = (ConstraintLayout) view.findViewById(R.id.clPendingListItems);

        }
    }


    public TodoPendingAdapter(List<TodoResponse> todoPendingListList, Context context) {
        this.todoPendingList = todoPendingListList;
        this.context = context;
        Log.d("TAG2", "TodoPendingAdapter: " + todoPendingList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_list_items, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (todoPendingList.size() != 0) {
            if (holder == null) {
                holder.clPendingListItems.setVisibility(View.GONE);
            }
            holder.clPendingListItems.setVisibility(View.VISIBLE);


            holder.tvTitle.setText(todoPendingList.get(position).getDescription());

            String milliSeconds = todoPendingList.get(position).getScheduledDate();
            String year = "";
            String month = "";
            String date = "";
            String time = "";
            String monthName;
            String hours = "";
            String minutes = "";
            String[] monthNames = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
            Log.d("TAG2", "onBindViewHolder TodoPending adapter: ");
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
                if (i < 10) {
                    hours = hours + milliSeconds.charAt(i);
                } else {
                    minutes = minutes + milliSeconds.charAt(i);
                }
            }
            time = hours + ":" + minutes;
            monthName = monthNames[Integer.parseInt(month) - 1];
            holder.tvDate.setText(date + "" + "," + monthName + "" + "," + year + "\n");
            holder.tvTime.setText(time);

            holder.clPendingListItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


    }


    @Override
    public int getItemCount() {
        Log.d("TAG2", "getItemCount: todoPending" + todoPendingList.size());
        return todoPendingList.size();
    }


    private void removeItem(int position) {
        todoPendingList.remove(position);
    }


}