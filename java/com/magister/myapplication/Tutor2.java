package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.magister.myapplication.R;

public class Tutor2 extends AppCompatActivity implements View.OnClickListener {

    private TextView tutorAccepted;
    private TextView tutorPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor2);

        tutorAccepted = findViewById(R.id.tutorAccepted);
        tutorPending = findViewById(R.id.tutorPending);

        tutorAccepted.setOnClickListener(this);
        tutorPending.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tutorAccepted:

                Intent intent = new Intent(Tutor2.this, tutorlegit.class);
                startActivity(intent);
                break;
            case R.id.tutorPending:
                 intent = new Intent(Tutor2.this, tutorloob.class);
                startActivity(intent);
                break;
        }
    }
}
