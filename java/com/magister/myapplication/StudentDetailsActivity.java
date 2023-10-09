package com.magister.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView roleTextView;
    private TextView ageTextView;
    private TextView creditsTextView;
    private TextView gradeTextView;
    private Button removeButton;
    private String firstName;
    private String lastName;
    private String email;
    private String grade;
    private ImageView img;

    private StudentInfo student;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_details2);

        nameTextView = findViewById(R.id.titleName);
        emailTextView = findViewById(R.id.email);
        addressTextView = findViewById(R.id.address);
        phoneTextView = findViewById(R.id.phone);
        roleTextView = findViewById(R.id.Role);
        creditsTextView = findViewById(R.id.ID);
        ageTextView = findViewById(R.id.ageStuds);
        gradeTextView = findViewById(R.id.grade);
        removeButton = findViewById(R.id.btnRemoveStudent);
        img = findViewById(R.id.profileImga);


        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("student")) {
            student = (StudentInfo) intent.getSerializableExtra("student");
            if (student != null) {
                firstName = student.getFirstName();
                lastName = student.getLastName();
                email = student.getAcc().getEmail();
                String address = student.getAddress();
                String phone = student.getAcc().getPhone();
                String role = student.getAcc().getRole();
                String age = student.getDateOfBirth();
                grade = student.getGrade();

                displayStudentData(firstName, lastName, email, address, phone, role, grade, age);
            } else {
                Toast.makeText(this, "Invalid student data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No student data found", Toast.LENGTH_SHORT).show();
        }

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
    }

    private void displayStudentData(String firstName, String lastName, String email, String address, String phone, String role, String grade, String age) {
        String fullName = firstName + " " + lastName;
        String emailAddress = "Email: " + email;
        String fullAddress = "Address: " + address;
        String phoneNumber = "Phone: " + phone;
        String userRole = "Role: " + role;
        String gradeLevel = "Grade: " + grade;
        String ageLevel = "Age: " + age;

        nameTextView.setText(fullName);
        emailTextView.setText(emailAddress);
        addressTextView.setText(fullAddress);
        phoneTextView.setText(phoneNumber);
        roleTextView.setText(userRole);
        gradeTextView.setText(gradeLevel);
        ageTextView.setText(ageLevel);

        if (student.getProfilePictureUrl() != null) {
            RequestOptions requestOptions = new RequestOptions();

            Glide.with(StudentDetailsActivity.this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(student.getProfilePictureUrl())
                    .into(img);
        }
        else {
            img.setImageResource(R.drawable.tutor);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Student");
        builder.setMessage("Are you sure you want to remove this student? This action is irreversible and will permanently remove the student from the list and database.");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeStudent();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeStudent() {
        String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("users").child(userID);

        studentRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> removeTask) {
                if (removeTask.isSuccessful()) {
                    // Student removed from "users/students" successfully
                    Toast.makeText(StudentDetailsActivity.this, "Student removed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Failed to remove student from "users/students"
                    Toast.makeText(StudentDetailsActivity.this, "Failed to remove student", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
