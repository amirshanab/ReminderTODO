package com.example.reminderapp_todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, timeEditText;
    private Button saveButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Reminder reminder = getIntent().getParcelableExtra("reminder");
        if (reminder != null) {
            titleEditText.setText(reminder.getTitle());
            dateEditText.setText(reminder.getDate());
            timeEditText.setText(reminder.getTime());
        }

        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> dateEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> timeEditText.setText(hourOfDay + ":" + minute1), hour, minute, true);
        timePickerDialog.show();
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Reminder newReminder = new Reminder(title, date, time);

            db.collection("users").document(userId).collection("reminders")
                    .add(newReminder)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.GONE);
                        finish();
                    })
                    .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
        }
    }
}
