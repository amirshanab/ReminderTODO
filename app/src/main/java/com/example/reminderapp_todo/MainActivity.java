package com.example.reminderapp_todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReminderAdapter.OnReminderClickListener {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        Button addReminderButton = findViewById(R.id.addReminderButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList, this, this);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        addReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });

        loadReminders(currentUser.getUid());
    }

    private void loadReminders(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(userId).collection("reminders")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        reminderList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Reminder reminder = document.toObject(Reminder.class);
                            reminderList.add(reminder);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onEditClick(Reminder reminder) {
        // Implement the edit functionality
        Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
        intent.putExtra("reminder", reminder);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Reminder reminder) {
        // Implement the delete functionality
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users").document(currentUser.getUid()).collection("reminders").document(reminder.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reminderList.remove(reminder);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDoneClick(Reminder reminder) {
        // Implement the mark as done functionality
        reminder.setDone(true);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("users").document(currentUser.getUid()).collection("reminders").document(reminder.getId())
                .set(reminder)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadReminders(mAuth.getCurrentUser().getUid());
        }
    }
}
