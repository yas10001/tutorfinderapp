package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PhoneEditActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_edit);

        EditText editTextPhone = findViewById(R.id.editTextPhone);
        Button btnSave = findViewById(R.id.btnSave);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");

        Intent intent = getIntent();
        if (intent != null) {
            String currentPhone = intent.getStringExtra("currentPhone");
            editTextPhone.setText(currentPhone);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedPhone = editTextPhone.getText().toString();

                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    if (email != null) {
                        databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String userKey = snapshot.getKey();
                                    if (userKey != null) {
                                        DatabaseReference userRef = databaseReference.child(userKey);
                                        userRef.child("acc/phone").setValue(editedPhone, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("editedPhone", editedPhone);
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                } else {
                                                    // Handle the error
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle the error
                            }
                        });
                    }
                }
            }
        });
    }
}
