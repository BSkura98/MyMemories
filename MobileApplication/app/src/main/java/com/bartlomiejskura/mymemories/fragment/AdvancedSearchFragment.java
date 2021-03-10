package com.bartlomiejskura.mymemories.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bartlomiejskura.mymemories.R;
import com.bartlomiejskura.mymemories.SearchMemoryActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class AdvancedSearchFragment extends Fragment {
    private Button normalSearchButton, addCategoryButton, searchButton;
    private Button[] dateButtons = new Button[datesNumber];
    private ImageButton[] deleteButtons = new ImageButton[datesNumber];
    private CheckBox highPriorityCheckBox, mediumPriorityCheckBox, lowPriorityCheckBox, yesPublicCheckBox, noPublicCheckBox, yesSharedMemoriesCheckBox, noSharedMemoriesCheckBox, withImageCheckBox, withoutImageCheckBox;
    private EditText categoryEditText;
    private ChipGroup categoriesChipGroup;

    private Calendar[] dateCalendars = {null, null, null, null};
    private List<Integer> priorityList = new LinkedList<>();
    private Boolean[] booleanValues = {null, null, null};
    private List<String> categories = new LinkedList<>();

    private final static int datesNumber = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);

        initElements(view);
        setListeners();
        setElements();

        return view;
    }

    private void initElements(View view){
        normalSearchButton = view.findViewById(R.id.normalSearchButton);
        dateButtons[0] = view.findViewById(R.id.creationDateStartButton);
        dateButtons[1] = view.findViewById(R.id.creationDateEndBottom);
        dateButtons[2] = view.findViewById(R.id.dateStartButton);
        dateButtons[3] = view.findViewById(R.id.dateEndBottom);
        addCategoryButton = view.findViewById(R.id.addCategoryButton);
        deleteButtons[0] = view.findViewById(R.id.deleteButton1);
        deleteButtons[1] = view.findViewById(R.id.deleteButton2);
        deleteButtons[2] = view.findViewById(R.id.deleteButton3);
        deleteButtons[3] = view.findViewById(R.id.deleteButton4);
        highPriorityCheckBox = view.findViewById(R.id.highPriorityCheckBox);
        mediumPriorityCheckBox = view.findViewById(R.id.mediumPriorityCheckBox);
        lowPriorityCheckBox = view.findViewById(R.id.lowPriorityCheckBox);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        categoriesChipGroup = view.findViewById(R.id.categoriesChipGroup);
        yesPublicCheckBox = view.findViewById(R.id.yesPublicCheckBox);
        noPublicCheckBox = view.findViewById(R.id.noPublicCheckBox);
        yesSharedMemoriesCheckBox = view.findViewById(R.id.yesSharedMemoriesCheckBox);
        noSharedMemoriesCheckBox = view.findViewById(R.id.noSharedMemoriesCheckBox);
        withImageCheckBox = view.findViewById(R.id.withImageCheckBox);
        withoutImageCheckBox = view.findViewById(R.id.withoutImageCheckBox);
        searchButton = view.findViewById(R.id.searchButton);
    }

    private void setListeners(){
        normalSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchMemoryActivity)getActivity()).changeFragment(false);
            }
        });

        for(int i=0;i<datesNumber;i++){
            final int finalI = i;
            dateButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDate(finalI);
                }
            });
        }

        for(int i=0;i<datesNumber;i++){
            final int finalI = i;
            deleteButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateButtons[finalI].setText("Select");
                    deleteButtons[finalI].setVisibility(View.GONE);
                    dateCalendars[finalI] = null;
                }
            });
        }

        highPriorityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    priorityList.add(90);
                }else{
                    priorityList.remove(Integer.valueOf(90));
                }
            }
        });

        mediumPriorityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    priorityList.add(50);
                }else{
                    priorityList.remove(Integer.valueOf(50));
                }
            }
        });

        lowPriorityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    priorityList.add(10);
                }else{
                    priorityList.remove(Integer.valueOf(10));
                }
            }
        });

        yesPublicCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(yesPublicCheckBox, noPublicCheckBox, 0, true));
        noPublicCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(noPublicCheckBox, yesPublicCheckBox, 0, false));
        yesSharedMemoriesCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(yesSharedMemoriesCheckBox, noSharedMemoriesCheckBox, 1, true));
        noSharedMemoriesCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(noSharedMemoriesCheckBox, yesSharedMemoriesCheckBox, 1, false));
        withImageCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(withImageCheckBox, withoutImageCheckBox, 2, true));
        withoutImageCheckBox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(withoutImageCheckBox, withImageCheckBox, 2, false));

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categoryEditText.getText().toString().toLowerCase();
                if(!categories.contains(category)||category.isEmpty()){
                    initChip(category);
                    categories.add(category);
                }
                categoryEditText.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchMemoryActivity)getActivity()).startSearchResultsActivity(((SearchMemoryActivity)getActivity()).getQuery());
            }
        });
    }

    private void setElements(){
        for(int i=0;i<datesNumber;i++){
            if(dateCalendars[i]==null){
                deleteButtons[i].setVisibility(View.GONE);
            }else{
                String dateText = DateFormat.format("dd-MM-yyyy", dateCalendars[i]).toString();
                dateButtons[i].setText(dateText);
            }
        }

        for(String category:categories){
            initChip(category);
        }
    }

    private void initChip(String category){
        Chip chip = (Chip)LayoutInflater.from(getContext()).inflate(R.layout.chip_category, null, false);
        chip.setText(category);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesChipGroup.removeView(v);
                categories.remove(((Chip)v).getText().toString());
            }
        });
        chip.setCheckable(false);
        categoriesChipGroup.addView(chip);
    }

    private void selectDate(final int dateId) {
        int year;
        int month;
        int date;

        if(dateCalendars[dateId]!=null){
            year = dateCalendars[dateId].get(Calendar.YEAR);
            month = dateCalendars[dateId].get(Calendar.MONTH);
            date = dateCalendars[dateId].get(Calendar.DATE);
        }else{
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            date = Calendar.getInstance().get(Calendar.DATE);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year1, int month1, int date) {
                dateCalendars[dateId] = Calendar.getInstance();
                dateCalendars[dateId].set(Calendar.YEAR, year1);
                dateCalendars[dateId].set(Calendar.MONTH, month1);
                dateCalendars[dateId].set(Calendar.DATE, date);
                String dateText = DateFormat.format("dd-MM-yyyy", dateCalendars[dateId]).toString();

                dateButtons[dateId].setText(dateText);
                deleteButtons[dateId].setVisibility(View.VISIBLE);
            }
        }, year, month, date);

        datePickerDialog.show();
    }

    public Calendar[] getDateCalendars() {
        return dateCalendars;
    }

    public List<Integer> getPriorityList() {
        return priorityList;
    }

    public Boolean getPublicToFriends() {
        return booleanValues[0];
    }

    public Boolean getSharedMemories() {
        return booleanValues[1];
    }

    public Boolean getWithImage() {
        return booleanValues[2];
    }

    public List<String> getCategories() {
        return categories;
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private CheckBox checkBox1, checkBox2;
        private int booleanValueId;

        private final Boolean onCheckedValue;

        public MyOnCheckedChangeListener(CheckBox checkBox1, CheckBox checkBox2, int booleanValueId, Boolean onCheckedValue){
            this.checkBox1 = checkBox1;
            this.checkBox2 = checkBox2;
            this.booleanValueId = booleanValueId;
            this.onCheckedValue = onCheckedValue;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(checkBox2.isChecked()&&isChecked){
                checkBox2.setOnCheckedChangeListener(null);
                checkBox2.setChecked(false);
                checkBox2.setOnCheckedChangeListener(new MyOnCheckedChangeListener(checkBox2, checkBox1, booleanValueId, !onCheckedValue));
            }
            if(!isChecked){
                booleanValues[booleanValueId] = null;
            }else{
                booleanValues[booleanValueId] = onCheckedValue;
            }
        }
    }
}