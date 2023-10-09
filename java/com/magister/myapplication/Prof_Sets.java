package com.magister.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class Prof_Sets extends AppCompatActivity {
    private TextView fullnametext;
    private TextView gradetext;
    private TextView phonetext;
    private TextView addresstext;
    private ImageView profile;

    private TextView ageText;
    private EditText gradeChange;
    private EditText changePhone;
    private EditText changeAddress;
    private EditText changeAge;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private Uri imageUri;

    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_sets);

        fullnametext = findViewById(R.id.textView42);
        gradetext = findViewById(R.id.gradeTextview);
        phonetext = findViewById(R.id.phoneTextView);
        addresstext = findViewById(R.id.addressTextView);
        gradeChange = findViewById(R.id.gradeChange);
        changePhone = findViewById(R.id.changePhone);
        changeAddress = findViewById(R.id.changeAddress);
        ageText = findViewById(R.id.setsAge);
        changeAge = findViewById(R.id.changeAge);
        profile = findViewById(R.id.profilepar);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                displayParentData(email);
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.savebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        if (getIntent().hasExtra("imageUri")) {
            imageUri = getIntent().getParcelableExtra("imageUri");
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayParentData(String email) {
        databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        ParentInfo parent = userSnapshot.getValue(ParentInfo.class);
                        if (parent != null) {
                            String fullName = parent.getFirstName() + " " + parent.getLastName();
                            String fullAddress = "Address: " + parent.getAddress();
                            String phoneNumber = "Phone: " + parent.getAcc().getPhone();
                            String gradeLevel = "Grade: " + parent.getGrade();
                            String age = "Age: " + parent.getDateOfBirth();

                            fullnametext.setText(fullName);
                            addresstext.setText(fullAddress);
                            phonetext.setText(phoneNumber);
                            gradetext.setText(gradeLevel);
                            ageText.setText(age);

                            if (parent.getProfilePictureUrl() != null) {
                                RequestOptions requestOptions = new RequestOptions();

                                Glide.with(Prof_Sets.this)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load(parent.getProfilePictureUrl())
                                        .into(profile);
                            }
                            return;
                        }
                    }
                    Toast.makeText(Prof_Sets.this, "User not found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Prof_Sets.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Prof_Sets.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                String newGrade = gradeChange.getText().toString().trim();
                String newPhone = changePhone.getText().toString().trim();
                String newAddress = changeAddress.getText().toString().trim();
                String newAge = changeAge.getText().toString().trim();
                final String[] profilePictureUrl = {""}; // Declare as final

                if (!newPhone.isEmpty() && !isValidPhoneNumber(newPhone)) {
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newGrade.isEmpty() && newPhone.isEmpty() && newAddress.isEmpty() && newAge.isEmpty()) {
                    Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.orderByChild("acc/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userKey = snapshot.getKey();
                            if (userKey != null) {
                                DatabaseReference userRef = databaseReference.child(userKey);
                                if (!newGrade.isEmpty()) {
                                    userRef.child("grade").setValue(newGrade);
                                }
                                if (!newPhone.isEmpty()) {
                                    if (!newPhone.matches("^09\\d{9}$")) {
                                        Toast.makeText(Prof_Sets.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    userRef.child("acc/phone").setValue(newPhone); // Update the correct field
                                }
                                if (!newAddress.isEmpty()) {
                                    userRef.child("address").setValue(newAddress);
                                }
                                if (!newAge.isEmpty()) {
                                    userRef.child("dateOfBirth").setValue(newAge);
                                }

                                if (snapshot.hasChild("profilePictureUrl")) {
                                    profilePictureUrl[0] = snapshot.child("profilePictureUrl").getValue(String.class);
                                }
                            }
                        }
                        Toast.makeText(Prof_Sets.this, "Changes saved", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("expertise", newGrade);
                        intent.putExtra("phone", newPhone);
                        intent.putExtra("address", newAddress);
                        intent.putExtra("age", newAge);
                        intent.putExtra("profilePictureUrl", profilePictureUrl[0]);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Prof_Sets.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^09\\d{9}$");
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

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
                Log.e("Prof_Sets", "Error selecting image: " + e.getMessage());
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
                Toast.makeText(Prof_Sets.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    if (currentUser != null) {
                        String email = currentUser.getEmail();
                        if (email != null) {
                            databaseReference.orderByChild("acc/email").equalTo(email)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String userKey = snapshot.getKey();
                                                if (userKey != null) {
                                                    DatabaseReference userRef = databaseReference.child(userKey);
                                                    userRef.child("profilePictureUrl").setValue(downloadUrl);

                                                    Glide.with(Prof_Sets.this)
                                                            .load(downloadUrl)
                                                            .placeholder(R.drawable.placeholder_image)
                                                            .error(R.drawable.error_image)
                                                            .into(profile);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {
                                            Toast.makeText(Prof_Sets.this,
                                                    "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                // Error uploading image
                Toast.makeText(Prof_Sets.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

