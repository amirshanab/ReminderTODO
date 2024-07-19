package com.example.reminderapp_todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private static final int RINGTONE_REQUEST_CODE = 1;
    private TextView ringtoneTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ringtoneTextView = findViewById(R.id.ringtoneTextView);
        Button changeRingtoneButton = findViewById(R.id.changeRingtoneButton);
        Button signOutButton = findViewById(R.id.signOutButton);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String ringtoneUri = sharedPreferences.getString("ringtoneUri", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        ringtoneTextView.setText(RingtoneManager.getRingtone(this, Uri.parse(ringtoneUri)).getTitle(this));

        changeRingtoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneUri));
            startActivityForResult(intent, RINGTONE_REQUEST_CODE);
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                sharedPreferences.edit().putString("ringtoneUri", uri.toString()).apply();
                ringtoneTextView.setText(RingtoneManager.getRingtone(this, uri).getTitle(this));
            }
        }
    }
}
