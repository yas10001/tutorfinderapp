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

public class studentloob extends AppCompatActivity implements StudentAdapter.OnItemClickListener {

    private DatabaseReference studentsRef;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<StudentInfo> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentloob);

        studentsRef = FirebaseDatabase.getInstance().getReference("users");
        recyclerView = findViewById(R.id.studentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList);
        studentAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(studentAdapter);

        retrieveStudents();
    }

    private void retrieveStudents() {
        Query studentQuery = studentsRef.orderByChild("acc/role").equalTo("student");
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    studentList.clear(); // Clear the list before adding new students
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        StudentInfo student = studentSnapshot.getValue(StudentInfo.class);
                        if (student != null) {
                            studentList.add(student);
                        }
                    }
                    studentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(studentloob.this, "No students found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(studentloob.this, "Failed to retrieve student data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(StudentInfo student) {
        Intent intent = new Intent(studentloob.this, StudentDetailsActivity.class);
        intent.putExtra("student", student);
        startActivity(intent);
    }
}
