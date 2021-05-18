package com.example.instagramclone.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.instagramclone.R;

public class ConfirmPasswordDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password,container,false);
        TextView confirmDialog = view.findViewById(R.id.dialog_confirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                Toast.makeText(getActivity(),"Confirmed !",Toast.LENGTH_SHORT).show();
            }
        });
        TextView cancelDialog = view.findViewById(R.id.dialog_cancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                Toast.makeText(getActivity(), "Cancelled !", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
