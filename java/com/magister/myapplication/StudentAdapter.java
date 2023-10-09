package com.magister.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<StudentInfo> studentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(StudentInfo student);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView studentNameTextView;
        public ImageView imageView;

        public TextView studentEmailTextView;
        public Button viewBtn;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            studentEmailTextView = itemView.findViewById(R.id.studentEmailTextView);
            imageView = itemView.findViewById(R.id.profileImgs);

            viewBtn = itemView.findViewById(R.id.viewStudent);
        }
    }

    public StudentAdapter(List<StudentInfo> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentInfo student = studentList.get(position);
        holder.studentNameTextView.setText(student.getFirstName() + " " + student.getLastName());
        holder.studentEmailTextView.setText(student.getAcc().getEmail());
        Picasso.get().load(student.getProfilePictureUrl()).into(holder.imageView);


        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(student);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
