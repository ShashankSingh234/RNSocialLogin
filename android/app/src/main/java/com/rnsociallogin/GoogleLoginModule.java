package com.rnsociallogin;
import static com.rnsociallogin.Constants.MODULE_NAME;
import static com.rnsociallogin.Constants.PLAY_SERVICES_NOT_AVAILABLE;
import static com.rnsociallogin.Constants.RC_SIGN_IN;
import static com.rnsociallogin.Constants.REQUEST_CODE_RECOVER_AUTH;
import static com.rnsociallogin.Utils.createScopesArray;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

public class GoogleLoginModule extends ReactContextBaseJavaModule {

    private  GoogleSignInClient mGoogleSignInClient;
    private Promise promise;
    private static final String SHOULD_RECOVER = "SHOULD_RECOVER";
    private PendingAuthRecovery pendingAuthRecovery;

    GoogleLoginModule(ReactApplicationContext context) {
        super(context);
        context.addActivityEventListener(new RNGoogleSigninActivityEventListener());
    }

    @Override
    public String getName() {
        return "GoogleLoginModule";
    }

    @ReactMethod
    public void performGoogleLogin(ReadableMap config, Promise promise) {
        try {
            this.promise = promise;
            Log.d("SocialLogin", "Perform Google login called " + (new Date()).toGMTString());

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            getCurrentActivity().startActivityForResult(signInIntent, RC_SIGN_IN);

        } catch(Exception e) {
            promise.reject("Create Event Error", e);
        }
    }

    @ReactMethod
    public void configure(ReadableMap config){
            final ReadableArray scopes = config.hasKey("scopes") ? config.getArray("scopes") : Arguments.createArray();
            final String webClientId = config.hasKey("webClientId") ? config.getString("webClientId") : null;
            final boolean offlineAccess = config.hasKey("offlineAccess") && config.getBoolean("offlineAccess");
            final boolean forceCodeForRefreshToken = config.hasKey("forceCodeForRefreshToken") && config.getBoolean("forceCodeForRefreshToken");
            final String accountName = config.hasKey("accountName") ? config.getString("accountName") : null;
            final String hostedDomain = config.hasKey("hostedDomain") ? config.getString("hostedDomain") : null;
//             Configure sign-in to request the user's ID, email address, and basic
//             profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(Scopes.EMAIL) ,createScopesArray(scopes));
            if (webClientId != null && !webClientId.isEmpty()) {
                gsoBuilder.requestIdToken(webClientId);
                if (offlineAccess) {
                    gsoBuilder.requestServerAuthCode(webClientId, forceCodeForRefreshToken);
                }
            }

            if (accountName != null && !accountName.isEmpty()) {
                gsoBuilder.setAccountName(accountName);
            }
            if (hostedDomain != null && !hostedDomain.isEmpty()) {
                gsoBuilder.setHostedDomain(hostedDomain);
            }
//
//            GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(getReactApplicationContext(), gsoBuilder.build());

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getReactApplicationContext());

    }

    public class RNGoogleSigninActivityEventListener extends BaseActivityEventListener {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInTaskResult(task);
            } else if (requestCode == REQUEST_CODE_RECOVER_AUTH) {
                if (resultCode == Activity.RESULT_OK) {
                    rerunFailedAuthTokenTask();
                } else {
                    promise.reject(MODULE_NAME, "Failed authentication recovery attempt, probably user-rejected.");
                }
            }
        }
    }

    private void handleSignInTaskResult(Task<GoogleSignInAccount> result) {
        Log.i("SocialLogin", "Handle Sigin Called");
        try {
            Log.i("SocialLogin", result.toString());
            GoogleSignInAccount account = result.getResult(ApiException.class);
            Log.i("SocialLogin", "On 129");
            if (account == null) {
                Log.i("SocialLogin", "Account null");
                promise.reject(MODULE_NAME, "GoogleSignInAccount instance was null");
            } else {
                Log.i("SocialLogin", "Successfull");
                WritableMap userParams = getUserProperties(account);
                promise.resolve(userParams);
            }
        } catch (ApiException e) {
//            Log.e("SocialLogin", e.getLocalizedMessage(), e);
            Log.e("SocialLogin", e.getMessage(), e.getCause());
            int code = e.getStatusCode();
            String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
            promise.reject(String.valueOf(code), errorDescription);
        }
    }

    static WritableMap getUserProperties(@NonNull GoogleSignInAccount acct) {
        Uri photoUrl = acct.getPhotoUrl();

        WritableMap user = Arguments.createMap();
        user.putString("id", acct.getId());
        user.putString("name", acct.getDisplayName());
        user.putString("givenName", acct.getGivenName());
        user.putString("familyName", acct.getFamilyName());
        user.putString("email", acct.getEmail());
        user.putString("photo", photoUrl != null ? photoUrl.toString() : null);

        WritableMap params = Arguments.createMap();
        params.putMap("user", user);
        params.putString("idToken", acct.getIdToken());
        params.putString("serverAuthCode", acct.getServerAuthCode());

        WritableArray scopes = Arguments.createArray();
        for (Scope scope : acct.getGrantedScopes()) {
            String scopeString = scope.toString();
            if (scopeString.startsWith("http")) {
                scopes.pushString(scopeString);
            }
        }
        params.putArray("scopes", scopes);
        return params;
    }

    private void rerunFailedAuthTokenTask() {
        WritableMap userProperties = pendingAuthRecovery.getUserProperties();
        if (userProperties != null) {
            //new AccessTokenRetrievalTask(this).execute(userProperties, null);
        } else {
            // this is unlikely to happen, since we set the pendingRecovery in AccessTokenRetrievalTask
            promise.reject(MODULE_NAME, "rerunFailedAuthTokenTask: recovery failed");
        }
    }

}
