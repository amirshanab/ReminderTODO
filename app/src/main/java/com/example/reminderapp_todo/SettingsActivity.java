package com.example.reminderapp_todo;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private static final int RINGTONE_REQUEST_CODE = 1;
    private TextView selectedRingtoneTextView;
    private Uri selectedRingtoneUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        selectedRingtoneTextView = findViewById(R.id.selectedRingtoneTextView);
        Button changeRingtoneButton = findViewById(R.id.changeRingtoneButton);
        Button signOutButton = findViewById(R.id.signOutButton);

        // Load saved ringtone
        String savedRingtoneUri = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("reminder_ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        selectedRingtoneUri = Uri.parse(savedRingtoneUri);
        selectedRingtoneTextView.setText(RingtoneManager.getRingtone(this, selectedRingtoneUri).getTitle(this));

        changeRingtoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Reminder Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtoneUri);
            startActivityForResult(intent, RINGTONE_REQUEST_CODE);
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingsActivity.this, AuthActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (selectedRingtoneUri != null) {
                selectedRingtoneTextView.setText(RingtoneManager.getRingtone(this, selectedRingtoneUri).getTitle(this));
                // Save selected ringtone
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString("reminder_ringtone", selectedRingtoneUri.toString()).apply();
            }
        }
    }
}
