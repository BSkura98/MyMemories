package com.bartlomiejskura.mymemories;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ChangeCategoryNameDialog extends AppCompatDialogFragment {
    private EditText categoryNameEditText;

    private DialogListener listener;
    private String categoryName;

    ChangeCategoryNameDialog(String categoryName){
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_category_name, null);

        categoryNameEditText = view.findViewById(R.id.categoryNameEditText);
        categoryNameEditText.setText(categoryName);

        builder.setView(view)
                .setNegativeButton("cancel", (dialog, which) -> {

                })
                .setPositiveButton("ok", (dialog, which)->{
                    String categoryName = categoryNameEditText.getText().toString();
                    new Thread(() -> listener.applyCategoryName(categoryName)).start();
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener{
        void applyCategoryName(String name);
    }
}
