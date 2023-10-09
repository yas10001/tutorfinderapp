package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Final_dashboard extends AppCompatActivity {
    private ImageView image;
    private TextView textView;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_dashboard);

        textView = findViewById(R.id.textView37);
        image = findViewById(R.id.imageView23);
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Final_dashboard.this, TutorEditProfNEW.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot tutorSnapshot : dataSnapshot.getChildren()) {
                                String firstName = tutorSnapshot.child("firstName").getValue(String.class);
                                textView.setText(firstName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
