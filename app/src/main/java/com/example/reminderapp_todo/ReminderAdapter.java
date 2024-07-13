package com.example.reminderapp_todo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private OnReminderClickListener listener;

    public ReminderAdapter(List<Reminder> reminderList, Context context, OnReminderClickListener listener) {
        this.reminderList = reminderList;
        this.context = context;
        this.listener = listener;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.reminderTitleTextView.setText(reminder.getTitle());

        holder.editReminderButton.setOnClickListener(v -> listener.onEditClick(reminder));
        holder.deleteReminderButton.setOnClickListener(v -> listener.onDeleteClick(reminder));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView reminderTitleTextView;
        ImageView editReminderButton;
        ImageView deleteReminderButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderTitleTextView = itemView.findViewById(R.id.reminderTitleTextView);
            editReminderButton = itemView.findViewById(R.id.editReminderButton);
            deleteReminderButton = itemView.findViewById(R.id.deleteReminderButton);
        }
    }

    public interface OnReminderClickListener {
        void onEditClick(Reminder reminder);
        void onDeleteClick(Reminder reminder);
        void onDoneClick(Reminder reminder);
    }
}
