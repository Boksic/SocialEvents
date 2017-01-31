package com.nlrd.socialevents;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity
{

    CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;

    JSONArray events;

    ProgressDialog progress;

    int range = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        events = new JSONArray();
        progress = new ProgressDialog(this);

        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        profile = Profile.getCurrentProfile();

        getEventsFacebook();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventsFacebook();
    }

    public void goToConfiguration(View view) {
        Intent intent = new Intent(this, ConfigurationActivity.class);
        intent.putExtra("range",range);
        startActivityForResult(intent, 1);
    }


    public void getEventsFacebook(){
        if(accessToken != null){
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            // To dismiss the dialog

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/search",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            TextView nameView = (TextView)findViewById(R.id.nbEvenements);
                            progress.dismiss();
                            JSONObject listResponse = response.getJSONObject();
                            try {
                                events = (JSONArray) listResponse.get("data");
                                int nb = events.length();
                                nameView.setText("" + nb);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("q", "\"AfterWork\"");
            parameters.putString("type", "event");
            parameters.putString("center", "48.8599825,2.4066411999999673");
            parameters.putString("distance", (range*10) + "");
            parameters.putString("limit", "500");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    public void getEventById(final String id){
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        if(accessToken == null){
            new GraphRequest(
                    accessToken,
                    "oauth/access_token?client_id=1186047631431496&client_secret=6dc0f55ed3e8220a20fda56f4ac3e26f&grant_type=client_credentials",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            Log.v("response",response.toString());

                            JSONObject json =  response.getJSONObject();
                            try{
                                String at = (String) json.get("access_token");
                                Set<String> s = Collections.emptySet();
                                AccessToken AppAT = new AccessToken(at,
                                        "5",
                                        "5",
                                        s,
                                        null,
                                        AccessTokenSource.FACEBOOK_APPLICATION_NATIVE,
                                        new Date(),
                                        null);

                                Log.v("response accessToken", at);
                                new GraphRequest(
                                        AppAT,
                                        "/"+id,
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                                events.put(response.getJSONObject());
                                                TextView nameView = (TextView)findViewById(R.id.nbEvenements);
                                                int nb = events.length();
                                                nameView.setText("" + nb);
                                                progress.dismiss();

                                            }
                                        }
                                ).executeAsync();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }
            ).executeAsync();
        }
        else{
            new GraphRequest(
                    accessToken,
                    "/"+id,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            events.put(response.getJSONObject());
                            TextView nameView = (TextView)findViewById(R.id.nbEvenements);
                            int nb = events.length();
                            nameView.setText("" + nb);
                            progress.dismiss();
                        }
                    }
            ).executeAsync();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                range = (int) data.getIntExtra("range", 10);
                getEventsFacebook();
            }
        }
    }

}


