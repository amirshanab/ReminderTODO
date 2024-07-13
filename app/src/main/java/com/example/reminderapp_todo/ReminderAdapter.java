package com.example.reminderapp_todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private Context context;
    private OnReminderClickListener onReminderClickListener;

    public ReminderAdapter(List<Reminder> reminderList, Context context, OnReminderClickListener onReminderClickListener) {
        this.reminderList = reminderList;
        this.context = context;
        this.onReminderClickListener = onReminderClickListener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.dateTextView.setText(reminder.getDate());
        holder.timeTextView.setText(reminder.getTime());

        holder.editImageView.setOnClickListener(v -> onReminderClickListener.onEditClick(reminder));
        holder.deleteImageView.setOnClickListener(v -> onReminderClickListener.onDeleteClick(reminder));
        holder.doneImageView.setOnClickListener(v -> onReminderClickListener.onDoneClick(reminder));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, dateTextView, timeTextView;
        ImageView editImageView, deleteImageView, doneImageView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            editImageView = itemView.findViewById(R.id.editImageView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);
            doneImageView = itemView.findViewById(R.id.doneImageView);
        }
    }

    public interface OnReminderClickListener {
        void onEditClick(Reminder reminder);
        void onDeleteClick(Reminder reminder);
        void onDoneClick(Reminder reminder);
    }
}
