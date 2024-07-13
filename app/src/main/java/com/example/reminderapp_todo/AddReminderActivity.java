package com.example.reminderapp_todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, timeEditText;
    private ProgressBar progressBar;
    private Button saveButton;

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
        progressBar = findViewById(R.id.progressBar);
        saveButton = findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get the reminder from the intent
        reminder = getIntent().getParcelableExtra("reminder");

        if (reminder != null) {
            // Populate the fields with the reminder details
            titleEditText.setText(reminder.getTitle());
            dateEditText.setText(reminder.getDate());
            timeEditText.setText(reminder.getTime());
        }

        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> timeEditText.setText(hourOfDay + ":" + minute),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            // Show error message or handle empty fields
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (reminder == null) {
                // Add new reminder
                reminder = new Reminder(title, date, time);
                db.collection("users").document(userId).collection("reminders")
                        .add(reminder)
                        .addOnSuccessListener(documentReference -> {
                            reminder.setId(documentReference.getId()); // Set the ID
                            db.collection("users").document(userId).collection("reminders")
                                    .document(reminder.getId())
                                    .set(reminder); // Save the reminder with the ID
                            progressBar.setVisibility(View.GONE);
                            setResult(RESULT_OK); // Set the result
                            finish();
                        })
                        .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
            } else {
                // Update existing reminder
                reminder.setTitle(title);
                reminder.setDate(date);
                reminder.setTime(time);
                db.collection("users").document(userId).collection("reminders")
                        .document(reminder.getId())
                        .set(reminder)
                        .addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            setResult(RESULT_OK); // Set the result
                            finish();
                        })
                        .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
            }
        }
    }

}
