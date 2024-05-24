package com.artakbaghdasaryan.fungus;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.artakbaghdasaryan.fungus.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ReplaceFragment(new PlayFragment());

        binding.bottomNavigationBar.setOnItemSelectedListener(menuItem -> {

            if(menuItem.getItemId() == R.id.play_fragment){
                ReplaceFragment(new PlayFragment());
            }
            else if(menuItem.getItemId() == R.id.settings_fragment){
                ReplaceFragment(new SettingsFragment());
            }
            else if(menuItem.getItemId() == R.id.profile_fragment){
                ReplaceFragment(new ProfileFragment());
            }

            return true;
        });
    }

    public void ReplaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}