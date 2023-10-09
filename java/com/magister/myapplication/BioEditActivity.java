package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BioEditActivity extends AppCompatActivity {
    private EditText editTextBio;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_edit);

        editTextBio = findViewById(R.id.editTextBio);
        saveButton = findViewById(R.id.saveButton);
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String currentBio = getIntent().getStringExtra("currentBio");
        editTextBio.setText(currentBio);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedBio();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveEditedBio();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEditedBio() {
        String editedBio = editTextBio.getText().toString();

        if (editedBio.length() > 250) {
            Toast.makeText(this, "Bio should not exceed 250 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userKey = snapshot.getKey();
                            if (userKey != null) {
                                Log.d("SaveBio", "Found userKey: " + userKey);
                                DatabaseReference userRef = databaseReference.child(userKey);
                                userRef.child("bio").setValue(editedBio)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("SaveBio", "Bio saved successfully");
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("editedBio", editedBio);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            } else {
                                                Log.e("SaveBio", "Failed to save bio: " + task.getException());
                                                Toast.makeText(BioEditActivity.this, "Failed to save bio.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("SaveBio", "Database error: " + databaseError.getMessage());
                        // Handle the error
                    }
                });
            }
        }
    }
}
