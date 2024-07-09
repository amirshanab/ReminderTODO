package com.example.reminderapp_todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_REMINDER_REQUEST = 1;

    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(reminderAdapter);

        // Floating action button to add new reminder
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });

        // Populate some dummy data
        loadDummyData();
    }

    private void loadDummyData() {
        reminderList.add(new Reminder("Buy groceries", "2024-07-10 10:00 AM"));
        reminderList.add(new Reminder("Doctor's appointment", "2024-07-11 02:00 PM"));
        reminderList.add(new Reminder("Meeting with team", "2024-07-12 11:00 AM"));
        reminderAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Handle settings action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String title = data.getStringExtra("newReminderTitle");
                String dateTime = data.getStringExtra("newReminderDateTime");
                reminderList.add(new Reminder(title, dateTime));
                reminderAdapter.notifyDataSetChanged();
            }
        }
    }
}
