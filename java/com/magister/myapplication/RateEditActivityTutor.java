package com.magister.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateEditActivityTutor extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private EditText editRateInput;
    private Button saveRateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_edit_tutor);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");

        editRateInput = findViewById(R.id.editRateInput);
        saveRateButton = findViewById(R.id.saveRateButton);

        saveRateButton.setOnClickListener(v -> saveRateToDatabase());
    }

    private void saveRateToDatabase() {
        String newRate = editRateInput.getText().toString().trim();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                if (!newRate.isEmpty()) {
                    double rateValue = Double.parseDouble(newRate);
                    if (rateValue >= 0) {
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE); // Show the progress bar

                        databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String userKey = snapshot.getKey();
                                    if (userKey != null) {
                                        DatabaseReference userRef = databaseReference.child(userKey);

                                        userRef.child("rate").setValue(newRate)
                                                .addOnCompleteListener(task -> {
                                                    progressBar.setVisibility(View.GONE); // Hide the progress bar

                                                    if (task.isSuccessful()) {
                                                        setResult(RESULT_OK, new Intent().putExtra("editedRate", newRate));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RateEditActivityTutor.this, "Failed to save rate.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(RateEditActivityTutor.this, "Rate cannot be negative.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
