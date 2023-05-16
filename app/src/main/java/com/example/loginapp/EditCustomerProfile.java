package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;


import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class EditCustomerProfile extends AppCompatActivity {

    TextInputEditText editName;
    TextInputEditText editDOB;
    TextInputEditText editCNIC;
    TextInputEditText editAge;
    TextInputEditText editContact;
    Button save_btn;
    FirebaseAuth mAuth;
    String customerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer_profile);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser!=null) {
            customerID=currentUser.getUid();
        }
        else {
            customerID=null;
        }


        String name=getIntent().getStringExtra("name");
        String dob=getIntent().getStringExtra("dob");
        String cnic=getIntent().getStringExtra("cnic");
        String contact=getIntent().getStringExtra("contact");
        String age=getIntent().getStringExtra("age");



        editName=(TextInputEditText) findViewById(R.id.editName);
        editDOB=(TextInputEditText) findViewById(R.id.editDOB);
        editCNIC=(TextInputEditText) findViewById(R.id.editCNIC);
        editAge=(TextInputEditText) findViewById(R.id.editAge);
        editContact=(TextInputEditText) findViewById(R.id.editContact);


        if(!name.equals("Nil")) {
            editName.setText(name);
        }

        if(!dob.equals("Nil")) {
            editDOB.setText(dob);
        }

        if(!cnic.equals("Nil")) {
            editCNIC.setText(cnic);
        }

        if(!contact.equals("Nil")) {
            editContact.setText(contact);
        }

        if(!age.equals("Nil")) {
            editAge.setText(age);
        }
        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Calendar calendar= Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog =new DatePickerDialog(EditCustomerProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        editDOB.setText(dateFormat.format(calendar.getTime()));
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });



        save_btn=(Button) findViewById(R.id.save_edit_profile_btn);
        save_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String editedName=editName.getText().toString().trim();
                String editedDOB=editDOB.getText().toString().trim();
                String editedCNIC=editCNIC.getText().toString().trim();
                String editedContact=editContact.getText().toString().trim();
                String editedAge=editAge.getText().toString().trim();

                if (editedName.isEmpty()) {
                    editedName="Nil";
                }

                if (editedDOB.isEmpty()) {
                    editedDOB="Nil";
                }

                if (editedCNIC.isEmpty()) {
                    editedCNIC="Nil";
                }

                if (editedContact.isEmpty()) {
                    editedContact="Nil";
                }

                if (editedAge.isEmpty()) {
                    editedAge="Nil";
                }

                //save the edited customer data in database
                Customer customer =new Customer();
                customer.setCustomerID(customerID);
                customer.setName(editedName);
                customer.setPhoneNumber(editedContact);
                customer.setCNIC(editedCNIC);
                customer.setDOB(editedDOB);
                customer.setAge(editedAge);
                customer.UpdateCustomerToDatabase();

                Intent user_profile_intent = new Intent(EditCustomerProfile.this,CustomerProfile.class);
                startActivity(user_profile_intent);
                finish();
            }
       });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditCustomerProfile.this,CustomerProfile.class);
        startActivity(intent);
        finish();
    }

}

