package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

        Button signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.signUp:
                registerUser();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    private void registerUser(){
        EditText accName = findViewById(R.id.editTextTextPersonName2);
        TextView welcome = findViewById(R.id.textView13);
        EditText fName = findViewById(R.id.editTextTextPersonName3);
        EditText lName = findViewById(R.id.editTextTextPersonName4);
        EditText pwrd = findViewById(R.id.editTextTextPersonName6);
        EditText userEmail = findViewById(R.id.editTextTextPersonName5);

        String accountName = accName.getText().toString().trim();
        String firstName = fName.getText().toString().trim();
        String lastName = lName.getText().toString().trim();
        String password = pwrd.getText().toString().trim();
        String email = userEmail.getText().toString().trim();

        if(accountName.isEmpty()){
            accName.setError("account name required");
            accName.requestFocus();
            return;
        }
       if(firstName.isEmpty()){
           fName.setError("first name required");
           fName.requestFocus();
           return;
       }
        if(lastName.isEmpty()){
            lName.setError("last name required");
            lName.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Appropriate email required");
            userEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            pwrd.setError("password required");
            pwrd.requestFocus();
            return;
        }

        if(password.length()<6){
            pwrd.setError("Must be 6 characters");
            pwrd.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(accountName,firstName,lastName,password,email);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUp.this,"User has been added!",Toast.LENGTH_LONG).show();

                                            }else{
                                                Toast.makeText(SignUp.this,"Failed to register",Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(SignUp.this,"Failed to register",Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
}