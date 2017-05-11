/*
 * CS 193A, Winter 2017, Marty Stepp
 * This activity is a small demonstration of adding Google Sign-in to an Android app.
 * It is tricky to set up, but this gives us the powerful feature of allowing logins
 * and user information in our app.
 *
 * We also demonstrate text-to-speech and speech-to-text here, though it is not really
 * related to signing in a user.
 */

package edu.stanford.cs193a.loginpleasework;

import android.content.*;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.*;

import stanford.androidlib.*;

public class LoginPleaseActivity extends SimpleActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final int REQ_CODE_GOOGLE_SIGNIN = 32767 / 2;

    private GoogleApiClient google;
    private TextToSpeech tts;
    private boolean isTTSinitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_please);
        isTTSinitialized = false;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isTTSinitialized = true;
            }
        });

        SignInButton button = (SignInButton) findViewById(R.id.signin_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClick(v);
            }
        });

        // request the user's ID, email address, and basic profile
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // build API client with access to Sign-In API and options above
        google = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addConnectionCallbacks(this)
                .build();

    }

    /*
     * This method is called when the Sign in with Google button is clicked.
     * It launches the Google Sign-in activity and waits for a result.
     */
    public void signInClick(View view) {
        Toast.makeText(this, "Sign in was clicked!", Toast.LENGTH_SHORT).show();

        // speak some words
        if (isTTSinitialized) {
            tts.speak("Hey idiot, you need to log in now. Booyah!",
                    TextToSpeech.QUEUE_FLUSH, null);
        }

        // connect to Google server to log in
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(google);
        startActivityForResult(intent, REQ_CODE_GOOGLE_SIGNIN);
    }

    /*
     * This method is called when the user has finished speaking from a speech-to-text action.
     * We just display the spoken text on the screen in a text view.
     */
    @Override
    public void onSpeechToTextReady(String spokenText) {
        $TV(R.id.results).setText("User's favorite color is: " + spokenText);
    }

    /*
     * This method is called when Google Sign-in comes back to my activity.
     * We grab the sign-in results and display the user's name and email address.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_GOOGLE_SIGNIN) {
            // google sign-in has returned
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                // yay; user logged in successfully
                GoogleSignInAccount acct = result.getSignInAccount();
                $TV(R.id.results).setText("You signed in as: " + acct.getDisplayName() + " "
                        + acct.getEmail());
            } else {
                $TV(R.id.results).setText("Login fail. :(");
            }
        }
    }

    /*
     * Called when the Speech to Text button is clicked.
     * Initiates a speech-to-text activity.
     */
    public void speechToTextClick(View view) {
        speechToText("Say your favorite color:");   // Stanford Android library method
    }

    /*
     * Called when the Text to Speech button is clicked.
     * Causes the app to speak aloud.
     */
    public void textToSpeechClick(View view) {
        if (isTTSinitialized) {
            tts.speak("Congratulations. You clicked a button, genius.",
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // this method is required for the GoogleApiClient.OnConnectionFailedListener above
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("onConnectionFailed");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnected(Bundle bundle) {
        log("onConnected");
    }

    // this method is required for the GoogleApiClient.ConnectionCallbacks above
    public void onConnectionSuspended(int i) {
        log("onConnectionSuspended");
    }
}
