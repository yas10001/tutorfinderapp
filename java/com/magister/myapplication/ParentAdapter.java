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

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {

    private List<ParentInfo> parentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ParentInfo parent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        public TextView parentNameTextView;
        public ImageView imageView;
        public TextView parentEmailTextView;
        public Button viewBtn;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentNameTextView = itemView.findViewById(R.id.parentNameTextView);
            parentEmailTextView = itemView.findViewById(R.id.parentEmailTextView);
            imageView = itemView.findViewById(R.id.profileImg);
            viewBtn = itemView.findViewById(R.id.viewBtn);
        }
    }

    public ParentAdapter(List<ParentInfo> parentList) {
        this.parentList = parentList;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_list, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        ParentInfo parent = parentList.get(position);
        holder.parentNameTextView.setText(parent.getFirstName() + " " + parent.getLastName());
        holder.parentEmailTextView.setText(parent.getAcc().getEmail());
        Picasso.get().load(parent.getProfilePictureUrl()).into(holder.imageView);


        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(parent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }
}
