package com.bartlomiejskura.mymemories.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bartlomiejskura.mymemories.AddMemoryActivity;
import com.bartlomiejskura.mymemories.LoginActivity;
import com.bartlomiejskura.mymemories.R;

public class RecentEntriesFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_entries, container, false);

        Button addMemoryButton = view.findViewById(R.id.addMemoryButton);

        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddMemoryActivity.class));
            }
        });

        return view;
    }
}
