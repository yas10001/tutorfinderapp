package com.magister.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.Arrays;

public class TutorEditProfNEW extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private static final int BIO_EDIT_REQUEST_CODE = 1; // Unique value for bio edit
    private static final int RATE_EDIT_REQUEST_CODE = 2; // Unique value for rate edit
    private static final int SCHED_EDIT_REQUEST_CODE = 3; // Unique value for rate edit
    private static final int EXPERTISE_EDIT_REQUEST_CODE = 4; // Unique value for rate edit
    private static final int ADDRESS_EDIT_REQUEST_CODE = 5; // Unique value for rate edit
    private FirebaseUser currentUser;
    private TextView bioDisplay;
    private TextView rateDisplay;
    private TextView schedDisplay;
    private TextView expertiseDisplay;
    private TextView phoneDisplay;
    private TextView addressDisplay;
    private TextView emailDisplay;
    private Button back;
    private ImageView profilePictureImageView;
    private static final int PICK_IMAGE_REQUEST = 6;
    private StorageReference storageReference;

    private TextView fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tutor_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference("accepted_tutors");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bioDisplay = findViewById(R.id.bioDisplay);
        rateDisplay = findViewById(R.id.rateDisplay);
        schedDisplay = findViewById(R.id.schedDisplay);
        expertiseDisplay = findViewById(R.id.expertiseDisplay);
        phoneDisplay = findViewById(R.id.phoneDisplay);
        addressDisplay = findViewById(R.id.addressDisplay);
        emailDisplay = findViewById(R.id.emailDisplay);
        fullname = findViewById(R.id.fullname);
        profilePictureImageView = findViewById(R.id.profiletutor);
        back = findViewById(R.id.backbutton);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures");


        findViewById(R.id.editaboutme).setOnClickListener(v -> launchEditBioActivity(bioDisplay.getText().toString()));
        findViewById(R.id.editRate).setOnClickListener(v -> launchEditRateActivity(rateDisplay.getText().toString()));
        findViewById(R.id.editsched).setOnClickListener(v -> launchEditSchedActivity(schedDisplay.getText().toString()));
        findViewById(R.id.editAddress).setOnClickListener(v -> launchEditAddressActivity(addressDisplay.getText().toString())); // Replace with your method
        findViewById(R.id.editNumber).setOnClickListener(v -> launchEditPhoneActivity(phoneDisplay.getText().toString()));
        findViewById(R.id.editExp).setOnClickListener(v -> launchEditExpertiseActivity(parseExpertiseString(expertiseDisplay.getText().toString())));
        profilePictureImageView.setOnClickListener(v -> showPhotoOptionsDialog());
        back.setOnClickListener(v -> onBackPressed());

        fetchTutorData(bioDisplay, rateDisplay, schedDisplay, addressDisplay, phoneDisplay, emailDisplay, expertiseDisplay, fullname, profilePictureImageView);
    }
    private ArrayList<String> parseExpertiseString(String expertiseString) {
        String[] expertiseArray = expertiseString.split(", ");
        return new ArrayList<>(Arrays.asList(expertiseArray));
    }

    private void fetchTutorData(TextView bioDisplay, TextView rateDisplay, TextView schedDisplay,
                                TextView addressDisplay, TextView phoneDisplay,
                                TextView emailDisplay, TextView expertiseDisplay, TextView fullname, ImageView profilePictureImageView) {
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
                                    bioDisplay.setText(tutor.getBio());
                                    rateDisplay.setText("Php " + tutor.getRate() + "/hr");
                                    schedDisplay.setText(tutor.getSchedule());
                                    addressDisplay.setText(tutor.getAddress());
                                    phoneDisplay.setText(tutor.getAcc().getPhone());
                                    emailDisplay.setText(currentUser.getEmail());
                                    String fullName = tutor.getFirstName() + " " + tutor.getLastName();
                                    fullname.setText(fullName);

                                    String expertiseText = tutor.getExpertise(); // Get the expertise as a string
                                    expertiseDisplay.setText(expertiseText);
                                    String profilePictureUrl = tutor.getProfilePictureUrl();
                                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                        Glide.with(TutorEditProfNEW.this)
                                                .load(profilePictureUrl)
                                                .placeholder(R.drawable.placeholder_image)
                                                .error(R.drawable.error_image)
                                                .into(profilePictureImageView);
                                    }

                                    return;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }


    private void showPhotoOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Profile Photo Options");
        builder.setMessage("Choose an option:");


        builder.setPositiveButton("Upload Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openImagePicker();
            }
        });

        builder.setNegativeButton("Remove Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (profilePictureExists()) {
                    profilePictureImageView.setImageResource(R.drawable.tutor);
                    removeProfilePictureFromDatabase();
                    Toast.makeText(TutorEditProfNEW.this, "Profile photo removed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TutorEditProfNEW.this, "No profile picture to remove", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean profilePictureExists() {
        return profilePictureImageView.getDrawable() != null;
    }
    private void removeProfilePictureFromDatabase() {
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
                                        userRef.child("profilePictureUrl").setValue(null);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(TutorEditProfNEW.this,
                                        "Failed to remove profile picture from database", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
    private void openImagePicker() {
        Log.d("ImagePicker", "Image picker intent started");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
                Toast.makeText(TutorEditProfNEW.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

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

                                                    Glide.with(TutorEditProfNEW.this)
                                                            .load(downloadUrl)
                                                            .placeholder(R.drawable.placeholder_image)
                                                            .error(R.drawable.error_image)
                                                            .into(profilePictureImageView);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {
                                            Toast.makeText(TutorEditProfNEW.this,
                                                    "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(TutorEditProfNEW.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
            });
        }
    }


    private void launchEditAddressActivity(String currentAddress) {
        Intent intent = new Intent(this, AddressEditActivity.class);
        intent.putExtra("currentAddress", currentAddress);
        startActivityForResult(intent, ADDRESS_EDIT_REQUEST_CODE); // Use a unique request code
    }
    private void launchEditRateActivity(String currentRate) {
        Intent intent = new Intent(this, RateEditActivityTutor.class);
        intent.putExtra("currentRate", currentRate);
        startActivityForResult(intent, RATE_EDIT_REQUEST_CODE);
    }

    private void launchEditBioActivity(String currentBio) {
        Intent intent = new Intent(this, BioEditActivity.class);
        intent.putExtra("currentBio", currentBio);
        startActivityForResult(intent, 1); // Use a unique request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BIO_EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String editedBio = data.getStringExtra("editedBio");
            bioDisplay.setText(editedBio);
        } else if (requestCode == RATE_EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String editedRate = data.getStringExtra("editedRate");
            rateDisplay.setText("Php " + editedRate + "/hr");
        } else if (requestCode == SCHED_EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String editedSched = data.getStringExtra("editedSched");
            schedDisplay.setText(editedSched);
        } else if (requestCode == EXPERTISE_EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> editedExpertise = data.getStringArrayListExtra("editedExpertise");
            if (editedExpertise != null && !editedExpertise.isEmpty()) {
                String expertiseText = TextUtils.join(", ", editedExpertise);
                expertiseDisplay.setText(expertiseText);
            }
        } else if (requestCode == ADDRESS_EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String editedAddress = data.getStringExtra("editedAddress");
            addressDisplay.setText(editedAddress);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void launchEditPhoneActivity(String currentPhone) {
        Intent intent = new Intent(this, PhoneEditActivity.class);
        intent.putExtra("currentPhone", currentPhone);
        startActivityForResult(intent, 2); // Use a unique request code
    }
    private void launchEditSchedActivity(String currentSched) {
        Intent intent = new Intent(this, ScheduleEditActivity.class);
        intent.putExtra("currentSched", currentSched);
        startActivityForResult(intent, 3);
    }
    private void launchEditExpertiseActivity(ArrayList<String> currentExpertise) {
        Intent intent = new Intent(this, ExpertiseEditActivity.class);
        intent.putStringArrayListExtra("currentExpertise", currentExpertise);
        startActivityForResult(intent, EXPERTISE_EDIT_REQUEST_CODE);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Final_dashboard.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
