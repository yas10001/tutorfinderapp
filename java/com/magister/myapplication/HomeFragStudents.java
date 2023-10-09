package com.magister.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class HomeFragStudents extends Fragment {
    private TextView nameTextView;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private EditText searchEditText;
    private GridLayout subjectsGridLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank2, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTextView = view.findViewById(R.id.names);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        searchEditText = view.findViewById(R.id.editTextTextPersonName6);
        subjectsGridLayout = view.findViewById(R.id.subjectsGridLayout);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                performSearch(searchText);
            }
        });

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot tutorSnapshot : dataSnapshot.getChildren()) {
                                String firstName = tutorSnapshot.child("firstName").getValue(String.class);
                                String fullName = firstName;
                                nameTextView.setText(fullName);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
        DatabaseReference subjectsRef = FirebaseDatabase.getInstance().getReference("subjects");
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectsGridLayout.removeAllViews();

                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    SubjectGet subjectGet = subjectSnapshot.getValue(SubjectGet.class);
                    if (subjectGet != null) {
                        addSubjectToGrid(subjectGet.getName(), subjectGet.getImageUrl());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void performSearch(String query) {
        for (int i = 0; i < subjectsGridLayout.getChildCount(); i++) {
            View subjectView = subjectsGridLayout.getChildAt(i);
            TextView subjectTextView = subjectView.findViewById(R.id.subjectTitleTextView);
            String subject = subjectTextView.getText().toString().trim().toLowerCase();

            if (subject.contains(query.toLowerCase())) {
                subjectView.setVisibility(View.VISIBLE);
            } else {
                subjectView.setVisibility(View.GONE);
            }
        }
    }
    private void addSubjectToGrid(String subjectName, String imageUrl) {
        View subjectView = LayoutInflater.from(requireContext()).inflate(R.layout.subject_item, subjectsGridLayout, false);
        TextView subjectTitleTextView = subjectView.findViewById(R.id.subjectTitleTextView);
        ImageView subjectImageView = subjectView.findViewById(R.id.subjectImageView);
        subjectTitleTextView.setText(subjectName);
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this).load(imageUrl).into(subjectImageView);
        }
        subjectsGridLayout.addView(subjectView);
    }
}
