package com.artakbaghdasaryan.fungus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.artakbaghdasaryan.fungus.databinding.ActivityLoginBinding;
import com.artakbaghdasaryan.fungus.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        _binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        _binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_binding.email.getText().toString().isEmpty() || _binding.password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fields can not be empty.", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(_binding.email.getText().toString(), _binding.password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }

                                }
                            });
                }
            }
        });

    }
}