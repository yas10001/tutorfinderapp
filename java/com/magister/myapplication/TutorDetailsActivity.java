package com.magister.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class TutorDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView roleTextView;
    private TextView areaOfExpertiseTextView;
    private String firstName;
    private String lastName;
    private TextView ageTextView;
    private TextView tutorImageView;
    private String selectedExpertise;
    private String email;
    private Button accept;
    private Button decline;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        nameTextView = findViewById(R.id.titleName);
        emailTextView = findViewById(R.id.emailTextView);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        ageTextView = findViewById(R.id.ageTextView);
        roleTextView = findViewById(R.id.roleTextView);
        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);
        tutorImageView = findViewById(R.id.credentialsTextView);
        areaOfExpertiseTextView = findViewById(R.id.expertiseTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("tutor")) {
            TutorInfo tutor = (TutorInfo) intent.getSerializableExtra("tutor");
            if (tutor != null) {
                firstName = tutor.getFirstName();
                lastName = tutor.getLastName();
                email = tutor.getAcc().getEmail();
                String address = tutor.getAddress();
                String phone = tutor.getAcc().getPhone();
                String role = tutor.getAcc().getRole();
                String imageUrl = tutor.getFileUrl();
                String expertise = tutor.getExpertise();
                String age = tutor.getDateOfBirth();

                displayTutorData(firstName, lastName, email, address, phone, role, imageUrl, expertise, age);
            } else {
                Toast.makeText(this, "Invalid tutor data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No tutor data found", Toast.LENGTH_SHORT).show();
        }

        accept.setOnClickListener(view -> {
            updateTutorStatus(true);
        });

        decline.setOnClickListener(view -> {
            updateTutorStatus(false);
        });
    }

    private void displayTutorData(String firstName, String lastName, String email, String address, String phone, String role, String imageUrl, String expertise, String age) {
        String fullName = firstName + " " + lastName;
        String emailAddress = "Email: " + email;
        String fullAddress = "Address: " + address;
        String phoneNumber = "Phone: " + phone;
        String userRole = "Role: " + role;
        String ageOfTutor = "Date Of Birth: " + age;

        nameTextView.setText(fullName);
        emailTextView.setText(emailAddress);
        addressTextView.setText(fullAddress);
        ageTextView.setText(ageOfTutor);
        phoneTextView.setText(phoneNumber);
        roleTextView.setText(userRole);

        if (expertise != null && !expertise.isEmpty()) {
            areaOfExpertiseTextView.setText("Expertise: " + expertise);
        } else {
            areaOfExpertiseTextView.setVisibility(View.GONE); // Hide the expertise field if empty
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Display the URL in tutorUrlTextView
            tutorImageView.setText(imageUrl);
            tutorImageView.setOnClickListener(v -> {
                // Open the URL in a browser when clicked
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                startActivity(browserIntent);
            });
        } else {
            tutorImageView.setVisibility(View.GONE); // Hide the tutorUrlTextView if empty
        }
    }

    private void updateTutorStatus(boolean isAccepted) {
        String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();
        DatabaseReference tutorRef = mDatabase.child("pending_tutors").child(userID);

        String status = isAccepted ? "accepted" : "rejected";

        tutorRef.child("status").setValue(status)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (isAccepted) {
                            Toast.makeText(this, "Tutor accepted", Toast.LENGTH_SHORT).show();
                            sendNotificationToTutor(email, true);
                            tutorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    TutorInfo tutorInfo = snapshot.getValue(TutorInfo.class);
                                    if (tutorInfo != null) {
                                        moveTutorToAccepted(userID, tutorInfo);
                                        removeTutorFromPending(userID);
                                    } else {
                                        Toast.makeText(TutorDetailsActivity.this, "Failed to retrieve tutor information", Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(TutorDetailsActivity.this, "Failed to retrieve tutor information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish(); // Finish the activity after updating the status
                                }
                            });
                        } else {
                            Toast.makeText(this, "Tutor declined", Toast.LENGTH_SHORT).show();
                            sendNotificationToTutor(email, false);
                            removeTutorFromPending(userID);
                            finish();
                        }
                    } else {

                        Toast.makeText(this, "Failed to update tutor status", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void moveTutorToAccepted(String userID, TutorInfo tutorInfo) {
        DatabaseReference acceptedTutorsRef = mDatabase.child("accepted_tutors").child(userID);

        acceptedTutorsRef.setValue(tutorInfo)
                .addOnCompleteListener(moveTask -> {
                    if (moveTask.isSuccessful()) {
                        Toast.makeText(TutorDetailsActivity.this, "Tutor accepted and moved to accepted_tutors", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TutorDetailsActivity.this, "Failed to move tutor to accepted_tutors", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
    }

    private void removeTutorFromPending(String userID) {
        DatabaseReference pendingTutorsRef = mDatabase.child("pending_tutors").child(userID);

        pendingTutorsRef.removeValue()
                .addOnCompleteListener(removeTask -> {
                    if (removeTask.isSuccessful()) {
                        Toast.makeText(TutorDetailsActivity.this, "Tutor removed from pending", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TutorDetailsActivity.this, "Failed to remove tutor from pending", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotificationToTutor(String tutorEmail, boolean isAccepted) {
        String notificationMessage = isAccepted ?
                "Congratulations! Your application as a tutor has been accepted. We are excited to have you on board. Please check your email for further instructions." :
                "We regret to inform you that your application as a tutor has been rejected. Thank you for your interest.";

        FirebaseMessaging.getInstance().subscribeToTopic("tutor_notifications")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        NotificationPayload payload = new NotificationPayload();
                        payload.setTitle("Application Status");
                        payload.setMessage(notificationMessage);
                        payload.setStartDate(null);

                        MyFCMService.sendNotificationToTutor(tutorEmail, payload);

                        Toast.makeText(this, "Notification sent to tutor", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to subscribe tutor to topic", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
