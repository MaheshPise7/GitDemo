package com.example.amol.ecom;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.amol.ecom.login.Login;
import com.example.amol.ecom.login.SessionManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements View.OnClickListener {

    private static final String KEY_RESPONSE_NUMBER = "Number";
    private static final String LOG_TAG = "";
    public EditText editname, editaddress, editnumber, editemail;
    private static final String TAG_PROFILE = "Profile_Details";
    private static final String TAG_ID = "user_id";
    private static final String TAG_NAME = "user_name";
    private static final String TAG_NUMBER = "user_number";
    private static final String TAG_EMAIL = "user_mail";
    private static final String TAG_ADDRESS = "user_address";


    boolean isChecked = false;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        editname = (EditText) view.findViewById(R.id.edit_updatename);
        editemail = (EditText) view.findViewById(R.id.edit_updateemail);
        editaddress = (EditText) view.findViewById(R.id.edit_updateaddress);
        editnumber = (EditText) view.findViewById(R.id.edit_updatenumber);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SessionManager.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String number = sharedPreferences.getString(SessionManager.NUMBER_SHARED_PREF, "Not Available");
        System.out.println("in profile Value of shared preference" + number);
        SessionManager.MobileNumber = number;
        System.out.println("in profile Value of shared preference" + SessionManager.MobileNumber);
        editemail.setText(SessionManager.personEmail);
        editname.setText(SessionManager.personName);

        ReadProfile();

        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toggle_button).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_button:
                if (isChecked) {
                    item.setTitle("Edit");
                    editname.setEnabled(false);
                    editname.setClickable(true);
                    editname.setFocusableInTouchMode(false);
                    editemail.setEnabled(false);
                    editemail.setClickable(true);
                    editemail.setFocusableInTouchMode(false);
                    Toast.makeText(getActivity(), "Clicked on edit", Toast.LENGTH_SHORT).show();
                    isChecked = false;
                } else {
                    item.setTitle("Save");
                    Toast.makeText(getActivity(), "Clicked on save", Toast.LENGTH_SHORT).show();
                    editname.setEnabled(true);
                    editname.setClickable(false);
                    editname.setFocusableInTouchMode(true);
                    editemail.setEnabled(true);
                    editemail.setFocusableInTouchMode(true);
                    editemail.setClickable(false);
                    UpdateProfile();
                    isChecked = true;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // case R.id.logout_btn:
            // logout();
            //   SignOut();
            //  break;
        }

    }

    private void SignOut() {
        Auth.GoogleSignInApi.signOut(SessionManager.googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                // updateUI(false);

            }
        });
    }


    public void updateUI(boolean signedIn) {
        if (signedIn) {
            //getView().findViewById(R.id.btn_signin).setVisibility(View.GONE);
            // getView().findViewById(R.id.logout_btn).setVisibility(View.VISIBLE);
        } else {
            //getView().findViewById(R.id.btn_signin).setVisibility(View.VISIBLE);
            // getView().findViewById(R.id.logout_btn).setVisibility(View.GONE);
        }
    }

    public void ReadProfile() {

        String Read_url = "http://10.0.2.2/xampp/ecom/Read_user_profile.php";

        StringRequest request = new StringRequest(Request.Method.POST, Read_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("in profile Volley on response" + response.toString());

                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    int success = object.getInt("success");
                    if (success == 1) {

                        ProfileEntity cust_profile = new ProfileEntity();
                        SQLiteDatabase database = new SQLiteDatabase(getActivity());

                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("in profile Value after success response");
                        JSONArray array = object.getJSONArray(TAG_PROFILE);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object1 = array.getJSONObject(i);

                            String id = object1.getString(TAG_ID);
                            cust_profile.setCustId(Integer.parseInt(id));

                            String username = object1.getString(TAG_NAME);
                            editname.setText(username);
                            cust_profile.setCustName(username);
                            System.out.println("in profile after response of json" + username);

                            String usernumber = object1.getString(TAG_NUMBER);
                            editnumber.setText(usernumber);
                            cust_profile.setCustContact(usernumber);
                            System.out.println("in profile after response of json" + usernumber);

                            String usermail = object1.getString(TAG_EMAIL);
                            editemail.setText(usermail);
                            cust_profile.setCustEmail(usermail);
                            System.out.println("in profile after response of json" + usermail);

                            String useraddress = object1.getString(TAG_ADDRESS);
                            editaddress.setText(useraddress);
                            cust_profile.setCustAddress(useraddress);
                            System.out.println("in profile after response of json" + useraddress);

                            database.SaveCurrentCustomerProfile(cust_profile);
                            System.out.println("in profile after response of json" + useraddress);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("in profile Volley error response" + error.toString());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("Number", SessionManager.MobileNumber);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    public void UpdateProfile() {

        final String updatename = editname.getText().toString();
        final String updatenumber = editnumber.getText().toString();
        final String updateemail = editemail.getText().toString();
        final String updateaddress = editaddress.getText().toString();

        String update_profile = "http://10.0.2.2/xampp/ecom/Update_user_profile.php";

        final ProgressDialog loading = ProgressDialog.show(getActivity(), null, "Updating Profile", true, false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setContentView(R.layout.remove_border);


        StringRequest request = new StringRequest(Request.Method.POST, update_profile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                System.out.println("in profile on Response" + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    int success = object.getInt("success");
                    if (success == 1) {
                        Toast.makeText(getActivity(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "failed to update, please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                System.out.println("in profile on Response" + error.getMessage());

                if (error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Slow internet connection. Please, try again", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("UpdateName", updatename);
                map.put("UpdateNumber", updatenumber);
                map.put("UpdateEmail", updateemail);
                map.put("UpdateAddress", updateaddress);
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }
}



