package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.magister.myapplication.R;

public class Login extends AppCompatActivity {
    private RadioButton studentRadio;
    private RadioButton tutorRadio;
    private RadioButton adminRadio;
    private Button createButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        studentRadio = findViewById(R.id.radioButton5);
        tutorRadio = findViewById(R.id.radioButton4);
        adminRadio = findViewById(R.id.radioButton3);
        createButton = findViewById(R.id.button5);
        cancelButton = findViewById(R.id.buttonton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentRadio.isChecked()) {
                    launchCreateAccountActivity("student");
                } else if (tutorRadio.isChecked()) {
                    launchCreateAccountActivity("tutor");
                } else if (adminRadio.isChecked()) {
                    launchCreateAccountActivity("admin");
                } else {
                    Toast.makeText(Login.this, "Please select user type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView forgotPasswordTextView = findViewById(R.id.textView17);
        String forgotPasswordText = "Forgot Password?";
        SpannableString spannableString = new SpannableString(forgotPasswordText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(Login.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        };
        spannableString.setSpan(clickableSpan, 0, forgotPasswordText.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), 0, 16, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        forgotPasswordTextView.setText(spannableString);
        forgotPasswordTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void launchCreateAccountActivity(String userType) {
        Intent intent = new Intent(Login.this, Information.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }
}
