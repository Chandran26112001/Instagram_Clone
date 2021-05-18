package com.example.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Context mContext;
    TextView link_signup;
    EditText mEmail,mPassword;
    Button btn_login;
    ProgressBar mProgressBar;

    private FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mProgressBar = findViewById(R.id.loginRequestLoadingProgressbar);
        mProgressBar.setVisibility(View.GONE);
        mContext = LoginActivity.this;

        //firebase
        init();

        auth = FirebaseAuth.getInstance();

        link_signup = findViewById(R.id.link_signup);
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    //for login
    private void init() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(email.equals("") || password.equals("")) {
                    Toast.makeText(mContext, "Fields cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        mProgressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(mContext, HomeActivity.class));
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(mContext, "Authentication Failed !", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
        /**
         * while signing in first time after successful sign in takes to Home Activity
         */
        //if(auth.getCurrentUser() != null) {
           // startActivity(new Intent(mContext, HomeActivity.class));
            //Toast.makeText(mContext, "Vaangana", Toast.LENGTH_SHORT).show();
            //finish();
        //}
    }
}
