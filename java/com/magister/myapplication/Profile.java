package com.magister.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.magister.myapplication.R;

import java.io.IOException;

public class Profile extends AppCompatActivity {

    private ImageView profileImageView;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Bitmap selectedImageBitmap;

    private TextView profileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileTextView = findViewById(R.id.proff);
        profileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfSetsActivity();
            }
        });

        profileImageView = findViewById(R.id.imageView15);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::handleImagePickerResult);
    }

    private void openProfSetsActivity() {
        Intent intent = new Intent(Profile.this, Prof_Sets.class);
        startActivity(intent);
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void handleImagePickerResult(Uri imageUri) {
        if (imageUri != null) {
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
