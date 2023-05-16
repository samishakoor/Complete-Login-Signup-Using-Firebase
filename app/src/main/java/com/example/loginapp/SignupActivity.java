package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;


public class SignupActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String password;
    private String confirm_password;
    FirebaseAuth auth;
    Customer customer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        // link to login activity
        TextView login_page_btn = (TextView) findViewById(R.id.login_account_btn);
        Intent login_intent = new Intent(SignupActivity.this,LoginActivity.class);

        login_page_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(login_intent);
                finish();
            }
        });


        //signup process
        Button signupButton = (Button) findViewById(R.id.signup_btn);
        EditText name_text=(EditText) findViewById(R.id.signup_name);
        EditText email_text=(EditText) findViewById(R.id.signup_email);
        EditText password_text=(EditText) findViewById(R.id.signup_password);
        EditText confirm_password_text=(EditText) findViewById(R.id.signup_confirm_password);
        signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                name=name_text.getText().toString().trim();
                email=email_text.getText().toString().trim();
                password=password_text.getText().toString().trim();
                confirm_password=confirm_password_text.getText().toString().trim();

            if(!name.isEmpty()) {
                if (!password.isEmpty()) {
                    if (!confirm_password.isEmpty()) {
                        if (password.equals(confirm_password)) {
                            String hashedPassword = Security.encryptPassword(password);
                            progressDialog.setMessage("Signing up...");
                            progressDialog.show();
                            auth.createUserWithEmailAndPassword(email,hashedPassword)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {

                                            AuthCredential credential = EmailAuthProvider.getCredential(email, hashedPassword);
                                            FirebaseUser user = authResult.getUser();
                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.cancel();
                                                            String userID = FirebaseAuthHelper.getCurrentUserId();
                                                            customer = new Customer(userID, name, "Nil", "Nil", email, "Nil", "Nil", null);
                                                            customer.AddCustomerToDatabase();
                                                            startActivity(login_intent);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Incorrect Password Confirmation", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(SignupActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

}