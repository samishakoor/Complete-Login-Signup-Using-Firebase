package com.example.loginapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context context;
    int Private_mode = 0;

    private static final String PREF_NAME="sharedPref";
    private static final String IS_LOGIN="isLoggedIn";
    private static final String KEY_EMAIL="email";
    private static final String KEY_PASSWORD="password";

    public SessionManager(Context context)
    {
        this.context=context;
        pref=this.context.getSharedPreferences(PREF_NAME, Private_mode);
        editor=this.pref.edit();
    }

    public void createLoginSession() {
        editor.putBoolean(IS_LOGIN,true);
        editor.commit();

        Intent login_session_intent = new Intent(context,MainActivity.class);
        login_session_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ((Activity) context).startActivity(login_session_intent);
        ((Activity) context).finish();
    }

    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN,false);
    }

    public void checkLogin()
    {
        if(this.isLoggedIn())
        {
            Intent intent =new Intent(context,MainActivity.class);
            ((Activity) context).startActivity(intent);
            ((Activity) context).finish();
        }
    }


    public void logoutUser()
    {
        editor.clear();
        editor.commit();
        Intent intent =new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivity(intent);
        ((Activity) context).finish();
    }


}
