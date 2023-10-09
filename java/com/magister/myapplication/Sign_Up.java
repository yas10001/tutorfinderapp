package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.magister.myapplication.R;

public class Sign_Up extends AppCompatActivity {

    private RadioButton studentRadio;
    private RadioButton tutorRadio;
    private RadioGroup radioGroup;
    private Button createButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        studentRadio = findViewById(R.id.radioButton);
        tutorRadio = findViewById(R.id.radioButton2);
        radioGroup = findViewById(R.id.raDioGroup);
        createButton = findViewById(R.id.button2);
        cancelButton = findViewById(R.id.button4);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentRadio.isChecked()) {
                    launchCreateAccountActivity("student");
                } else if (tutorRadio.isChecked()) {
                    launchCreateAccountActivity("tutor");
                } else {
                    Toast.makeText(Sign_Up.this, "Please select user type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_Up.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void launchCreateAccountActivity(String userType) {
        Intent intent = new Intent(Sign_Up.this, Information.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }
}
