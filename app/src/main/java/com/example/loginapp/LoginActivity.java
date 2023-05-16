package com.example.loginapp;


import java.util.concurrent.atomic.AtomicBoolean;
import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import android.support.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.EmailAuthProvider;

public class LoginActivity extends AppCompatActivity implements CustomerDB.CustomerDataCheckListener{

    private String email;
    private String password;
    TextView signInWithGoogle;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    CheckBox remember_chk_box;
    CheckBox recaptcha_chk_box;
    SessionManager session;

    GoogleSignInClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session=new SessionManager(LoginActivity.this);
        session.checkLogin();

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        remember_chk_box=(CheckBox) findViewById(R.id.remember_me_chk_box);

        //link to signup activity
        TextView create_account = (TextView) findViewById(R.id.create_account_btn);
        create_account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signup_intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(signup_intent);
            }
        });

        //login process
        Button login_button = (Button) findViewById(R.id.login_btn);
        EditText user_email=(EditText) findViewById(R.id.login_email);
        EditText user_password=(EditText) findViewById(R.id.login_password);

        login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                email=user_email.getText().toString().trim();
                password=user_password.getText().toString().trim();

                if(!email.isEmpty())
                {
                    if(!password.isEmpty()) {
                        AuthenticateUser(email,password);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Please Enter Your Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //resetting password
        TextView reset_password_btn = (TextView) findViewById(R.id.recover_password);
        reset_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = user_email.getText().toString();
                if (!email.isEmpty()) {
                  ResetPassword(email);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                }
        }
        });


        //sign-in with google
        signInWithGoogle=(TextView) findViewById(R.id.signInWithGoogle);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i,1234);
            }
        });

    }

    public void AuthenticateUser(String email, String password)
    {

        String hashedPassword = Security.encryptPassword(password);
        AuthCredential credential = EmailAuthProvider.getCredential(email, hashedPassword);

        progressDialog.setMessage("Logging in ...");
        progressDialog.show();
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        if(remember_chk_box.isChecked())
                        {
                            session.createLoginSession();
                        }
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this,"Login Success", Toast.LENGTH_SHORT).show();

                        Intent login_without_session_intent=new Intent (LoginActivity.this, MainActivity.class);
                        startActivity(login_without_session_intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
    }

    public void ResetPassword(String email)
    {
        progressDialog.setMessage("Sending Email");
        progressDialog.show();

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Email Sent to "+email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // Check if the customer information has already been stored
                                    String userID = FirebaseAuthHelper.getCurrentUserId();
                                    CustomerDB db=new CustomerDB();
                                    db.checkIfCustomerInfoStored(userID,LoginActivity.this);
                                    session.createLoginSession();
                                }else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void oncheckIfCustomerAlreadyStored(String userID,boolean isCustomerInfoStored)
    {
        if(!isCustomerInfoStored)
        {
            String userEmail = FirebaseAuthHelper.getCurrentUserEmail();
            Customer customer = new Customer(userID, "Nil", "Nil", "Nil",userEmail, "Nil", "Nil", null);
            customer.AddCustomerToDatabase();
        }
    }


}