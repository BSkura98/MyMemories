package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.AddMemoryActivity;
import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesForDateTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoriesFragment extends Fragment {
    private RecyclerView memoryList;
    private Button dateButton;
    private FloatingActionButton addMemoryButton;
    private ImageView previousDayButton, nextDayButton;
    private CircularProgressIndicator memoriesFragmentProgressIndicator;
    private TextView messageTextView;

    private MemoryListAdapter adapter;
    private Date date = new Date();
    private GetMemoriesForDateTask currentTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        findViews(view);
        initValues();
        prepareViews();
        setListeners();

        return view;
    }

    private void findViews(View view){
        memoryList = view.findViewById(R.id.memoryList);
        dateButton = view.findViewById(R.id.dateButton);
        addMemoryButton = view.findViewById(R.id.addMemoryButton);
        previousDayButton = view.findViewById(R.id.previousDayButton);
        nextDayButton = view.findViewById(R.id.nextDayButton);
        memoriesFragmentProgressIndicator = view.findViewById(R.id.memoriesFragmentProgressIndicator);
        messageTextView = view.findViewById(R.id.noMemoriesTextView);
    }

    private void initValues(){
        if(getActivity()!=null){
            currentTask = new GetMemoriesForDateTask(getActivity(), date);
        }

        if(getActivity()!=null){
            String dateFromIntent=getActivity().getIntent().getStringExtra("date");
            if(dateFromIntent!=null){
                dateFromIntent = dateFromIntent.replace("T", " ");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                try {
                    date = formatter.parse(dateFromIntent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setListeners(){
        dateButton.setOnClickListener(v -> selectDate());

        addMemoryButton.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddMemoryActivity.class)));

        previousDayButton.setOnClickListener((view)->{
            date = new Date(date.getTime() - (24 * 3600000));
            String dateText = DateFormat.format("dd-MM-yyyy", date).toString();
            dateButton.setText(dateText);
            new Thread(() -> getMemories(date)).start();
        });

        nextDayButton.setOnClickListener((view)->{
            date = new Date(date.getTime() + (24 * 3600000));
            String dateText = DateFormat.format("dd-MM-yyyy", date).toString();
            dateButton.setText(dateText);
            new Thread(() -> getMemories(date)).start();
        });
    }

    private void prepareViews(){
        //date button
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateButton.setText(sdf.format(date));

        //recycler view with memory list
        new Thread(() -> getMemories(date)).start();

        //TextView with text "No memories for this date"
        messageTextView.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int LAUNCH_SECOND_ACTIVITY = 1;

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                new Thread(() -> getMemories(date)).start();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getMemories(Date date){
        try{
            if(getActivity()==null){
                throw new NullPointerException();
            }
            getActivity().runOnUiThread(()->{
                memoryList.setAdapter(null);
                messageTextView.setVisibility(View.GONE);
                memoriesFragmentProgressIndicator.setVisibility(View.VISIBLE);
            });
            GetMemoriesForDateTask task = new GetMemoriesForDateTask(getActivity(), date);
            currentTask = task;
            Memory[] memoryArray = task.execute().get();

            if(currentTask==task){
                if(memoryArray ==null){
                    getActivity().runOnUiThread(()->{
                        messageTextView.setVisibility(View.VISIBLE);
                        if(task.getError().contains("Unable to resolve host")){
                            messageTextView.setText("Problem with the Internet connection");
                        }else if(task.getError().contains("timeout")){
                            messageTextView.setText("Connection timed out");
                        }else{
                            messageTextView.setText("A problem occurred");
                        }
                        memoriesFragmentProgressIndicator.setVisibility(View.GONE);
                    });
                    return;
                }
                final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));

                getActivity().runOnUiThread(() -> {
                    if(memories.isEmpty()){
                        memoriesFragmentProgressIndicator.setVisibility(View.GONE);
                        messageTextView.setText("No memories for this date");
                        messageTextView.setVisibility(View.VISIBLE);
                    }else{
                        adapter = new MemoryListAdapter(
                                getContext(),
                                memories,
                                getActivity()
                        );
                        adapter.setFragment(this);
                        memoriesFragmentProgressIndicator.setVisibility(View.GONE);
                        memoryList.setAdapter(adapter);
                        memoryList.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });
            }
        }catch (Exception e){
            if(getActivity()!=null){
                getActivity().runOnUiThread(()->memoriesFragmentProgressIndicator.setVisibility(View.GONE));
            }
            e.printStackTrace();
        }
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if(getContext()==null){
            return;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year1, month1, date1) -> {

            date.setDate(date1);
            date.setMonth(month1);
            date.setYear(year1-1900);
            String dateText = DateFormat.format("dd-MM-yyyy", date).toString();

            dateButton.setText(dateText);

            new Thread(() -> getMemories(date)).start();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        datePickerDialog.show();
    }
}
