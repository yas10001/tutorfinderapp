package com.magister.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.magister.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class parentloob extends AppCompatActivity implements ParentAdapter.OnItemClickListener {

    private DatabaseReference usersRef;
    private RecyclerView recyclerView;
    private ParentAdapter parentAdapter;
    private List<ParentInfo> parentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentloob);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView = findViewById(R.id.parentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        parentList = new ArrayList<>();
        parentAdapter = new ParentAdapter(parentList);
        parentAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(parentAdapter);

        retrieveParents();
    }

    private void retrieveParents() {
        Query parentQuery = usersRef.orderByChild("acc/role").equalTo("parent");
        parentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    parentList.clear(); // Clear the list before adding new parents
                    for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                        ParentInfo parent = parentSnapshot.getValue(ParentInfo.class);
                        if (parent != null) {
                            parentList.add(parent);
                        }
                    }
                    parentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(parentloob.this, "No parents found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(parentloob.this, "Failed to retrieve parent data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(ParentInfo parent) {
        Intent intent = new Intent(parentloob.this, ParentDetailsActivity.class);
        intent.putExtra("parent", parent);
        startActivity(intent);
    }
}
