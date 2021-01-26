package com.bartlomiejskura.mymemories.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.adapter.MemoryListAdapter;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.task.GetMemoriesForDateTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatesFragment extends Fragment {
    private RecyclerView memoryList;
    private Button dateButton;

    private MemoryListAdapter adapter;
    private Date date = new Date();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dates, container, false);

        memoryList = view.findViewById(R.id.memoryList);
        dateButton = view.findViewById(R.id.dateButton);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllMemoriesForDate(date);
            }
        }).start();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateButton.setText(sdf.format(date));

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        return view;
    }

    public void getAllMemoriesForDate(Date date){
        try{
            GetMemoriesForDateTask task = new GetMemoriesForDateTask(getActivity(), date);
            Memory[] memoryArray = task.execute().get();
            if(memoryArray ==null){
                return;
            }
            final List<Memory> memories = new ArrayList<>(Arrays.asList(memoryArray));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new MemoryListAdapter(
                            getContext(),
                            memories,
                            getActivity()
                    );
                    memoryList.setAdapter(adapter);
                    memoryList.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int date1) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year1);
                calendar1.set(Calendar.MONTH, month1);
                calendar1.set(Calendar.DATE, date1);
                String dateText = DateFormat.format("dd-MM-yyyy", calendar1).toString();

                dateButton.setText(dateText);
                date.setDate(date1);
                date.setMonth(month1);
                date.setYear(year1-1900);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getAllMemoriesForDate(date);
                    }
                }).start();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }
}
