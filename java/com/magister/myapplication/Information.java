package com.magister.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Information extends AppCompatActivity {

    private CheckBox checkBox;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        getSupportActionBar().hide();

        TextView textView21 = findViewById(R.id.textView21);
        checkBox = findViewById(R.id.checkBox);
        Button button = findViewById(R.id.button7);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);

        button.setEnabled(false);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Information.this, Dashboard1.class);
                startActivity(intent);
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                button.setEnabled(isChecked);
            }
        });

        String termsOfService = "I agree to the Terms of Service and the Privacy Policy.";
        String text = "Already have an account? Sign in here.";
        SpannableString ss = new SpannableString(text);
        SpannableString spannable = new SpannableString(termsOfService);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(Information.this, "Terms of Service", Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(Information.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (checkBox.isChecked()) {
                    startActivity(new Intent(Information.this, Dashboard.class));
                } else {
                    Toast.makeText(Information.this, "Please agree to the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
                }
            }

        };

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);

        spannable.setSpan(clickableSpan, 15, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(clickableSpan2, 40, 55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan1, 25, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(boldSpan, 25, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(underlineSpan, 25, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(blackSpan, 25, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setText(spannable);

        textView21.setText(ss);
    }

}

