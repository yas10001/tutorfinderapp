package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.magister.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificationActivity extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private Button verifyButton;
    private Button resendButton;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();

        verificationCodeEditText = findViewById(R.id.verification_code_edittext);
        verifyButton = findViewById(R.id.verify_button);
        resendButton = findViewById(R.id.resend_button);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verificationCodeEditText.getText().toString().trim();
                verifyUser(verificationCode);
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode();
            }
        });
    }

    private void verifyUser(String verificationCode) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Use the verification code entered by the user
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(user.getUid(), verificationCode);
            user.updatePhoneNumber(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VerificationActivity.this, "Verification successful.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(VerificationActivity.this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private String generateVerificationCode() {
        // Generate a random 6-digit verification code
        int min = 100000;
        int max = 999999;
        int randomCode = min + (int) (Math.random() * (max - min + 1));
        return String.valueOf(randomCode);
    }



    private void resendVerificationCode() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VerificationActivity.this, "Verification code resent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(VerificationActivity.this, "Failed to resend verification code.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
