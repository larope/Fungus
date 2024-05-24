package com.artakbaghdasaryan.fungus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.artakbaghdasaryan.fungus.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding _binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        _binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(_binding.name.getText().toString().isEmpty() || _binding.email.getText().toString().isEmpty() || _binding.password.getText().toString().isEmpty() || _binding.passwordRepeat.getText().toString().isEmpty()){
                     Toast.makeText(Register.this, "Fields can not be empty.", Toast.LENGTH_SHORT).show();
                 }
                 else if(!_binding.passwordRepeat.getText().toString().isEmpty() && !_binding.password.getText().toString().equals(_binding.passwordRepeat.getText().toString())){
                     Toast.makeText(Register.this, "Passwords does not match.", Toast.LENGTH_SHORT).show();
                 }
                 else{
                     FirebaseAuth.getInstance().createUserWithEmailAndPassword(_binding.email.getText().toString(), _binding.password.getText().toString())
                             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if(task.isSuccessful()){
                                         HashMap<String, String> userInfo = new HashMap<>();

                                         userInfo.put("email", _binding.email.getText().toString());
                                         userInfo.put("username", _binding.name.getText().toString());

                                         FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);

                                         startActivity(new Intent(Register.this, MainActivity.class));
                                     }
                                 }
                             });

                 }
            }
        });
    }
}