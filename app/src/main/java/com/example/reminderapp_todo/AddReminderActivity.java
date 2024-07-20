package com.example.reminderapp_todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set the minimum date to today
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute)),
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
                            setAlarm(title, date, time); // Set the alarm
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
                            setAlarm(title, date, time); // Set the alarm
                            progressBar.setVisibility(View.GONE);
                            setResult(RESULT_OK); // Set the result
                            finish();
                        })
                        .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
            }
        }
    }

    private void setAlarm(String title, String date, String time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date reminderDate = dateFormat.parse(date + " " + time);
            if (reminderDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(reminderDate);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
