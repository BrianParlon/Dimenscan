package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    private String name;
    private EditText email, password;
    private Button signIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        email = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editTextTextPassword);


        Button loginBtn = (Button) findViewById(R.id.Login);
        loginBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        Button signupBtn = (Button) findViewById(R.id.button1);
        signupBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button1:
                startActivity(new Intent(this, SignUp.class));
                break;

            case R.id.Login:
                userLogin();
                break;
        }
    }

    private void userLogin() {

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if(userEmail.isEmpty()){
            email.setError("Please enter your email");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }

        if(userPassword.isEmpty()){
            password.setError("Please enter a password");
            password.requestFocus();
            return;
        }

        if(userPassword.length()<6){
            password.setError("Password must be longer than 6 characters");
            password.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                        startActivity(new Intent(MainActivity.this,Profile.class));

                }else{
                    Toast.makeText(MainActivity.this,"Failed to login",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}