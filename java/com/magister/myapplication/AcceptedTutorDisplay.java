package com.magister.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptedTutorDisplay extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView roleTextView;
    private TextView AgeTextView;
    private TextView ExpertTextView;
    private String firstName;
    private String lastName;
    private ImageView imageView;
    private String email;
    private Button feedbackButton;
    private Button removeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accepted_tutor_display);

        nameTextView = findViewById(R.id.titleName);
        emailTextView = findViewById(R.id.emailTextView);
        addressTextView = findViewById(R.id.addressTv);
        AgeTextView = findViewById(R.id.ageTv);
        ExpertTextView = findViewById(R.id.expertise);
        phoneTextView = findViewById(R.id.phoneTextView);
        roleTextView = findViewById(R.id.roleTextView);
        feedbackButton = findViewById(R.id.Feedback);
        removeButton = findViewById(R.id.Remove);
        imageView = findViewById(R.id.profileImage);

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
                String expertise = tutor.getExpertise();
                String age = tutor.getDateOfBirth();
                String ProfilePictureUrl= tutor.getProfilePictureUrl();

                displayTutorData(firstName, lastName, email, address, phone, role, expertise, age, ProfilePictureUrl);
            } else {
                Toast.makeText(this, "Invalid tutor data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No tutor data found", Toast.LENGTH_SHORT).show();
        }
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTutor();
            }
        });
    }


    private void displayTutorData(String firstName, String lastName, String email, String address, String phone, String role, String expertise, String age, String profilePictureUrl) {
        String fullName = getNonNullString(firstName) + " " + getNonNullString(lastName);
        String emailAddress = "Email: " + getNonNullString(email);
        String fullAddress = "Address: " + getNonNullString(address);
        String phoneNumber = "Phone: " + getNonNullString(phone);
        String userRole = "Role: " + getNonNullString(role);
        String ageOfTutor = "Date of Birth: " + getNonNullString(age);
        String expertiseText = "Expertise: " + getNonNullString(expertise);

        nameTextView.setText(fullName);
        emailTextView.setText(emailAddress);
        addressTextView.setText(fullAddress);
        phoneTextView.setText(phoneNumber);
        roleTextView.setText(userRole);
        AgeTextView.setText(ageOfTutor);
        ExpertTextView.setText(expertiseText);

        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(AcceptedTutorDisplay.this)
                    .load(profilePictureUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.tutor);
        }
    }


    private String getNonNullString(String value) {
        return value != null ? value : "";
    }

    private void removeTutor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Tutor");
        builder.setMessage("Are you sure you want to remove this tutor? This action is irreversible and will permanently remove the tutor from the list and database.");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();

                DatabaseReference tutorsRef = FirebaseDatabase.getInstance().getReference("accepted_tutors").child(userID);

                tutorsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> removeTask) {
                        if (removeTask.isSuccessful()) {
                            Toast.makeText(AcceptedTutorDisplay.this, "Tutor removed successfully", Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent();
                            intent.putExtra("removedTutor", firstName + " " + lastName);
                            setResult(RESULT_OK, intent);

                            finish();
                        } else {
                            Toast.makeText(AcceptedTutorDisplay.this, "Failed to remove tutor", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
