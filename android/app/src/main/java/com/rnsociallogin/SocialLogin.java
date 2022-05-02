package com.rnsociallogin;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;

public class SocialLogin extends ReactContextBaseJavaModule {
    SocialLogin(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "SocialLogin";
    }

    @ReactMethod
    public void performGoogleLogin(String name, String location, Promise promise) {
        try {
            Integer eventId = 1;
            promise.resolve(eventId);
        } catch(Exception e) {
            promise.reject("Create Event Error", e);
        }
    }
}
