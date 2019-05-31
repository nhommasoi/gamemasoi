package com.example.fbtest;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;


public class MainActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private LinearLayout Prof_section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name,Email;
    private ImageView Prof_pic;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private LoginButton loginButton;


    public static final int REC_CODE = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Prof_section=(LinearLayout)findViewById(R.id.prof_section);
        SignOut=(Button)findViewById(R.id.btn_logout);
        SignIn=(SignInButton) findViewById(R.id.btn_login);
        Name=(TextView)findViewById(R.id.name);
        Email=(TextView)findViewById(R.id.email);
        Prof_pic=(ImageView)findViewById(R.id.prof_pic);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        Prof_section.setVisibility(View.GONE);
        //fb login
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // configure login fb
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        AccessToken tok;
                        tok = AccessToken.getCurrentAccessToken();

                        System.out.println(tok.getUserId());
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                        } else {

                                            String id = me.optString("id");
                                            String name =me.optString("name");

                                            //finish();
                                        }
                                    }
                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });
        //google
        GoogleSignInOptions signInOptions =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleSignInClient=GoogleSignIn.getClient(this, signInOptions);
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(
                Auth.GOOGLE_SIGN_IN_API,signInOptions
        ).build();
    }
    private void signIn() {
        Intent intent =Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REC_CODE);
    }
    private void updateUI(boolean isLogin){
    if(isLogin){
        Prof_section.setVisibility(View.VISIBLE);
        SignIn.setVisibility(View.GONE);
    }else
    {
        Prof_section.setVisibility(View.GONE);
        SignIn.setVisibility(View.VISIBLE);
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REC_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }


    }
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            String name =account.getDisplayName();
            String email=account.getEmail();
            String img_url=account.getPhotoUrl().toString();
            Name.setText(name);
            Email.setText(email);
            Glide.with(this).load(img_url).into(Prof_pic);
            updateUI(true);
        }else{
            updateUI(false);
        }

    }

    @Override
    public void onClick(View v) {

    switch (v.getId()){
    case R.id.btn_login:
        signIn();
        break;
}
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}





























