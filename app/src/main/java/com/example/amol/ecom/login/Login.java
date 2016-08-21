package com.example.amol.ecom.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.amol.ecom.AppController;
import com.example.amol.ecom.MainActivity;
import com.example.amol.ecom.R;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public SignInButton signInButton;
    public GoogleSignInOptions signInOptions;
    public GoogleApiClient googleApiClient;
    private int RC_SIGN_IN = 9001;

    public CallbackManager callbackManager;
    public AccessTokenTracker accessTokenTracker;
    public ProfileTracker profileTracker;

    private EditText editusernumber, editpassword;
    private Button signIn;
    private TextView signup;
    private boolean loggedIn = false;
    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean signedIn;
    public ProgressDialog pDialog;
    private ImageLoader imageLoader;
    public ProgressDialog progressDialog;

    //  private String KEY_EMAIL="email";
    // private String KEY_PASSWORD="password";
    //private String Loggin_Url="http://10.0.2.2:8088/xampp/ecom/Login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FacebookSdk.sdkInitialize(this.getApplicationContext());
        // callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        signIn = (Button) findViewById(R.id.btn_signin);
        signIn.setOnClickListener(this);
        editusernumber = (EditText) findViewById(R.id.edittext_phonenumber);
        editpassword = (EditText) findViewById(R.id.edittext_password);
        signup = (TextView) findViewById(R.id.sinuphere);
        signup.setOnClickListener(this);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        signInButton = (SignInButton) findViewById(R.id.button_googleSignin);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(signInOptions.getScopeArray());

        SessionManager.googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.sinuphere:
                Intent i = new Intent(Login.this, SignUp.class);
                startActivity(i);
                break;

            case R.id.btn_signin:
                SessionManager.LoginNormal = 1;
                UserLogin();

                break;

            case R.id.button_googleSignin:
                SessionManager.LoginWithGoogle = 1;
                SignIn();
                break;

            // case R.id.button_facebookSignIn:
            //   SessionManager.LoginWithFacebook = 1;
            //  break;

        }
    }

    /***********************************************************************************************
     * Handling Normal Login Activities*
     ***********************************************************************************************/

    private void UserLogin() {
        System.out.println("User Login......................");
        final ProgressDialog pDialog = ProgressDialog.show(Login.this, "Logging In", "Please Wait...", false, false);

        final String number = editusernumber.getText().toString();
        final String password = editpassword.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, SessionManager.Loggin_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                System.out.println("in login Volley params on response " + response + "from server");
                try {
                    JSONObject jobj = new JSONObject(response);

                    int success = jobj.getInt("success");

                    if (success == 1) {

                        SharedPreferences sharedPreferences = Login.this.getSharedPreferences(SessionManager.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SessionManager.LOGGEDIN_SHARED_PREF, true);
                        editor.putString(SessionManager.NUMBER_SHARED_PREF, number);
                        System.out.println("in login Value of shared preference" + SessionManager.NUMBER_SHARED_PREF);
                        editor.apply();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                System.out.println("Volley params on errorResponse" + error.getMessage());
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Slow internet connection, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put(SessionManager.KEY_NUMBER, number);
                System.out.println("Volley login response" + number);

                map.put(SessionManager.KEY_PASSWORD, password);
                System.out.println("Volley login response" + password);

                return map;
            }

        };

        AppController.getInstance().addToRequestQueue(request);

    }


    /***********************************************************************************************
     * Handling Override Activities *
     ***********************************************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionManager.LoginNormal == 1) {
            SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            loggedIn = sharedPreferences.getBoolean(SessionManager.LOGGEDIN_SHARED_PREF, false);
            if (loggedIn) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        }
        if (SessionManager.LoginWithGoogle == 1) {
            if (SessionManager.googleApiClient.isConnected()) {
                SessionManager.googleApiClient.connect();
            }
        }

        if (SessionManager.LoginWithFacebook == 1) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(SessionManager.googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handlesignInResult(result);
            //   }else {
            //    pDialog.show();
            //   opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
            //     @Override
            //    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
            //      pDialog.dismiss();
            //    handlesignInResult(googleSignInResult);
            // }
            //});
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (SessionManager.googleApiClient.isConnected()) {
            SessionManager.googleApiClient.disconnect();
        }
    }


    /*******************************************************************************************
     * Handling Login WIth Google Activities*
     ********************************************************************************************/


    public void SignIn() {
        progressDialog = ProgressDialog.show(this, "Logging in", "Please Wait..", false, false);
        Intent signInintent = Auth.GoogleSignInApi.getSignInIntent(SessionManager.googleApiClient);
        startActivityForResult(signInintent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handlesignInResult(result);
        }
    }

    private void handlesignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            progressDialog.dismiss();
            GoogleSignInAccount acct = result.getSignInAccount();
            SessionManager.personName = acct.getDisplayName();
            SessionManager.personEmail = acct.getEmail();
            String personId = acct.getId();
//            SessionManager.personPicture = acct.getPhotoUrl().toString();
            SessionManager.personPhoto = acct.getPhotoUrl();


            //updateUI(true);

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            // updateUI(false);
        }
    }

    public void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.btn_signin).setVisibility(View.GONE);
            // findViewById(R.id.logout_btn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_signin).setVisibility(View.VISIBLE);
            //  findViewById(R.id.logout_btn).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
        }

    }


    /*******************************************************************************************
     * Handling Login WIth Facebook Activities*
     ********************************************************************************************/


}