package com.magister.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> {

    private List<TutorInfo> tutorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TutorInfo tutor);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        public TextView tutorNameTextView;
        public TextView tutorEmailTextView;
        public ImageView imageView;

        public Button viewTutor;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorNameTextView = itemView.findViewById(R.id.tutorNameTextView);
            tutorEmailTextView = itemView.findViewById(R.id.parentloob);
            imageView = itemView.findViewById(R.id.profileImgss);

            viewTutor = itemView.findViewById(R.id.viewTutor);
        }
    }

    public TutorAdapter(List<TutorInfo> tutorList) {
        this.tutorList = tutorList;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup tutor, int viewType) {
        View view = LayoutInflater.from(tutor.getContext()).inflate(R.layout.tutor_list, tutor, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        TutorInfo tutor = tutorList.get(position);
        holder.tutorNameTextView.setText(tutor.getFirstName() + " " + tutor.getLastName());
        holder.tutorEmailTextView.setText(tutor.getAcc().getEmail());


        holder.viewTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(tutor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }
}
