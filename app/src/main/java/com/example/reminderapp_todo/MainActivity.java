package com.example.reminderapp_todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private TextView noRemindersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noRemindersTextView = findViewById(R.id.noRemindersTextView);
        Button addReminderButton = findViewById(R.id.addReminderButton);
        Button settingsButton = findViewById(R.id.settingsButton);

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

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        loadReminders();
    }

    private void loadReminders() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            db.collection("users").document(currentUser.getUid()).collection("reminders")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        reminderList.clear(); // Clear the list before adding updated data
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Reminder reminder = document.toObject(Reminder.class);
                            if (reminder != null) {
                                reminder.setId(document.getId());
                                reminderList.add(reminder);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                        // Show or hide the "No Reminders Set Yet" message based on the list
                        if (reminderList.isEmpty()) {
                            noRemindersTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noRemindersTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onEditClick(Reminder reminder) {
        Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
        intent.putExtra("reminder", reminder);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onDeleteClick(Reminder reminder) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("reminders")
                    .document(reminder.getId())
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reminderList.remove(reminder);
                            adapter.notifyDataSetChanged();

                            // Show or hide the "No Reminders Set Yet" message based on the list
                            if (reminderList.isEmpty()) {
                                noRemindersTextView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadReminders();
        }
    }
}
