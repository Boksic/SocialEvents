package com.nlrd.socialevents;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.AccessToken;
import com.facebook.HttpMethod;

public class MainActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /*GraphRequestAsyncTask graphRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/search?&type=event&q=Afterwork",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        int toto = 5;
                    }
                }
        ).executeAsync();*/
    }

}
