package com.magister.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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

public class TutorEditProf extends AppCompatActivity {
    private TextView grade, phone, address, ages, fullname, rate;
    private EditText gradeChange, phoneChange, addressChange, ageChange, setRate;
    private ImageView image;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit_prof);

        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        grade = findViewById(R.id.gradeTextviewss);
        phone = findViewById(R.id.phoneTextViewss);
        address = findViewById(R.id.addressTextViewss);
        ages = findViewById(R.id.setsAgess);
        gradeChange = findViewById(R.id.gradeChangess);
        phoneChange = findViewById(R.id.changePhoness);
        addressChange = findViewById(R.id.changeAddressss);
        ageChange = findViewById(R.id.changeAgess);
        fullname = findViewById(R.id.textView42);
        image = findViewById(R.id.imageView192);
        rate = findViewById(R.id.rateTextView);
        setRate = findViewById(R.id.changeRate);

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
        fetchTutorData();

        findViewById(R.id.savebtnss).setOnClickListener(new View.OnClickListener() {
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
        ageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

    }
    private void fetchTutorData() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot tutorSnapshot : dataSnapshot.getChildren()) {
                                TutorInfo tutor = tutorSnapshot.getValue(TutorInfo.class);
                                if (tutor != null) {
                                    String fullName = tutor.getFirstName() + " " + tutor.getLastName();
                                    String fullAddress = "Address: " + tutor.getAddress();
                                    String phoneNumber = "Phone: " + tutor.getAcc().getPhone();
                                    String gradeLevel = "Expertise: " + tutor.getExpertise();
                                    String age = "Date Of Birth: " + tutor.getDateOfBirth();
                                    fullname.setText(fullName);
                                    grade.setText(gradeLevel);
                                    phone.setText(phoneNumber);
                                    address.setText(fullAddress);
                                    ages.setText(age);

                                    String profilePictureUrl = tutor.getProfilePictureUrl();
                                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                        Glide.with(TutorEditProf.this)
                                                .load(profilePictureUrl)
                                                .placeholder(R.drawable.placeholder_image)
                                                .error(R.drawable.error_image)
                                                .into(image);
                                    }

                                    String tutorRate = tutor.getRate();
                                    if (tutorRate != null && !tutorRate.isEmpty()) {
                                        rate.setText("Rate: " + tutorRate);
                                    }

                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TutorEditProf", "Error fetching tutor data: " + databaseError.getMessage());
                    }
                });
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(TutorEditProf.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                ageChange.setText(selectedDate);
            }
        }, 1990, 0, 1);

        datePickerDialog.show();
    }

    private void saveChanges() {
        String newGrade = gradeChange.getText().toString().trim();
        String newPhone = phoneChange.getText().toString().trim();
        String newAddress = addressChange.getText().toString().trim();
        String newAge = ageChange.getText().toString().trim();
        String newRate = setRate.getText().toString().trim();

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

                                if (!newPhone.isEmpty()) {
                                    if (!newPhone.matches("^09\\d{9}$")) {
                                        Toast.makeText(TutorEditProf.this, "Invalid phone number. Please enter a valid Philippines-based number.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    userRef.child("acc/phone").setValue(newPhone);
                                }

                                if (!newGrade.isEmpty()) {
                                    userRef.child("expertise").setValue(newGrade);
                                }
                                if (!newAddress.isEmpty()) {
                                    userRef.child("address").setValue(newAddress);
                                }
                                if (!newAge.isEmpty()) {
                                    userRef.child("dateOfBirth").setValue(newAge);
                                }
                                if (!newRate.isEmpty()) {
                                    userRef.child("rate").setValue(newRate);
                                    rate.setText(newRate);
                                }
                            }
                        }
                        Toast.makeText(TutorEditProf.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(TutorEditProf.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
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

                                                    Glide.with(TutorEditProf.this)
                                                            .load(downloadUrl)
                                                            .placeholder(R.drawable.placeholder_image)
                                                            .error(R.drawable.error_image)
                                                            .into(image);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(TutorEditProf.this,
                                                    "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(TutorEditProf.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
