package com.javed.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.javed.todo.API.TodoResponse;
import com.javed.todo.Adapters.FragmentAdapter;
import com.javed.todo.Adapters.TodoAdapterToFragmentCommunicator;
import com.javed.todo.Adapters.TodoCompletedAdapter;
import com.javed.todo.Adapters.TodoPendingAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TodayFragment extends Fragment {
    Context context;
    TodoAdapterToFragmentCommunicator todoAdapterToFragmentCommunicator;
    FragmentAdapter fragmentAdapter;
    TodoPendingAdapter todoPendingAdapter;
    TodoCompletedAdapter todoCompletedAdapter;
    RecyclerView completedTodoRecyclerView;
    RecyclerView pendingTodoRecyclerView;
    FloatingActionButton addTodo;
    String dialogueStatus = "Hidden";
    Dialog dialog;
    List<TodoResponse> todoListToday;
    List<TodoResponse> todoListPending;
    List<TodoResponse> todoListCompleted;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String dateTextMilliSeconds, timeTextMilliSeconds;

    public TodayFragment(List<TodoResponse> todoList) {
        this.todoListToday = todoList;

    }

    public TodayFragment() {

    }

    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
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
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        completedTodoRecyclerView = view.findViewById(R.id.rvCompleted);
        pendingTodoRecyclerView = view.findViewById(R.id.rvPending);
        completedTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        pendingTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));

        addTodo = view.findViewById(R.id.btnAddTodo);

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG2", "onClick: addtodo");
                if (dialogueStatus.equals("Hidden")) {
                    showCustomDialog();
                    dialogueStatus = "Shown";
                    addTodo.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                } else if (dialogueStatus.equals("Shown")) {
                    dialog.show();
                    dialogueStatus = "Hidden";
                    addTodo.setImageResource(R.drawable.ic_add);
                }
            }
        });

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
                TodoResponse todoResponse = todoListPending.get(position);
                todoResponse.setStatus("COMPLETED");

                todoListCompleted.add(todoResponse);
                todoCompletedAdapter.notifyItemInserted(todoListCompleted.size() - 1);
                todoCompletedAdapter.notifyDataSetChanged();
                completedTodoRecyclerView.scrollToPosition(todoListCompleted.size() - 1);

                todoListPending.remove(position);
                todoPendingAdapter.notifyItemRemoved(position);
                todoPendingAdapter.notifyDataSetChanged();

//               todoCompletedAdapter.notifyItemInserted(todoListCompleted.size()-1);
            }


        }
        @Override
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red))
                    .addSwipeRightActionIcon(R.drawable.ic_done_)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

    void showCustomDialog() {
        //We have added a title in the custom layout. So let's disable the default title.
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_dialogue);

        //Initializing the views of the dialog.
        TextView btnYes = dialog.findViewById(R.id.btn_yes);
        EditText etDialogTodoDescription = dialog.findViewById(R.id.etDialogueDescription);
        Button setDate = dialog.findViewById(R.id.btnSetDate);
        Button setTime = dialog.findViewById(R.id.btnSetTime);
        TextView tvSetDate = dialog.findViewById(R.id.tvSetDate);
        TextView tvSetTime = dialog.findViewById(R.id.tvSetTime);


        setDate.setOnClickListener(view -> {
            handleDateButton(tvSetDate);
        });

        setTime.setOnClickListener(view -> {
            handleTimeButton(tvSetTime);
        });


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTodoDescription, id, milliSeconds;

                if (!etDialogTodoDescription.getText().toString().equals("") && !tvSetDate.getText().toString().equals("") && !tvSetTime.getText().toString().equals("")) {
                    dialogTodoDescription = etDialogTodoDescription.getText().toString();
                    id = String.valueOf(todoListPending.size());

                    milliSeconds = dateTextMilliSeconds + timeTextMilliSeconds;
                    String status = "PENDING";
                    dialog.setCanceledOnTouchOutside(false);
                    TodoResponse todoResponse = new TodoResponse();
                    todoResponse.setId(id);
                    todoResponse.setDescription(dialogTodoDescription);
                    todoResponse.setScheduledDate(milliSeconds);
                    todoResponse.setStatus(status);

                    todoListPending.add(todoResponse);
                    Log.d("TAG2", "onClick: " + milliSeconds);

                    if (todoListPending.size() < 2) {
                        todoPendingAdapter = new TodoPendingAdapter(todoListPending, getActivity());
                        pendingTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        pendingTodoRecyclerView.setHasFixedSize(true);
                        pendingTodoRecyclerView.setAdapter(todoPendingAdapter);
                    } else {
                        todoPendingAdapter.notifyItemInserted(todoListPending.size() - 1);
                        todoPendingAdapter.notifyItemChanged(todoListPending.size() - 1, todoListPending.size());
                        pendingTodoRecyclerView.scrollToPosition(todoListPending.size() - 1);

                        Log.d("TAG2", "onClick: 3" + dialogTodoDescription);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Fill all the fields  ", Toast.LENGTH_LONG).show();

                }
            }
        });

        dialog.show();
    }


    private void todoPending() {
        todoListPending = new ArrayList<TodoResponse>(todoListToday.subList(0, todoListToday.size()));

        Log.d("TAG2", "todoPending: todoListPending.size  " + todoListPending.size());

        String todayDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date());
        ListIterator<TodoResponse> iterCheckDate = todoListPending.listIterator();
        while (iterCheckDate.hasNext()) {
            if (Long.parseLong(iterCheckDate.next().getScheduledDate()) < Long.parseLong(todayDate)) {
                iterCheckDate.remove();
            }
        }
        ListIterator<TodoResponse> iterCheckStatus = todoListPending.listIterator();
        while (iterCheckStatus.hasNext()) {
            if (iterCheckStatus.next().getStatus().equals("COMPLETED")) {
                iterCheckStatus.remove();
            }
        }

        Collections.sort(todoListPending, new Comparator<TodoResponse>() {
            public int compare(TodoResponse obj1, TodoResponse obj2) {
                // ## Ascending order
                return obj2.getScheduledDate().compareToIgnoreCase(obj1.getScheduledDate());
            }
        });
        Log.d("TAG2", "onResponse: todoPending " + todoListPending.size());
        todoPendingAdapter = new TodoPendingAdapter(todoListPending, getActivity());
        pendingTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingTodoRecyclerView.setHasFixedSize(true);
        pendingTodoRecyclerView.setAdapter(todoPendingAdapter);


    }

    private void todoCompleted() {
        todoListCompleted = new ArrayList<TodoResponse>(todoListToday.subList(0, todoListToday.size()));
        Log.d("TAG2", "todoCompleted: todoListCompleted.size  " + todoListCompleted.size());


        String todayDate = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(new Date());
        ListIterator<TodoResponse> iterCheckDate = todoListCompleted.listIterator();
        while (iterCheckDate.hasNext()) {
            if (Long.parseLong(iterCheckDate.next().getScheduledDate()) < Long.parseLong(todayDate)) {
                iterCheckDate.remove();
            }
        }

        ListIterator<TodoResponse> iterCheckStatus = todoListCompleted.listIterator();
        while (iterCheckStatus.hasNext()) {
            if (iterCheckStatus.next().getStatus().equals("PENDING")) {
                iterCheckStatus.remove();
            }
        }
        Collections.sort(todoListCompleted, new Comparator<TodoResponse>() {
            public int compare(TodoResponse obj1, TodoResponse obj2) {
                // ## Ascending order
                return obj1.getScheduledDate().compareToIgnoreCase(obj2.getScheduledDate());
            }
        });


        todoCompletedAdapter = new TodoCompletedAdapter(todoListCompleted);
        completedTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        completedTodoRecyclerView.setHasFixedSize(true);
        completedTodoRecyclerView.setAdapter(todoCompletedAdapter);


    }

    private void handleDateButton(TextView setDate) {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String dateText = "";

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);
                dateText = DateFormat.format(" MMM dd, yyyy", calendar1).toString();
                String c = DateFormat.format("yyyyMMMdd", calendar1).toString();
                String c1 = "" + c.charAt(4) + c.charAt(5) + c.charAt(6);
                String[] monthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthNumber;
                for (monthNumber = 0; monthNumber < monthNames.length; monthNumber++) {
                    if (c1.equals(monthNames[monthNumber])) {
                        break;
                    }
                }
                if (monthNumber < 10) {
                    c1 = "0" + (monthNumber + 1);
                } else {
                    c1 = String.valueOf(monthNumber + 1);
                }


                dateTextMilliSeconds = "" + c.charAt(0) + c.charAt(1) + c.charAt(2) + c.charAt(3) + c1 + c.charAt(7) + c.charAt(8);
                Log.d("TAG2", "onDateSet: " + dateText);
                setDate.setText(dateText);
                if (!dateText.equals("")) {
                    setDate.setVisibility(View.VISIBLE);
                    setDate.setText(dateText);
                }
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();

    }

    private void handleTimeButton(TextView setTime) {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                String timeText = "";
//                Log.i("TAG2", "onTimeSet: " + hour + minute);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                timeText = DateFormat.format("h:mm a", calendar1).toString();
                Log.d("TAG2", "onTimeSet: " + timeText);
                timeTextMilliSeconds = DateFormat.format("hhmm", calendar1).toString();
                if (!timeText.equals("")) {
                    setTime.setVisibility(View.VISIBLE);
                    setTime.setText(timeText);
                }
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();

    }

}