package com.bartlomiejskura.mymemories.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MemoriesFragment extends Fragment {
    private RecyclerView memoryList;
    private Button dateButton;
    private FloatingActionButton addMemoryButton;
    private ImageView previousDayButton, nextDayButton;

    private MemoryListAdapter adapter;
    private Date date = new Date();
    private List<Memory> memories;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        findViews(view);

        //if(savedInstanceState != null){
        //    restoreDataAfterRotation(savedInstanceState);
        //}else{
        //    new Thread(() -> getAllMemoriesForDate(date)).start();
        //}

        prepareViews(savedInstanceState);
        setListeners();

        return view;
    }


    private void findViews(View view){
        memoryList = view.findViewById(R.id.memoryList);
        dateButton = view.findViewById(R.id.dateButton);
        addMemoryButton = view.findViewById(R.id.addMemoryButton);
        previousDayButton = view.findViewById(R.id.previousDayButton);
        nextDayButton = view.findViewById(R.id.nextDayButton);
    }

    private void prepareViews(Bundle savedInstanceState){
        if(savedInstanceState==null){
            //date button
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dateButton.setText(sdf.format(date));

            //recycler view with memory list
            new Thread(() -> getAllMemoriesForDate(date)).start();
        }else{
            memories = gson.fromJson(savedInstanceState.getString("memories"), new TypeToken<List<Memory>>(){}.getType());
            createMemoryListAdapter();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try{
                date = formatter.parse(savedInstanceState.getString("date"));
            }catch (Exception e){
                e.printStackTrace();
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
            new Thread(() -> getAllMemoriesForDate(date)).start();
        });

        nextDayButton.setOnClickListener((view)->{
            date = new Date(date.getTime() + (24 * 3600000));
            String dateText = DateFormat.format("dd-MM-yyyy", date).toString();
            dateButton.setText(dateText);
            new Thread(() -> getAllMemoriesForDate(date)).start();
        });
    }

    private void restoreDataAfterRotation(Bundle savedInstanceState){
        memories = gson.fromJson(savedInstanceState.getString("memories"), new TypeToken<List<Memory>>(){}.getType());
        createMemoryListAdapter();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try{
            date = formatter.parse(savedInstanceState.getString("date"));
           // System.out.println("restoreDataAfterRotation " + formatter.format(formatter.parse(savedInstanceState.getString("date"))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String memoriesJson = gson.toJson(memories);
        outState.putString("memories", memoriesJson);
        outState.putString("date", formatter.format(date));
        //System.out.println("onSaveInstanceState "+ formatter.format(date));
    }

    private void getAllMemoriesForDate(Date date){
        try{
            GetMemoriesForDateTask task = new GetMemoriesForDateTask(getActivity(), date);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            memories = new ArrayList<>(Arrays.asList(memoryArray));
            getActivity().runOnUiThread(this::createMemoryListAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createMemoryListAdapter(){
        adapter = new MemoryListAdapter(
                getContext(),
                memories,
                getActivity()
        );
        memoryList.setAdapter(adapter);
        memoryList.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year1, month1, date1) -> {

            date.setDate(date1);
            date.setMonth(month1);
            date.setYear(year1-1900);
            String dateText = DateFormat.format("dd-MM-yyyy", date).toString();

            dateButton.setText(dateText);

            new Thread(() -> getAllMemoriesForDate(date)).start();
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }
}
