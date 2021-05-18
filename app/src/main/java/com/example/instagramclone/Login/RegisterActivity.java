package com.example.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.models.User;
import com.example.instagramclone.models.UserAccountSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    EditText mEmail,mUsername,mPassword;
    Button btn_register;
    String email,username,password;
    ProgressBar mProgressBar;
    Context mContext;
    private String append = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();


        //initializing widgets
        mContext = RegisterActivity.this;
        mProgressBar = findViewById(R.id.loginRequestLoadingProgressbar_r);
        mProgressBar.setVisibility(View.GONE);
        btn_register = findViewById(R.id.btn_register);
        mEmail = findViewById(R.id.input_email_r);
        mPassword = findViewById(R.id.input_password_r);
        mUsername = findViewById(R.id.input_username_r);

        init();

    }
    private void init() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();
                if(email.equals("") || password.equals("") || username.equals("")) {
                    Toast.makeText(mContext, "Fields Cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(mContext, "Registration Success", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);

                                        //adding the values to database
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        reference = FirebaseDatabase.getInstance().getReference();

                                        User mUser = new User(email, "1888888888",firebaseUser.getUid(),username);
                                        reference.child("users").child(firebaseUser.getUid()).setValue(mUser);

                                        UserAccountSettings settings = new UserAccountSettings(
                                                "",username,0,0,0,"",
                                                StringManipulation.condenseUsername(username),""
                                        );
                                        reference.child("user_account_settings").child(firebaseUser.getUid()).setValue(settings);
                                        startActivity(new Intent(mContext, HomeActivity.class));

                                    }
                                    else {
                                        Toast.makeText(mContext, "Registration Failed ! Try Again", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
    }
    //checking if username already exists

    /**
     *
     * private boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
     *         User user = new User();
     *         for (DataSnapshot ds: dataSnapshot.getChildren()) {
     *             user.setUsername(ds.getValue(User.class).getUsername());
     *             if(StringManipulation.expandUsername(user.getUsername()).equals(username)) {
     *                 return true;
     *             }
     *         }
     *         return false;
     *     }
     * @param username
     * @param dataSnapshot
     * @return
     */

}
