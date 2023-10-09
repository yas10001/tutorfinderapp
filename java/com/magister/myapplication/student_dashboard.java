package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.magister.myapplication.HomeAdapter.FeaturedAdapter;
import com.magister.myapplication.HomeAdapter.FeaturedHelperClass;
import com.magister.myapplication.R;

import java.util.ArrayList;

public class student_dashboard extends AppCompatActivity {

    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.student_dashboard);

        featuredRecycler = findViewById(R.id.featured_recycler);

        featuredRecycler();
    }

    private void featuredRecycler() {

        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

        featuredLocations.add(new FeaturedHelperClass(R.drawable.tutor, "Tutor", "Hawd ko ana dol kaya ra kay na "));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.tutor, "Tutor", "Hawd ko ana dol kaya ra kay na "));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.tutor, "Tutor", "Hawd ko ana dol kaya ra kay na "));

        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);

        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffeff400, 0xffaff600});
    }
}