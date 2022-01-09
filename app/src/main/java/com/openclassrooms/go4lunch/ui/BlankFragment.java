package com.openclassrooms.go4lunch.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openclassrooms.go4lunch.R;

public class BlankFragment extends Fragment {

    private String title;

    public BlankFragment(String title) {
        this.title = title;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.blank_fragment, container, false);
        TextView blankTextView = root.findViewById(R.id.blank_fragment_text);
        blankTextView.setText(title);
        return root;
    }
}