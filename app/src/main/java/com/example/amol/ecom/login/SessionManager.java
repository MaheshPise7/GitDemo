package com.example.amol.ecom.login;

import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Amol on 01-Mar-16.
 */
public class SessionManager {

    public static final String Loggin_Url = "http://10.0.2.2/xampp/ecom/Login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";
    public static final String NUMBER_SHARED_PREF = "number";

    public static String personName, personEmail, personPicture;
    public static Uri personPhoto;
    public static CircleImageView circleImageView;
    public static int ProccedFlag = 0;
    public static int count = 0;

    public static int currentSelectedItemPosition = 0;
    public static double cartNotificationTotal = 0;
    public static double GrandTotalOfAllItems = 0;

    public static Integer LoginNormal = 0, LoginWithGoogle = 0, LoginWithFacebook = 0;

    public static GoogleApiClient googleApiClient;



    public static String MobileNumber = "";
}



