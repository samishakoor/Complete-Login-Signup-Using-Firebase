package com.example.loginapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Map;
import androidx.annotation.NonNull;
import java.util.HashMap;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
public class CustomerDB {

    private final String CUSTOMERS_REFERENCE="customers";
    private final String CUSTOMER_PROFILE_PHOTO="profile_photo";
    private final String CUSTOMER_NAME="name";
    private final String CUSTOMER_CONTACT_NO="phoneNumber";
    private final String CUSTOMER_CNIC="cnic";
    private final String CUSTOMER_AGE="age";
    private final String CUSTOMER_DOB="dob";

    public CustomerDB() {
    }

    public void AddCustomer(Customer obj) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference customersRef = db.getReference(CUSTOMERS_REFERENCE);
        String customerID=obj.getCustomerID();
        Customer customer=new Customer(customerID, obj.getName(),obj.getPhoneNumber(),obj.getDOB(),obj.getEmail(),obj.getCNIC(),obj.getAge(),obj.getProfile_photo());
        customersRef.child(customerID).setValue(customer);
    }

    public void UpdateCustomerProfilePhoto(String cID,String photo)
    {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().
                child(CUSTOMERS_REFERENCE)
                .child(cID);
        Map<String, Object> updates = new HashMap<>();
        updates.put(CUSTOMER_PROFILE_PHOTO,photo);
        customerRef.updateChildren(updates);
    }


    public void UpdateCustomer(Customer obj)
    {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().
                child(CUSTOMERS_REFERENCE)
                .child(obj.getCustomerID());
        Map<String, Object> updates = new HashMap<>();
        updates.put(CUSTOMER_NAME,obj.getName());
        updates.put(CUSTOMER_CONTACT_NO,obj.getPhoneNumber());
        updates.put(CUSTOMER_CNIC,obj.getCNIC());
        updates.put(CUSTOMER_AGE,obj.getAge());
        updates.put(CUSTOMER_DOB,obj.getDOB());
        customerRef.updateChildren(updates);
    }


    public interface CustomerDataLoadedListener
    {
        void onCustomerDataLoaded(Customer customer);
    }

    public interface CustomerDataCheckListener
    {
        void oncheckIfCustomerAlreadyStored(String userID,boolean checkCustomer);
    }


    public void LoadCustomerData(String customerID,CustomerDataLoadedListener listener) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference customerRef = database.getReference(CUSTOMERS_REFERENCE+"/"+customerID);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                listener.onCustomerDataLoaded(customer);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void checkIfCustomerInfoStored(String userID,CustomerDataCheckListener listener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(CUSTOMERS_REFERENCE).child(userID);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.oncheckIfCustomerAlreadyStored(userID,true);
                } else {
                    listener.oncheckIfCustomerAlreadyStored(userID,false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }







}
