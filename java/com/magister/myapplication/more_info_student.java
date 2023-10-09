package com.magister.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class more_info_student extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText middleNameEditText;
    private EditText dateOfBirthEditText;
    private EditText locationEditText;
    private Button saveButton;
    private Button uploadButton;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private EditText editTextNumber;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private Uri selectedImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info_student);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference("Crendentials");

        firstNameEditText = findViewById(R.id.persname);
        lastNameEditText = findViewById(R.id.lasname);
        middleNameEditText = findViewById(R.id.gitnaname);
        dateOfBirthEditText = findViewById(R.id.datebirth);
        locationEditText = findViewById(R.id.loc);
        saveButton = findViewById(R.id.buttonSave);
        uploadButton = findViewById(R.id.uploadBtn);
        editTextNumber = findViewById(R.id.editTextNumber);

        progressDialog = new ProgressDialog(more_info_student.this);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        final String emailText = intent.getStringExtra("email");
        final String phoneText = intent.getStringExtra("phone");
        final String passwordText = intent.getStringExtra("password");

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData();

                            saveButton.setEnabled(true);
                        }
                    }
                }
        );

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(more_info_student.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(more_info_student.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    openImagePicker();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String middleName = middleNameEditText.getText().toString().trim();
                String age = dateOfBirthEditText.getText().toString().trim();
                String location = locationEditText.getText().toString().trim();
                String gradelevel = editTextNumber.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty() || age.isEmpty() || location.isEmpty() || selectedImageUri == null || gradelevel.isEmpty() ) {
                    Toast.makeText(more_info_student.this, "Please fill all the necessary information and upload a photo", Toast.LENGTH_SHORT).show();
                    return;
                }
             if (!isValidName(firstName)) {
                firstNameEditText.setError("Invalid! First name Format");
                return;
            }

                if (!isValidName(lastName)) {
                lastNameEditText.setError("Invalid! Last name Format");
                return;
            }

                if (!isValidMiddleInitial(middleName)) {
                middleNameEditText.setError("Invalid! Middle name Format");
                return;
            }

            int date = Integer.parseInt(age);
                if (date < 5) {
                dateOfBirthEditText.setError("Age must be at least 5");
                return;
            }
                int grade = Integer.parseInt(gradelevel);
                if (grade > 12) {
                    editTextNumber.setError("Maximum grade level is 12");
                    return;
                }


                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setFirstName(firstName);
                studentInfo.setLastName(lastName);
                studentInfo.setMiddleInitial(middleName);
                studentInfo.setDateOfBirth(age);
                studentInfo.setAddress(location);
                studentInfo.setGrade(gradelevel);

                LaborClass account = new LaborClass();
                account.setEmail(emailText);
                account.setPhone(phoneText);
                account.setPassword(passwordText);
                account.setRole("student");

                studentInfo.setAcc(account);

                String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();

                progressDialog.show();

                StorageReference imageRef = mStorageRef.child(userID + ".jpg");
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        studentInfo.setProfileImage(downloadUri.toString());

                                        mDatabase.child(userID).setValue(studentInfo, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                progressDialog.dismiss();
                                                if (error != null) {
                                                    Toast.makeText(more_info_student.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(more_info_student.this, "Registered! Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(more_info_student.this, Login1.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(more_info_student.this, "Failed to upload image", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // TODO: Handle the selected image (e.g., display it in an ImageView)

            saveButton.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open file picker
                openImagePicker();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onBackPressed();
    }
    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+(\\s[A-Za-z]+)?$");
    }

    private boolean isValidMiddleInitial(String initial) {
        return initial.length() == 1 && initial.matches("[a-zA-Z]+");
    }
    private boolean isValidGradeLevel(String gradeLevel) {
        String[] validGrades = {"kinder 1", "Kinder 1", "kindergarten 1", "Kindergarten 1", "kinder 2", "Kinder 2", "kindergarten 2", "Kindergarten 2"};
        String formattedGradeLevel = gradeLevel.toLowerCase().trim();

        for (String validGrade : validGrades) {
            if (formattedGradeLevel.equals(validGrade)) {
                return true;
            }
        }
        return false;
    }
}
