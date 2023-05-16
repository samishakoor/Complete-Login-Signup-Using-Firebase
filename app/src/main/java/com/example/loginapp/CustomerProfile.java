package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;

import android.provider.MediaStore;


import android.widget.TextView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerProfile extends AppCompatActivity implements CustomerDB.CustomerDataLoadedListener{

    CircleImageView profile_photo;
    Button edit_profile_btn;
    Uri profile_photo_uri;
    String customerID;

    TextView username;
    TextView email;

    TextView contact;
    TextView dob;
    TextView cnic;
    TextView age;

    FirebaseAuth mAuth;
    private final int GALLERY_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if(currentUser!=null) {
            customerID=currentUser.getUid();
        }
        else {
            customerID=null;
        }

        username = (TextView) findViewById(R.id.profile_username);
        email = (TextView) findViewById(R.id.profile_email);
        contact = (TextView) findViewById(R.id.contact_no);
        dob = (TextView) findViewById(R.id.dob);
        cnic = (TextView) findViewById(R.id.cnic);
        age = (TextView) findViewById(R.id.age);
        profile_photo=(CircleImageView) findViewById(R.id.profile_photo);
        edit_profile_btn = (Button) findViewById(R.id.edit_profile_btn);



        //load customer data from database
        Customer obj=new Customer();
        obj.setCustomerID(customerID);
        obj.LoadCustomer(this);

        edit_profile_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent edit_profile_intent = new Intent(CustomerProfile.this,EditCustomerProfile.class);
                edit_profile_intent.putExtra("contact",contact.getText().toString());
                edit_profile_intent.putExtra("dob",dob.getText().toString());
                edit_profile_intent.putExtra("cnic",cnic.getText().toString());
                edit_profile_intent.putExtra("age",age.getText().toString());
                edit_profile_intent.putExtra("name",username.getText().toString());
                startActivity(edit_profile_intent);
                finish();
            }
        });

        profile_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent iGallery =new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });

    }

    @Override
    public void onCustomerDataLoaded(Customer customer) {

        String profileImage = customer.getProfile_photo();
        String phone_no=customer.getPhoneNumber();
        String date_of_birth=customer.getDOB();
        String CNIC=customer.getCNIC();
        String AGE=customer.getAge();
        String UserName=customer.getName();
        String UserEmail=customer.getEmail();


        if (UserName.equals("Nil"))
        {
            username.setText("Username");
        }
        else{
            username.setText(UserName);
        }

        if(UserEmail!=null) {
            email.setText(UserEmail);
        }
        if(profileImage!=null)
        {
            Uri profileImageUri=extractProfileImageUri(profileImage);
            if(profileImageUri!=null)
            {
                profile_photo.setImageURI(profileImageUri);
            }
        }
        if(phone_no!=null) {
            contact.setText(phone_no);
        }
        if(date_of_birth!=null) {
            dob.setText(date_of_birth);
        }
        if(CNIC!=null) {
            cnic.setText(CNIC);
        }
        if(AGE!=null) {
            age.setText(AGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQ_CODE)
        {
            if(resultCode==RESULT_OK && data.getData()!=null)
            {
                profile_photo_uri=data.getData();
                profile_photo.setImageURI(profile_photo_uri);

                //update photo in database
                String profile_photo = extractProfileImageString(profile_photo_uri);
                if(profile_photo!=null && customerID!=null) {
                    Customer customer =new Customer();
                    customer.setCustomerID(customerID);
                    customer.setProfilePhoto(profile_photo);
                    customer.UpdateProfilePhotoToDatabase();
                }
            }
            if(resultCode == RESULT_CANCELED)
            {

            }
        }
    }
    public String extractProfileImageString(Uri profile_photo)
    {
        Uri uri=profile_photo;
        if(uri!=null) {
            Bitmap img = null;
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] imageBytes = stream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return null;
    }

    public Uri extractProfileImageUri(String imageString)
    {
        if(imageString!=null){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Uri photoUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null));
        return photoUri;
        }
        return null;
    }
}
