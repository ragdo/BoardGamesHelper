package com.example.shaga.boardgameshelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "fbLogin";
    private AccessToken accessToken;
    private String userID = null;
    FirebaseDatabase fd = FirebaseDatabase.getInstance();

    private void enableButton(boolean enabled, int buttonId)
    {
        Button button = (Button) findViewById(buttonId);
        button.setEnabled(enabled);
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                   AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                enableButton(false,R.id.templateButton);
                enableButton(false,R.id.searchTemplateButton);
                enableButton(false,R.id.sessionButton);
            }
        }
    };

    CallbackManager callbackManager = CallbackManager.Factory.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        // Initialize Firebase Auth
        enableButton(false,R.id.buttonEvents);
        enableButton(false,R.id.templateButton);
        enableButton(false,R.id.searchTemplateButton);
        enableButton(false,R.id.sessionButton);
        mAuth = FirebaseAuth.getInstance();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_friends"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        enableButton(false,R.id.templateButton);
                        enableButton(false,R.id.searchTemplateButton);
                        enableButton(false,R.id.sessionButton);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook:onError", exception);
                        enableButton(false,R.id.templateButton);
                        enableButton(false,R.id.searchTemplateButton);
                        enableButton(false,R.id.sessionButton);
                    }
                });
        accessTokenTracker.startTracking();
    }

    public void facebookButtonOnClick(View view)
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            accessToken = AccessToken.getCurrentAccessToken();
                            if(accessToken != null) {
                                if (!accessToken.isExpired()) {
                                    userID = accessToken.getUserId();
                                }
                            }

                            GraphRequest request1 = GraphRequest.newMeRequest(
                                    accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            Log.d("Graph", object.toString());
                                            try {
                                                String id = (String) object.get("id");
                                                String name = (String) object.get("name");
                                                DatabaseOperations.addPlayer(fd,id,name);
                                                enableButton(true,R.id.templateButton);
                                                enableButton(true,R.id.searchTemplateButton);
                                                enableButton(true,R.id.sessionButton);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters1 = new Bundle();
                            parameters1.putString("fields", "id,name");
                            request1.setParameters(parameters1);
                            request1.executeAsync();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            enableButton(false,R.id.templateButton);
                            enableButton(false,R.id.searchTemplateButton);
                            enableButton(false,R.id.sessionButton);
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        enableButton(false,R.id.templateButton);
        enableButton(false,R.id.searchTemplateButton);
        enableButton(false,R.id.sessionButton);
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            if (!accessToken.isExpired()) {
                enableButton(true,R.id.templateButton);
                enableButton(true,R.id.searchTemplateButton);
                enableButton(true,R.id.sessionButton);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void diceButtonOnClick(View view)
    {
        Intent intent = new Intent(this,DiceRollActivity.class);
        startActivity(intent);
    }

    public void eventsButtonOnClick(View view)
    {
        Intent intent = new Intent(this,EventListActivity.class);
        startActivity(intent);
    }

    public void templatesButtonOnClick(View view)
    {
        Intent intent = new Intent(this, TemplatesActivity.class);
        startActivity(intent);
    }

    public void searchTemplatesButtonOnClick(View view)
    {
        Intent intent = new Intent(this, SearchTemplatesActivity.class);
        startActivity(intent);
    }

    public void sessionsButtonOnClick(View view)
    {
        Intent intent = new Intent(this, SessionsActivity.class);
        startActivity(intent);
    }
}
