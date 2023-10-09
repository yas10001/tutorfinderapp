package com.magister.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StudentEditProf extends AppCompatActivity {
    private TextView grade, phone, address, ages, fullname;
    private EditText gradeChange, phoneChange, addressChange, ageChange;
    private ImageView image;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_prof);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        grade = findViewById(R.id.gradeTextviews);
        phone = findViewById(R.id.phoneTextViews);
        address = findViewById(R.id.addressTextViews);
        ages = findViewById(R.id.setsAges);
        gradeChange = findViewById(R.id.gradeChanges);
        phoneChange = findViewById(R.id.changePhones);
        addressChange = findViewById(R.id.changeAddresss);
        ageChange = findViewById(R.id.changeAges);
        fullname = findViewById(R.id.textView42);
        image = findViewById(R.id.imageView19);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String gradeText = intent.getStringExtra("grade");
            String phoneText = intent.getStringExtra("phone");
            String addressText = intent.getStringExtra("address");
            String ageText = intent.getStringExtra("age");

            grade.setText(gradeText);
            phone.setText(phoneText);
            address.setText(addressText);
            ages.setText(ageText);
        }

        fetchStudentData();

        findViewById(R.id.savebtns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void fetchStudentData() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                StudentInfo student = userSnapshot.getValue(StudentInfo.class);
                                if (student != null) {
                                    String fullName = student.getFirstName() + " " + student.getLastName();
                                    String fullAddress = "Address: " + student.getAddress();
                                    String phoneNumber = "Phone: " + student.getAcc().getPhone();
                                    String gradeLevel = "Grade: " + student.getGrade();
                                    String age = "Age: " + student.getDateOfBirth();

                                    fullname.setText(fullName);
                                    grade.setText(gradeLevel);
                                    phone.setText(phoneNumber);
                                    address.setText(fullAddress);
                                    ages.setText(age);

                                    String profilePictureUrl = student.getProfilePictureUrl();
                                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                        Glide.with(StudentEditProf.this)
                                                .load(profilePictureUrl)
                                                .placeholder(R.drawable.placeholder_image)
                                                .error(R.drawable.error_image)
                                                .into(image);
                                    }

                                    return;
                                }
                            }
                            Toast.makeText(StudentEditProf.this, "User not found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudentEditProf.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(StudentEditProf.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String newGrade = gradeChange.getText().toString().trim();
        String newPhone = phoneChange.getText().toString().trim();
        String newAddress = addressChange.getText().toString().trim();
        String newAge = ageChange.getText().toString().trim();
        final String[] profilePictureUrl = {""};

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
                                profilePictureUrl[0] = snapshot.child("profilePictureUrl").getValue(String.class);

                                if (!newPhone.isEmpty()) {
                                    if (!newPhone.matches("^09\\d{9}$")) {
                                        Toast.makeText(StudentEditProf.this, "Invalid phone number. Please enter a valid Philippines-based number.", Toast.LENGTH_SHORT).show();
                                        return; // Stop saving changes if phone number is invalid
                                    }
                                    userRef.child("acc/phone").setValue(newPhone);
                                }

                                if (!newGrade.isEmpty()) {
                                    userRef.child("grade").setValue(newGrade);
                                }
                                if (!newAddress.isEmpty()) {
                                    userRef.child("address").setValue(newAddress);
                                }
                                if (!newAge.isEmpty()) {
                                    userRef.child("dateOfBirth").setValue(newAge);
                                }
                            }
                        }
                        Toast.makeText(StudentEditProf.this, "Changes saved", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("grade", newGrade);
                        intent.putExtra("phone", newPhone);
                        intent.putExtra("address", newAddress);
                        intent.putExtra("age", newAge);
                        intent.putExtra("profilePictureUrl", profilePictureUrl[0]); // Use profilePictureUrl[0]
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled event
                    }
                });
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }



    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Convert the selected image to a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadImage(bitmap);
            } catch (IOException e) {
                Log.e("StudentEditProf", "Error selecting image: " + e.getMessage());
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Bitmap bitmap) {
        if (currentUser != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("profile_pictures")
                    .child(currentUser.getUid());

            // Compress the bitmap into a JPEG format and store it in a byte array output stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Upload the byte array to Firebase storage
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                Toast.makeText(StudentEditProf.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                // Retrieve the download URL of the uploaded image
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Update the database with the download URL
                    String downloadUrl = uri.toString();
                    if (currentUser != null) {
                        String email = currentUser.getEmail();
                        if (email != null) {
                            databaseReference.orderByChild("acc/email").equalTo(email)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String userKey = snapshot.getKey();
                                                if (userKey != null) {
                                                    DatabaseReference userRef = databaseReference.child(userKey);
                                                    userRef.child("profilePictureUrl").setValue(downloadUrl);

                                                    // Load the uploaded profile picture into the ImageView
                                                    Glide.with(StudentEditProf.this)
                                                            .load(downloadUrl)
                                                            .placeholder(R.drawable.placeholder_image)
                                                            .error(R.drawable.error_image)
                                                            .into(image);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(StudentEditProf.this,
                                                    "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                // Error uploading image
                Toast.makeText(StudentEditProf.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
