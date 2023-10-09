package com.magister.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class more_info_tutor extends AppCompatActivity {
    private static final int REQUEST_FILE_PICK = 3;

    private static final int PERMISSION_REQUEST_CODE = 2;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private int mYear, mMonth, mDay;
    private EditText dateOfBirthEditText;
    private EditText addressEditText;
    private Button saveButton;
    private Button uploadButton;
    private Spinner spinner;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private Uri selectedFileUri;
    private ProgressDialog progressDialog;
    private Set<String> selectedExpertise = new HashSet<>(); // Initialize the set for selected expertise


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("Credentials");

        firstNameEditText = findViewById(R.id.first);
        lastNameEditText = findViewById(R.id.last);
        dateOfBirthEditText = findViewById(R.id.edad);
        addressEditText = findViewById(R.id.lctn);
        saveButton = findViewById(R.id.saveButton);
        uploadButton = findViewById(R.id.uploadButton);
        spinner = findViewById(R.id.spinner);

        progressDialog = new ProgressDialog(more_info_tutor.this);
        progressDialog.setMessage("Uploading File...");
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        final String emailText = intent.getStringExtra("email");
        final String phoneText = intent.getStringExtra("phone");
        final String passwordText = intent.getStringExtra("password");


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.list)) {
            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.rec, parent, false);
                }

                CheckBox checkbox = convertView.findViewById(R.id.checkbox);
                TextView textView = convertView.findViewById(R.id.spinner_item_text);

                String item = getItem(position).toString();
                textView.setText(item);

                checkbox.setOnCheckedChangeListener(null);
                checkbox.setChecked(selectedExpertise.contains(item)); // Set the initial state

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedExpertise.add(item); // Add the item to the selectedExpertise set
                    } else {
                        selectedExpertise.remove(item); // Remove the item from the selectedExpertise set
                    }
                });

                return convertView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        dateOfBirthEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                String numericOnly = input.replaceAll("[^\\d]", "");

                if (!numericOnly.equals(input)) {
                    s.replace(0, s.length(), numericOnly);
                    input = numericOnly;
                }

                if (!input.equals(current)) {
                    String formatted = formatDateString(input);
                    current = formatted;

                    int newSelectionIndex = Math.min(formatted.length(), dateOfBirthEditText.getSelectionStart());
                    dateOfBirthEditText.setSelection(newSelectionIndex);
                }
            }

            private String formatDateString(String input) {
                String formatted = "dd-mm-yyyy";

                if (input.isEmpty()) {
                    return formatted;
                }

                if (input.length() != 8) {
                    return input;
                }

                int day = Integer.parseInt(input.substring(0, 2));
                int month = Integer.parseInt(input.substring(2, 4));
                int year = Integer.parseInt(input.substring(4, 8));

                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.YEAR, year);

                formatted = String.format(Locale.getDefault(), "%02d-%02d-%04d",
                        cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));

                return formatted;
            }
        });



        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedFileUri = data.getData();

                            saveButton.setEnabled(true);
                        }
                    }
                }
        );

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(more_info_tutor.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(more_info_tutor.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    openFilePicker();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String expertise = spinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(dateOfBirth)  ||
                        TextUtils.isEmpty(address) || selectedFileUri == null) {
                    Toast.makeText(more_info_tutor.this, "Please fill all the necessary information and upload a file", Toast.LENGTH_SHORT).show();
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

                StringBuilder selectedExpertiseBuilder = new StringBuilder();
                for (String expertiseItem : selectedExpertise) {
                    if (selectedExpertiseBuilder.length() > 0) {
                        selectedExpertiseBuilder.append(", ");
                    }
                    selectedExpertiseBuilder.append(expertiseItem);
                }
                String selectedExpertiseString = selectedExpertiseBuilder.toString();

                TutorInfo tutorInfo = new TutorInfo();
                tutorInfo.setFirstName(firstName);
                tutorInfo.setLastName(lastName);
                tutorInfo.setDateOfBirth(dateOfBirth);
                tutorInfo.setAddress(address);

                tutorInfo.setExpertise(selectedExpertiseString);

                LaborClass account = new LaborClass();
                account.setEmail(emailText);
                account.setPhone(phoneText);
                account.setPassword(passwordText);
                account.setRole("tutor");

                tutorInfo.setAcc(account);

                String userID = firstName.toLowerCase() + "_" + lastName.toLowerCase();
                DatabaseReference userRef = mDatabase.child("pending_tutors").child(userID);


        progressDialog.show();
                StorageReference fileRef = mStorageRef.child(userID + ".pdf");

                fileRef.putFile(selectedFileUri)
                        .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return fileRef.getDownloadUrl();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri fileDownloadUri = task.getResult();
                                    tutorInfo.setFileUrl(fileDownloadUri.toString());

                                    tutorInfo.setStatus("pending");

                                    userRef.setValue(tutorInfo, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            progressDialog.dismiss();

                                            if (error != null) {
                                                Toast.makeText(more_info_tutor.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(more_info_tutor.this, "Register Successfully!", Toast.LENGTH_SHORT).show();

                                                String tutorEmail = tutorInfo.getAcc().getEmail();
                                                sendRegistrationReminderNotification(tutorEmail);

                                                Intent loginIntent = new Intent(more_info_tutor.this, Login1.class);
                                                startActivity(loginIntent);
                                                finish(); // Optional: If you want to finish the current activity
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(more_info_tutor.this, "Failed to upload file: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set the MIME type of files to be selected (e.g., "*/*" for all file types)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_PICK && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            // TODO: Handle the selected file (e.g., perform upload or store the file URI)

            saveButton.setEnabled(true);
        }
    }


    @Override
    public void onBackPressed() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onBackPressed();
    }
    private void sendRegistrationReminderNotification(String tutorEmail) {
        NotificationPayload payload = new NotificationPayload();
        payload.setTitle("Registration Reminder");
        payload.setMessage("Thank you for registering as a tutor. Please wait until your account is accepted.");

        MyFCMService.sendNotificationToTutor(tutorEmail, payload);
    }
    private boolean isValidName(String name) {
        return name.matches("^[A-Za-z]+(\\s[A-Za-z]+)?$");
    }

    private boolean isValidMiddleInitial(String initial) {
        return initial.length() == 1 && initial.matches("[a-zA-Z]+");
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(more_info_tutor.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d",
                                dayOfMonth, monthOfYear + 1, year);
                        dateOfBirthEditText.setText(selectedDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
