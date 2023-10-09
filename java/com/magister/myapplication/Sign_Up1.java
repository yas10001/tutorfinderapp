package com.magister.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_Up1 extends AppCompatActivity {

    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText confirmPassword;
    private Button register;
    private CheckBox acceptCheckBox;

    private RadioGroup radioGroup;
    private Button registerButton;
    private boolean isTermsAccepted = false;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    private boolean isPasswordConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm);
        acceptCheckBox = findViewById(R.id.acceptCheckBox);
        radioGroup = findViewById(R.id.radioGroups);
        registerButton = findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String passwordText = s.toString().trim();

                if (TextUtils.isEmpty(passwordText)) {
                    password.setError("Password is required.");
                } else {
                    if (isStrongPassword(passwordText)) {
                        password.setError(null);
                    } else {
                        password.setError("Password must be strong. Use a combination of at least 8 characters, including uppercase, lowercase, and numeric characters.");
                    }
                }
            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String passwordText = password.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(confirmPasswordText)) {
                    confirmPassword.setError("Confirm password is required.");
                    isPasswordConfirmed = false;
                } else if (!confirmPasswordText.equals(passwordText)) {
                    confirmPassword.setError("Passwords do not match.");
                    isPasswordConfirmed = false;
                } else {
                    isPasswordConfirmed = true;
                }
            }
        });
        TextView loginTextView = findViewById(R.id.textView45);
        String loginText = "Already have an account? Login.";
        SpannableString spannableLoginText = new SpannableString(loginText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Sign_Up1.this, Login1.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };
        spannableLoginText.setSpan(clickableSpan, loginText.indexOf("Login"), loginText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTextView.setText(spannableLoginText);
        loginTextView.setMovementMethod(LinkMovementMethod.getInstance());


        String termsAndConditionsText = "I agree to the Terms and Conditions";
        SpannableString spannableString = new SpannableString(termsAndConditionsText);
        ClickableSpan aclickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showTermsAndConditions();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };

        spannableString.setSpan(aclickableSpan, 0, termsAndConditionsText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        acceptCheckBox.setText(spannableString);
        acceptCheckBox.setMovementMethod(LinkMovementMethod.getInstance());


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailText = email.getText().toString().trim();

                if (TextUtils.isEmpty(emailText)) {
                    email.setError("Email is required.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    email.setError("Invalid email format.");
                }
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneText = phone.getText().toString().trim();

                if (TextUtils.isEmpty(phoneText)) {
                    phone.setError("Phone is required.");
                } else if (!isPhoneNumberValid(phoneText)) {
                    phone.setError("Invalid phone number.");
                }
            }
        });
    }

    private void registerUser() {
        String emailText = email.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailText)) {
            email.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(phoneText)) {
            phone.setError("Phone is required.");
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            password.setError("Password is required.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email format.");
            return;
        }

        if (!isPhoneNumberValid(phoneText)) {
            phone.setError("Invalid phone number.");
            return;
        }

        if (!isStrongPassword(passwordText)) {
            password.setError("Password must be strong. Use a combination of at least 8 characters, including uppercase, lowercase, and numeric characters.");
            return;
        }

        if (!isPasswordConfirmed) {
            confirmPassword.setError("Passwords do not match.");
            return;
        }

        if (!isTermsAccepted) {
            Toast.makeText(Sign_Up1.this, "You must accept the terms and conditions.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                            String role;
                            Intent intent;

                            // Send email verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> emailTask) {
                                        if (emailTask.isSuccessful()) {
                                            // Email verification sent successfully
                                            Toast.makeText(Sign_Up1.this, "Email verification sent.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Failed to send email verification
                                            Toast.makeText(Sign_Up1.this, "Failed to send email verification.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            if (selectedRadioButtonId == R.id.studentRadio) {
                                role = "STUDENT";
                                intent = new Intent(Sign_Up1.this, more_info_student.class);
                            } else if (selectedRadioButtonId == R.id.tutorRadio) {
                                role = "TUTOR";
                                intent = new Intent(Sign_Up1.this, more_info_tutor.class);
                            } else if (selectedRadioButtonId == R.id.tutorParent) {
                                role = "PARENT";
                                intent = new Intent(Sign_Up1.this, more_info_parent.class);
                            } else {
                                // No radio button selected, display an error message
                                Toast.makeText(Sign_Up1.this, "Please select a role", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            intent.putExtra("email", emailText);
                            intent.putExtra("phone", phoneText);
                            intent.putExtra("role", role);
                            intent.putExtra("password", passwordText);

                            startActivity(intent);
                        } else {
                            // Email authentication failed
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        email.setError("Invalid email format.");
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        email.setError("Email already in use.");
                                        break;
                                    default:
                                        Toast.makeText(Sign_Up1.this, "Email authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    }
                });
    }




        private boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
            return digitsOnly.length() == 11;
        } else if (phoneNumber.startsWith("+")) {
            String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
            return digitsOnly.length() == 12;
        }
        return false;
    }

    private boolean isStrongPassword(String password) {
        // Use your own criteria for password strength
        return password.length() >= 8 && containsUpperCase(password) && containsLowerCase(password) && containsDigit(password);
    }

    private boolean containsUpperCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }


    private void showTermsAndConditions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("Terms and Conditions of the Magister\n" +
                "\n" +
                "Please read these terms and conditions carefully before using the Magister: Tutor Finder application. By using the app, you agree to be bound by these terms and conditions. If you do not agree with any part of these terms and conditions, please refrain from using the app.\n" +
                "\n" +
                "1. App Usage\n" +
                "\n" +
                "1.1 The Tutor Finder app is a platform designed to connect students, parents with tutors for educational purposes. The app facilitates the process of finding, scheduling, and managing tutoring sessions.\n" +
                "\n" +
                "1.2 The app is intended for users who are 18 years of age or older. If you are under 18, you must obtain consent from a parent or legal guardian before using the app.\n" +
                "\n" +
                "1.3 Users are responsible for providing accurate and up-to-date information during the registration process. The app is not liable for any consequences resulting from the provision of false or misleading information.\n" +
                "\n" +
                "2. User Responsibilities\n" +
                "\n" +
                "2.1 Users are solely responsible for their interactions and communications with Tutors. The app does not guarantee the quality, accuracy, or reliability of the tutoring services provided by Tutors.\n" +
                "\n" +
                "2.2 Users must conduct themselves in a professional and respectful manner when using the app. Any form of harassment, abusive behavior, or violation of the rights of others will not be tolerated and may result in the termination of the user's account.\n" +
                "\n" +
                "2.3 Users are responsible for maintaining the confidentiality of their account login credentials. Any unauthorized use of an account must be reported immediately to the app's support team.\n" +
                "\n" +
                "2.4 Users are responsible for ensuring the compatibility of their devices and internet connections to access and use the app. The app is not responsible for any technical issues that may arise from the user's equipment or internet service provider.\n" +
                "\n" +
                "3. Tutor Responsibilities\n" +
                "\n" +
                "3.1 Tutors must provide accurate information regarding their qualifications, experience, and areas of expertise. Any misrepresentation or falsification of information may result in the termination of the tutor's account.\n" +
                "\n" +
                "3.2 Tutors must conduct themselves professionally and ethically when interacting with users. They must adhere to the agreed-upon schedule, deliver the agreed-upon tutoring services, and provide accurate and constructive feedback.\n" +
                "\n" +
                "3.3 Tutors must maintain the confidentiality of user information obtained through the app. Sharing or using such information for any purpose other than providing tutoring services is strictly prohibited.\n" +
                "\n" +
                "4. Payments\n" +
                "\n" +
                "4.1 The app may facilitate payments between Users and Tutors for tutoring services. Users agree to pay the agreed-upon fees to Tutors for the services rendered. The app is not responsible for any disputes or issues arising from payments made between Users and Tutors.\n" +
                "\n" +
                "6. Termination\n" +
                "\n" +
                "6.1 The app reserves the right to terminate or suspend any user's account at its discretion, without prior notice, for any reason, including but not limited to violation of these terms and conditions\n" +
                "\n" +
                "6.2 Users and Tutors may terminate their accounts at any time by following the app's account closure process.\n" +
                "\n" +
                "7. Modifications\n" +
                "\n" +
                "7.1 The app reserves the right to modify or update these terms and conditions at any time. Users will be notified of any material changes, and continued use of the app after the changes will constitute acceptance of the modified terms and conditions.\n" +
                "\n" +
                "By using the Magister, you acknowledge that you have read, understood, and agree to be bound by these terms and conditions.\n");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isTermsAccepted = true;
                acceptCheckBox.setChecked(true);
                dialog.dismiss();
            }
        });
        acceptCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                registerButton.setEnabled(isChecked);
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isTermsAccepted = false;
                acceptCheckBox.setChecked(false);
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
