package com.example.android.insta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passField = (EditText) findViewById(R.id.passField);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }


    public void registerButtonClicked(View view){
        final String name = nameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        final String pass = passField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("Name").setValue(name);
                        current_user_db.child("Image").setValue("default");
                        Toast.makeText(RegisterActivity.this, "Succesfully registered", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "User is already registered. Login to enter.", Toast.LENGTH_SHORT).show();
                        Intent inter=new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(inter);
                    }
                }
            });
        }
        else
        {
            if(name.isEmpty()){
                nameField.setError("Enter name");
            }
            else if(email.isEmpty()){
                emailField.setError("Enter email");
            }
            else if(pass.isEmpty()){
                passField.setError("Enter password");
            }
        }
    }

    public void loginButtonClicked(View view){
        Intent inst = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(inst);
    }
}
