package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Home_Admin_Frag extends Fragment {

    private TextView tutorsTextView;
    private TextView studentsTextView;
    private TextView parentsTextView;
    private TextView subjectTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        tutorsTextView = view.findViewById(R.id.tutors);
        studentsTextView = view.findViewById(R.id.students);
        parentsTextView = view.findViewById(R.id.parents);
        subjectTextView = view.findViewById(R.id.subjects);

        tutorsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Tutor2.class);
                startActivity(intent);
            }
        });

        studentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), studentloob.class);
                startActivity(intent);
            }
        });

        parentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), parentloob.class);
                startActivity(intent);
            }
        });

        subjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Subjects.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
