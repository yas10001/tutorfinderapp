package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class messageFragment extends Fragment {

    private TextView profileTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        profileTextView = rootView.findViewById(R.id.proff);
        profileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfSetsActivity();
            }
        });

        return rootView;
    }

    private void openProfSetsActivity() {
        Intent intent = new Intent(getActivity(), Prof_Sets.class);
        startActivity(intent);
    }
}
