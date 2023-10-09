package  com.magister.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class Subjects extends AppCompatActivity {

    private EditText editTextSubjectName;
    private Button buttonAddSubject;
    private Button buttonUploadImage;
    private Button deleteSub;
    private Button updateSub;
    private GridView gridViewSubjects;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> subjectsAdapter;
    private List<String> subjectsList;

    private DatabaseReference subjectsRef;
    private StorageReference storageRef;

    private String selectedSubject;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST_FOR_UPDATE  = 2;
    private Uri imageUri;

    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        buttonAddSubject = findViewById(R.id.buttonAddSubject);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        gridViewSubjects = findViewById(R.id.gridViewSubjects);


        subjectsList = new ArrayList<>();
        subjectsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjectsList);

        gridViewSubjects.setAdapter(subjectsAdapter);

        subjectsRef = FirebaseDatabase.getInstance().getReference("subjects");
        storageRef = FirebaseStorage.getInstance().getReference("subject_images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);

        buttonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject();
            }
        });
        gridViewSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = subjectsList.get(position);
                showOptionsDialog(selectedSubject);
            }
        });

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        loadSubjects();
    }
    private void positionProgressDialog() {
        if (progressDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            progressDialog.getWindow().setAttributes(layoutParams);
        }
    }
    private void addSubject() {
        final String subjectName = editTextSubjectName.getText().toString().trim();

        if (TextUtils.isEmpty(subjectName)) {
            Toast.makeText(this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        final String subjectId = subjectName.toLowerCase().replace(" ", "_");

        subjectsRef.child(subjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Subject already exists, show a toast message
                    Toast.makeText(Subjects.this, "Subject already exists", Toast.LENGTH_SHORT).show();
                } else {
                    SubjectGet subjectGet = new SubjectGet();
                    subjectGet.setName(subjectName);

                    if (selectedImageBitmap != null) {
                        uploadImage(subjectId, selectedImageBitmap, subjectGet);
                    } else {
                        saveSubjectToFirebase(subjectId, subjectGet);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Subjects.this, "Error checking subject existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(final String subjectId, Bitmap imageBitmap, final SubjectGet subjectGet) {
        positionProgressDialog(); // Position the progress dialog in the center

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        final StorageReference imageRef = storageRef.child(subjectId + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        subjectGet.setImageUrl(uri.toString());
                        saveSubjectToFirebase(subjectId, subjectGet);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Subjects.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.dismiss(); // Dismiss progress dialog after upload (success or failure)
            }
        });
    }


    private void saveSubjectToFirebase(String subjectId, SubjectGet subjectGet) {
        subjectsRef.child(subjectId).setValue(subjectGet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Subjects.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                        editTextSubjectName.setText("");
                        selectedImageBitmap = null; // Reset the selected image bitmap
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Subjects.this, "Failed to add subject", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSubjects() {
        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectsList.clear();

                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    SubjectGet subjectGet = subjectSnapshot.getValue(SubjectGet.class);
                    if (subjectGet != null && subjectGet.getName() != null) { // Check for null value
                        subjectsList.add(subjectGet.getName());
                    }
                }

                subjectsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Subjects.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showOptionsDialog(final String subject) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Options");
        dialogBuilder.setItems(new CharSequence[]{"Delete", "Update", "Cancel"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Delete
                        showDeleteDialog(subject);
                        break;
                    case 1: // Update
                        showUpdateDialog(subject);
                        break;
                    case 2: // Cancel
                        dialog.dismiss();
                        break;
                }
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private void showDeleteDialog(final String subject) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Delete Subject");
        dialogBuilder.setMessage("Are you sure you want to delete this subject?");

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String subjectId = subject.toLowerCase().replace(" ", "_");
                subjectsRef.child(subjectId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Subjects.this, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Subjects.this, "Failed to delete subject", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    private void showUpdateDialog(final String subject) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Update Subject");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_subject, null);
        final EditText editText = dialogView.findViewById(R.id.editTextNewSubjectName);
        final Button buttonUpdateImage = dialogView.findViewById(R.id.buttonUpdateImage);

        editText.setText(subject);

        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newSubjectName = editText.getText().toString().trim();
                updateSubject(subject, newSubjectName);
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = dialogBuilder.create();

        buttonUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the current dialog
                openImagePickerForUpdate(subject); // Open image picker for subject image update
            }
        });

        dialog.show();
    }


    private void openImagePickerForUpdate(final String subject) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST_FOR_UPDATE);

        selectedSubject = subject;
    }

    private void updateSubjectImage(final String subjectId, final Bitmap newImageBitmap) {
        if (subjectId != null) {
            progressDialog.show(); // Show progress dialog before starting upload

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            final StorageReference imageRef = storageRef.child(subjectId + ".jpg");

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Update the subject data with the new image URL
                            updateSubjectData(subjectId, uri.toString());

                            progressDialog.dismiss();
                            Toast.makeText(Subjects.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss(); // Dismiss progress dialog on failure
                    Toast.makeText(Subjects.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void updateSubjectData(final String subjectId, final String imageUrl) {
        subjectsRef.child(subjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SubjectGet updatedSubject = dataSnapshot.getValue(SubjectGet.class);
                    if (updatedSubject != null) {
                        updatedSubject.setImageUrl(imageUrl);

                        subjectsRef.child(subjectId).setValue(updatedSubject)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Successfully updated subject data including the image URL
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Subjects.this, "Failed to update subject", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(Subjects.this, "Error updating subject data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedImageBitmap = bitmap;
                updateSubjectImage(getKeyByValue(subjectsList, selectedSubject), selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Subjects.this, "Failed to update image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST_FOR_UPDATE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                updateSubjectImage(getKeyByValue(subjectsList, selectedSubject), bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Subjects.this, "Failed to update image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateSubject(final String oldSubject, final String newSubject) {
        final String oldSubjectId = oldSubject.toLowerCase().replace(" ", "_");
        final String newSubjectId = newSubject.toLowerCase().replace(" ", "_");

        subjectsRef.child(oldSubjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    subjectsRef.child(newSubjectId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot newDataSnapshot) {
                            if (!newDataSnapshot.exists() || oldSubjectId.equals(newSubjectId)) {
                                SubjectGet updatedSubject = dataSnapshot.getValue(SubjectGet.class);
                                if (updatedSubject != null) {
                                    updatedSubject.setName(newSubject);

                                    subjectsRef.child(oldSubjectId).removeValue(); // Remove the old entry
                                    subjectsRef.child(newSubjectId).setValue(updatedSubject) // Add the updated entry
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Subjects.this, "Subject updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Subjects.this, "Failed to update subject", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Subject name already exists, show a toast message
                                Toast.makeText(Subjects.this, "Subject already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Subjects.this, "Error checking subject existence", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Subjects.this, "Error retrieving subject information", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getKeyByValue(List<String> list, String value) {
        int index = list.indexOf(value);
        if (index != -1) {
            return list.get(index);
        }
        return null;
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

}