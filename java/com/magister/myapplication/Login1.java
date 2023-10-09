package com.magister.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login1 extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private TextView forgotPasswordText, createAccountText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        email = findViewById(R.id.name);
        password = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPasswordText = findViewById(R.id.textView32);
        createAccountText = findViewById(R.id.textView41);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in, please wait...");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    loginUser();
                } else {
                    Toast.makeText(Login1.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SpannableString forgotPasswordSpannable = new SpannableString("Forgot Password?");
        ClickableSpan forgotPasswordClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(Login1.this, "Contact Support", Toast.LENGTH_SHORT).show();
            }
        };
        forgotPasswordSpannable.setSpan(forgotPasswordClickableSpan, 0, forgotPasswordSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPasswordText.setText(forgotPasswordSpannable);
        forgotPasswordText.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString createAccountSpannable = new SpannableString("Create New Account");
        ClickableSpan createAccountClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login1.this, Sign_Up1.class);
                startActivity(intent);
            }
        };
        createAccountSpannable.setSpan(createAccountClickableSpan, 0, createAccountSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        createAccountSpannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, createAccountSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        createAccountText.setText(createAccountSpannable);
        createAccountText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isEmailValid = isEmailValid();
            boolean isPasswordValid = isPasswordValid();
            loginBtn.setEnabled(isEmailValid && isPasswordValid);
        }
    };

    private boolean isEmailValid() {
        String emailText = email.getText().toString().trim();
        return !TextUtils.isEmpty(emailText);
    }

    private boolean isPasswordValid() {
        String passwordText = password.getText().toString().trim();
        return !TextUtils.isEmpty(passwordText);
    }

    private boolean isFormValid() {
        return isEmailValid() && isPasswordValid();
    }

    private void loginUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
            Toast.makeText(Login1.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();

        Log.d("LoginActivity", "Attempting to login with email: " + emailText);

        if (emailText.equalsIgnoreCase("admin") && passwordText.equals("admin123")) {
            progressDialog.dismiss();
            Log.d("LoginActivity", "Login successful for admin");

            Intent intent = new Intent(Login1.this, Tutor_dashboard.class);
            startActivity(intent);
            finish();
        } else {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                if (firebaseUser.isEmailVerified()) {
                                    progressDialog.dismiss();
                                    String userEmail = firebaseUser.getEmail();
                                    checkTutorStatus(userEmail);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login1.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Log.d("LoginActivity", "User not found for email: " + emailText);
                                Toast.makeText(Login1.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Login1.this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show();
                            Log.d("LoginActivity", "Login failed: " + e.getMessage());
                        }
                    });
        }
    }

    private void checkTutorStatus(String userEmail) {
        Query queryPending = mDatabase.child("pending_tutors").orderByChild("acc/email").equalTo(userEmail);
        Query queryAccepted = mDatabase.child("accepted_tutors").orderByChild("acc/email").equalTo(userEmail);

        queryPending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("LoginActivity", "Account found in pending tutors for Account: " + userEmail);
                    Toast.makeText(Login1.this, "Your tutor application is still pending approval. Wait for Notification", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    queryAccepted.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("LoginActivity", "Tutor found in accepted tutors for email: " + userEmail);

                                Intent intent = new Intent(Login1.this, Final_dashboard.class);
                                startActivity(intent);
                            } else {
                                Log.d("LoginActivity", "Tutor not found in accepted tutors for email: " + userEmail);
                                progressDialog.dismiss();
                                Toast.makeText(Login1.this, "Account not approved.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(Login1.this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show();
                            Log.d("LoginActivity", "Login canceled: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(Login1.this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "Login canceled: " + databaseError.getMessage());
            }
        });
    }
}