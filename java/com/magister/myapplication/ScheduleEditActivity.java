package com.magister.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleEditActivity extends AppCompatActivity {

    private TextView startTextView;
    private TextView endTextView;
    private Button saveSchedButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private Date selectedStartDate;
    private Date selectedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);

        startTextView = findViewById(R.id.startTextView);
        endTextView = findViewById(R.id.endTextView);
        saveSchedButton = findViewById(R.id.saveSchedButton);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");

        String currentSched = getIntent().getStringExtra("currentSched");
        if (currentSched != null) {
            String[] schedParts = currentSched.split(" - ");
            if (schedParts.length == 2) {
                startTextView.setText(schedParts[0]);
                endTextView.setText(schedParts[1]);
            }
        }

        startTextView.setOnClickListener(v -> showDatePicker(true));
        endTextView.setOnClickListener(v -> showDatePicker(false));
        saveSchedButton.setOnClickListener(v -> saveScheduleToDatabase());
    }

    private void showDatePicker(boolean isStart) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            if (isStart) {
                selectedStartDate = calendar.getTime();
                showTimePicker(true);
            } else {
                selectedEndDate = calendar.getTime();
                showTimePicker(false);
            }
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void showTimePicker(boolean isStartTime) {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        builder.setTimeFormat(TimeFormat.CLOCK_12H);
        builder.setTitleText("Select Time");

        MaterialTimePicker picker = builder.build();
        picker.addOnPositiveButtonClickListener(v -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(isStartTime ? selectedStartDate : selectedEndDate);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            if (!isStartTime) {
                selectedEndDate = calendar.getTime();
                checkAndFormatTimeDifference();
            } else {
                selectedStartDate = calendar.getTime();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String formattedTime = dateFormat.format(calendar.getTime());

            if (isStartTime) {
                startTextView.setText(formattedTime);
            } else {
                endTextView.setText(formattedTime);
            }
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    private void checkAndFormatTimeDifference() {
        if (selectedStartDate.after(selectedEndDate)) {
            Toast.makeText(this, "End time cannot be before start time.", Toast.LENGTH_SHORT).show();
            endTextView.setText(""); // Clear the end time display
            selectedEndDate = null; // Reset the selected end date
        }
    }


    private void saveScheduleToDatabase() {
        if (selectedStartDate != null && selectedEndDate != null) {
            if (selectedStartDate.after(selectedEndDate)) {
                Toast.makeText(this, "End time cannot be before start time.", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String formattedStart = dateFormat.format(selectedStartDate);
            String formattedEnd = dateFormat.format(selectedEndDate);
            String schedule = formattedStart + " - " + formattedEnd;

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
                                    userRef.child("schedule").setValue(schedule)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("editedSched", schedule);
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(ScheduleEditActivity.this, "Failed to save schedule.", Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(this, "Please select both start and end times.", Toast.LENGTH_SHORT).show();
        }
    }
}
