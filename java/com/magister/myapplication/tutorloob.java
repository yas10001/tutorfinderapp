package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.magister.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class tutorloob extends AppCompatActivity implements TutorAdapter.OnItemClickListener {

    private DatabaseReference usersRef;
    private RecyclerView recyclerView;
    private TutorAdapter tutorAdapter;
    private List<TutorInfo> tutorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorloob);

        usersRef = FirebaseDatabase.getInstance().getReference().child("pending_tutors");
        recyclerView = findViewById(R.id.tutorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tutorList = new ArrayList<>();
        tutorAdapter = new TutorAdapter(tutorList);
        tutorAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(tutorAdapter);

        retrievePendingTutors();
    }


    private void retrievePendingTutors() {

        Query tutorQuery = usersRef.orderByChild("status").equalTo("pending");
        tutorQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tutorList.clear(); // Clear the list before adding new tutors
                    for (DataSnapshot tutorSnapshot : dataSnapshot.getChildren()) {
                        TutorInfo tutor = tutorSnapshot.getValue(TutorInfo.class);
                        if (tutor != null) {
                            tutorList.add(tutor);
                        }
                    }
                    tutorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(tutorloob.this, "No pending tutors found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(tutorloob.this, "Failed to retrieve pending tutor data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemClick(TutorInfo tutor) {
        Intent intent = new Intent(tutorloob.this, TutorDetailsActivity.class);
        intent.putExtra("tutor", tutor);
        startActivity(intent);
    }
}
