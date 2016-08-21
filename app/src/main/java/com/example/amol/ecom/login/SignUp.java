package com.example.amol.ecom.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.amol.ecom.AppController;
import com.example.amol.ecom.R;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    public EditText editusername, editmobilenumber, editpassword, editpassconfirm, editemaail, editAddress;
    public Button btnSignUp;
    public TextView textSignIn;

    private String KEY_USERNAME = "UserName";
    private String KEY_USERNUMBER = "UserNumber";
    private String KEY_USEREMAIL = "UserEmail";
    private String KEY_ADDRESS = "UserAddress";
    private String KEY_USERPASS = "UserPassword";
    private String KEY_USERCONFIRMPASS = "UserConfirmPassword";
    private String SignUp_Url = "http://10.0.2.2/xampp/ecom/Regi.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editusername = (EditText) findViewById(R.id.edit_signupname);
        editmobilenumber = (EditText) findViewById(R.id.editsignupnumber);
        editpassword = (EditText) findViewById(R.id.editsignuppass);
        editpassconfirm = (EditText) findViewById(R.id.editsignuppassconfirm);
        editemaail = (EditText) findViewById(R.id.editsignupemail);
        editAddress = (EditText) findViewById(R.id.editsignupAdress);
        btnSignUp = (Button) findViewById(R.id.button_SignUp);
        textSignIn = (TextView) findViewById(R.id.textview_SignInexisting);
        btnSignUp.setOnClickListener(this);
        textSignIn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.button_SignUp:
                RegisterUser();
                break;

            case R.id.textview_SignInexisting:
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
                break;
        }

    }

    private void RegisterUser() {


        final String UserName = editusername.getText().toString();
        final String UserNumber = editmobilenumber.getText().toString();
        final String UserEmail = editemaail.getText().toString();
        final String UserAddress = editAddress.getText().toString();
        final String UserPassword = editpassword.getText().toString();
        final String UserConfirmPassword = editpassconfirm.getText().toString();

        final ProgressDialog progressDialog = ProgressDialog.show(SignUp.this, "SigningUp", "Please Wait...", false, false);

        StringRequest request = new StringRequest(Request.Method.POST, SignUp_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                System.out.println("Volley params on response" + response);
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Volley params on errorResponse" + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put(KEY_USERNAME, UserName);
                System.out.println("Volley params" + UserName);

                map.put(KEY_USERNUMBER, UserNumber);
                System.out.println("Volley params" + UserNumber);

                map.put(KEY_USEREMAIL, UserEmail);
                System.out.println("Volley params" + UserEmail);

                map.put(KEY_ADDRESS, UserAddress);
                System.out.println("Volley params" + UserAddress);

                map.put(KEY_USERPASS, UserPassword);
                System.out.println("Volley params" + UserPassword);

                map.put(KEY_USERCONFIRMPASS, UserConfirmPassword);
                System.out.println("Volley login response" + UserConfirmPassword);

                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }
}
