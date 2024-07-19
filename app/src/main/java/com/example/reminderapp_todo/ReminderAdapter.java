package com.example.reminderapp_todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private OnReminderClickListener listener;
    private MainActivity mainActivity;

    public ReminderAdapter(List<Reminder> reminderList, MainActivity mainActivity, OnReminderClickListener listener) {
        this.reminderList = reminderList;
        this.mainActivity = mainActivity;
        this.listener = listener;
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
        holder.titleTextView.setText(reminder.getTitle());
        holder.dateTextView.setText(reminder.getDate());
        holder.timeTextView.setText(reminder.getTime());

        holder.editButton.setOnClickListener(v -> listener.onEditClick(reminder));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(reminder));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public interface OnReminderClickListener {
        void onEditClick(Reminder reminder);
        void onDeleteClick(Reminder reminder);
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, timeTextView;
        ImageButton editButton, deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
