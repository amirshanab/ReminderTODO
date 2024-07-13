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
import com.google.firebase.firestore.DocumentChange;
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

                        reminderList.clear(); // Clear the list before adding new items
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                                Reminder reminder = dc.getDocument().toObject(Reminder.class);
                                reminder.setId(dc.getDocument().getId()); // Set the ID here
                                reminderList.add(reminder);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
        }
    }


    @Override
    public void onEditClick(Reminder reminder) {
        Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
        intent.putExtra("reminder", reminder);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Reminder reminder) {
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
        // You can remove this method if you are not using it
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadReminders();
        }
    }

}