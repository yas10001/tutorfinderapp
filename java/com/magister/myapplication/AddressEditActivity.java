package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class AddressEditActivity extends AppCompatActivity {

    private EditText editAddressInput;
    private Button saveAddressButton;

    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        editAddressInput = findViewById(R.id.editAddressInput);
        saveAddressButton = findViewById(R.id.saveAddressButton);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");

        String currentAddress = getIntent().getStringExtra("currentAddress");
        editAddressInput.setText(currentAddress);

        saveAddressButton.setOnClickListener(v -> saveAddressToDatabase());
    }

    private void saveAddressToDatabase() {
        String newAddress = editAddressInput.getText().toString().trim();

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
                                userRef.child("address").setValue(newAddress)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("editedAddress", newAddress);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            } else {
                                                // Handle error
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
