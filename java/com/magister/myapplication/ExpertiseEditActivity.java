package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExpertiseEditActivity extends AppCompatActivity {

    private Button saveExpertiseButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private Set<String> selectedExpertise = new HashSet<>(); // Initialize the set for selected expertise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expertise_edit);

        saveExpertiseButton = findViewById(R.id.saveExpertiseButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        selectedExpertise.addAll(intent.getStringArrayListExtra("currentExpertise"));

        Spinner spinner = findViewById(R.id.expertiseSpinner);
        ArrayAdapter<CharSequence> adapter = createCustomAdapter();
        spinner.setAdapter(adapter);

        for (int i = 0; i < adapter.getCount(); i++) {
            if (selectedExpertise.contains(adapter.getItem(i).toString())) {
                spinner.setSelection(i);
            }
        }

        saveExpertiseButton.setOnClickListener(v -> saveExpertiseToDatabase());
    }

    private ArrayAdapter<CharSequence> createCustomAdapter() {
        return new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.list)) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull android.view.ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.rec, parent, false);
                }

                CheckBox checkbox = convertView.findViewById(R.id.checkbox);
                TextView textView = convertView.findViewById(R.id.spinner_item_text);

                String item = getItem(position).toString();
                textView.setText(item);

                checkbox.setOnCheckedChangeListener(null);
                checkbox.setChecked(selectedExpertise.contains(item)); // Set the initial state

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (!selectedExpertise.contains(item)) {
                            selectedExpertise.add(item);
                        }
                    } else {
                        selectedExpertise.remove(item);
                    }
                });

                return convertView;
            }
        };
    }


    private void saveExpertiseToDatabase() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userKey = snapshot.getKey();
                            if (userKey != null) {
                                DatabaseReference userRef = databaseReference.child(userKey);

                                String selectedExpertiseString = TextUtils.join(", ", selectedExpertise);

                                userRef.child("expertise").setValue(selectedExpertiseString)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                setResultAndFinish();
                                            } else {
                                                // Handle the case where saving the expertise failed
                                                // You can show an error message here
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled event if necessary
                    }
                });
            }
        }
    }


    private void setResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("editedExpertise", new ArrayList<>(selectedExpertise));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
