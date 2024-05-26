package com.artakbaghdasaryan.fungus;


import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_PIECES;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_TIMER;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_SHOW_LAST_MOVE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;

public class CreateClassicGame extends AppCompatActivity {
    private EditText _hours;
    private EditText _minutes;
    private EditText _seconds;

    private EditText _increment;

    private boolean _gameCreated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classic_game);

        _hours = findViewById(R.id.hours);
        _minutes = findViewById(R.id.minutes);
        _seconds = findViewById(R.id.seconds);

        _increment = findViewById(R.id.increment);

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   CreateGame();
               }
           }
        );

        Log.d("CREATION", "VREATION MENU");
    }

    private void CreateGame() {
        Intent intent = new Intent();
        intent.setClass(this, ChessGame.class);

        _gameCreated = true;

        String hoursStr = _hours.getText().toString();
        String minutesStr = _minutes.getText().toString();
        String secondsStr = _seconds.getText().toString();
        String incrementStr = _increment.getText().toString();

        if (!isValidInteger(hoursStr) || !isValidInteger(minutesStr) || !isValidInteger(secondsStr) || !isValidInteger(incrementStr)) {
            Toast.makeText(this, "Please enter valid integers in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra("hours",  Integer.parseInt(hoursStr));
        intent.putExtra("minutes",  Integer.parseInt(minutesStr));
        intent.putExtra("seconds",  Integer.parseInt(secondsStr));

        intent.putExtra("increment",  Integer.parseInt(incrementStr));

        intent.putExtra("isNewGame", true);

        startActivity(intent);
    }


    public static boolean isValidInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}