package com.example.loginapp;

public class Customer {
    private String CustomerID;
    private String Name;
    private String PhoneNumber;
    private String DOB;
    private String Email;
    private String CNIC;
    private String profile_photo;
    private String Age;

    private CustomerDB db=new CustomerDB();

    public Customer()
    {

    }


    public Customer(String customerID, String name, String phoneNumber, String dob, String email,String cnic , String age ,String img) {
        CustomerID=customerID;
        Name = name;
        PhoneNumber = phoneNumber;
        DOB = dob;
        Email = email;
        CNIC=cnic;
        Age = age;
        profile_photo=img;
    }

    public void AddCustomerToDatabase()
    {
        if (db!=null) {
            db.AddCustomer(new Customer(getCustomerID(),getName(),getPhoneNumber(),getDOB(),getEmail(),getCNIC(),getAge(),getProfile_photo()));
        }
    }

    public void LoadCustomer(CustomerDB.CustomerDataLoadedListener listener)
    {
        if (db!=null) {
            db.LoadCustomerData(getCustomerID(),listener);
        }
    }

    public void UpdateProfilePhotoToDatabase()
    {
        if (db!=null)
        {
            db.UpdateCustomerProfilePhoto(getCustomerID(),getProfile_photo());
        }
    }

    public void UpdateCustomerToDatabase()
    {
        if (db!=null)
        {
            db.UpdateCustomer(new Customer(getCustomerID(),getName(),getPhoneNumber(),getDOB(),getEmail(),getCNIC(),getAge(),getProfile_photo()));
        }
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCustomerID() {
        return CustomerID;
    }



    public void setAge(String age) {
        Age = age;
    }

    public String getAge() {
        return Age;
    }


    public void setCNIC(String cnic) {
        CNIC = cnic;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setProfilePhoto(String img) {
        profile_photo = img;
    }

    public String getProfile_photo() {
        return profile_photo;
    }


    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

}
