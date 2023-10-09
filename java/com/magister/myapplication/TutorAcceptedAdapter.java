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

public class TutorAcceptedAdapter extends RecyclerView.Adapter<TutorAcceptedAdapter.TutorViewHolder> {

    private List<TutorInfo> tutorlegit;
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
            tutorNameTextView = itemView.findViewById(R.id.tutor);
            tutorEmailTextView = itemView.findViewById(R.id.emailadd);
            imageView = itemView.findViewById(R.id.profileImg);
            viewTutor = itemView.findViewById(R.id.viewTutorAcc);

        }
    }

    public TutorAcceptedAdapter(List<TutorInfo> tutorlegit) {
        this.tutorlegit = tutorlegit;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup tutor, int viewType) {
        View view = LayoutInflater.from(tutor.getContext()).inflate(R.layout.tutor_accepted, tutor, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        TutorInfo tutor = tutorlegit.get(position);
        holder.tutorNameTextView.setText(tutor.getFirstName() + " " + tutor.getLastName());

        LaborClass laborClass = tutor.getAcc();
        if (laborClass != null) {
            holder.tutorEmailTextView.setText(laborClass.getEmail()); // Access the email property
        }
        Picasso.get().load(tutor.getProfilePictureUrl()).into(holder.imageView);

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
        return tutorlegit.size();
    }
}
