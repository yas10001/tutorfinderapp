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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParentDetailsActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView roleTextView;
    private String firstName;
    private String lastName;
    private ImageView img;
    private String email;
    private String firstname;
    private String lastname;
    private TextView ageTextView;
    private TextView creditsTextView;
    private TextView gradeTextView;
    private String grade;
    private Button removeButton;
    private ParentInfo parent;
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
        if (intent != null && intent.hasExtra("parent")) {
            parent = (ParentInfo) intent.getSerializableExtra("parent");
            if (parent != null) {
                firstName = parent.getFirstName();
                lastName = parent.getLastName();
                email = parent.getAcc().getEmail();
                String address = parent.getAddress();
                String phone = parent.getAcc().getPhone();
                String role = parent.getAcc().getRole();
                String age = parent.getDateOfBirth();
                grade = parent.getGrade();

                displayParentData(firstName, lastName, email, address, phone, role, grade, parent.getProfileImage(), age);
            } else {
                Toast.makeText(this, "Invalid parent data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No parent data found", Toast.LENGTH_SHORT).show();
        }

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showConfirmationDialog();
            }
        });
    }

    private void displayParentData(String firstName, String lastName, String email, String address, String phone, String role, String grade, String profileImage, String age) {
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

        String imageLink = "<a href='" + profileImage + "'>" + profileImage + "</a>";
        creditsTextView.setText(android.text.Html.fromHtml(imageLink));
        creditsTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        if (parent.getProfilePictureUrl() != null) {
            RequestOptions requestOptions = new RequestOptions();

            Glide.with(ParentDetailsActivity.this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(parent.getProfilePictureUrl())
                    .into(img);
        }
        else {
            img.setImageResource(R.drawable.tutor);
        }
    }


    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Parent");
        builder.setMessage("Are you sure you want to remove this parent? This action is irreversible and will permanently remove the parent from the list and database.");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeParent();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeParent() {
        String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("users").child(userID);

        parentRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> removeTask) {
                if (removeTask.isSuccessful()) {
                    Toast.makeText(ParentDetailsActivity.this, "Parent removed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ParentDetailsActivity.this, "Failed to remove parent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
