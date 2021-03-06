package com.nlrd.socialevents;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private final String TWIT_CONS_KEY = "UEyaYt84sYqOtAPh9JttjDw2L";
    private final String TWIT_CONS_SEC_KEY = "m6I2jehA4GpUSqbGJdRR4SH7VaH7uisLijCr85BDUKEWobbn5E";

    private double latitude;
    private double longitude;

    Float zoom = new Float(10);

    GoogleMap mMap;

    CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;

    JSONArray events;

    ProgressDialog progress;

    int range = 10;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        events = new JSONArray();
        progress = new ProgressDialog(this);

        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        profile = Profile.getCurrentProfile();

        //new SearchOnTwitter().execute("#afterwork facebook");
        //new SearchOnTwitter().execute("#soiree facebook");

        getEventsFacebook();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        GPSTracker gps = new GPSTracker(MainActivity.this);

        mMap = map;

        if (gps.canGetLocation) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            LatLng myLocation = new LatLng(latitude, longitude);

            map.addMarker(new MarkerOptions().position(myLocation).title("My Position"));

            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            map.moveCamera(CameraUpdateFactory.zoomTo(zoom));

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String fullDescription = marker.getTitle();

                    String[] desc = fullDescription.split("##");

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    alertDialogBuilder.setTitle(desc[0]);

                    String date = desc[2];

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date value = null;
                    try {
                        value = formatter.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                    dateFormatter.setTimeZone(TimeZone.getDefault());
                    String dt = dateFormatter.format(value);

                    alertDialogBuilder.setMessage("\n" + desc[1] + "\n\n"
                        + dt + "\n\n" + desc[3]
                    );

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventsFacebook();

        events = new JSONArray();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class SearchOnTwitter extends AsyncTask<String, Void, Integer> {
        ArrayList<Tweet> tweets;
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.searching));
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setUseSSL(true);
                builder.setApplicationOnlyAuthEnabled(true);
                builder.setOAuthConsumerKey(TWIT_CONS_KEY);
                builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);

                OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

                builder = new ConfigurationBuilder();
                builder.setUseSSL(true);
                builder.setApplicationOnlyAuthEnabled(true);
                builder.setOAuthConsumerKey(TWIT_CONS_KEY);
                builder.setOAuthConsumerSecret(TWIT_CONS_SEC_KEY);
                builder.setOAuth2TokenType(token.getTokenType());
                builder.setOAuth2AccessToken(token.getAccessToken());

                Twitter twitter = new TwitterFactory(builder.build()).getInstance();

                Query query = new Query(params[0]);
                // YOu can set the count of maximum records here
                query.setCount(50);
                QueryResult result;
                result = twitter.search(query);
                List<twitter4j.Status> tweets = result.getTweets();
                StringBuilder str = new StringBuilder();
                if (tweets != null) {
                    this.tweets = new ArrayList<Tweet>();
                    String a = "";
                    for (twitter4j.Status tweet : tweets) {
                        str.append("@" + tweet.getUser().getScreenName() + " - " + tweet.getText() + "\n");
                        str.append(tweet.getText() + "\n");

                        ArrayList url = getLinks(tweet.getText().toString());

                        if (url.size() != 0) {
                            a = url.get(0).toString();

                            try {
                                URL url2 = new URL(a);
                                HttpURLConnection urlConnection = null;

                                urlConnection = (HttpURLConnection) url2.openConnection();
                                InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                                String titre = urlConnection.toString();
                                String facebook = "facebook";
                                String eventString = "events";

                                if (titre.indexOf(facebook) > 0 && titre.indexOf(eventString) > 0) {
                                    String[] groupeFacebook = titre.split("/");

                                    getEventById(groupeFacebook[4]);
                                }

                                urlConnection.disconnect();
                            } catch (IOException e) {
                                //	e.printStackTrace();
                            }
                        }

                        this.tweets.add(new Tweet("@" + tweet.getUser().getScreenName(), tweet.getText()));
                    }

                    return SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            return FAILURE;
        }

        private ArrayList getLinks(String text) {
            ArrayList links = new ArrayList();

            String regex = "\\(?\\b(https://|[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(text);
            while (m.find()) {
                String urlStr = m.group();
                if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                    urlStr = urlStr.substring(1, urlStr.length() - 1);
                }
                links.add(urlStr);
            }
            return links;
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == SUCCESS) {
                //list.setAdapter(new TweetAdapter(MainActivity.this, tweets));
            } else {
                //Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToConfiguration(View view) {
        Intent intent = new Intent(this, ConfigurationActivity.class);
        intent.putExtra("range", range);
        startActivityForResult(intent, 1);
    }


    public void getEventsFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            // To dismiss the dialog

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/search",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {

                            progress.dismiss();
                            JSONObject listResponse = response.getJSONObject();
                            try {
                                JSONArray eventResponse = (JSONArray) listResponse.get("data");

                                for (int i = 0; i < eventResponse.length(); i++) {
                                    try {
                                        JSONObject event = eventResponse.getJSONObject(i);

                                        events.put(event);

                                        String lat = event.getJSONObject("place").getJSONObject("location").get("latitude").toString();
                                        String lon = event.getJSONObject("place").getJSONObject("location").get("longitude").toString();

                                        String name = event.get("name").toString();
                                        String description = event.get("description").toString();
                                        String startTime = event.get("start_time").toString();
                                        String address = event.getJSONObject("place").getJSONObject("location").get("street").toString() + ", "
                                                + event.getJSONObject("place").getJSONObject("location").get("zip").toString() + " "
                                                + event.getJSONObject("place").getJSONObject("location").get("city").toString() + " "
                                                + event.getJSONObject("place").getJSONObject("location").get("country").toString();

                                        String fullDescription = name + "##" + description + "##" + startTime + "##" + address;

                                        LatLng location = new LatLng(Float.parseFloat(lat), Float.parseFloat(lon));

                                        mMap.addMarker(new MarkerOptions().position(location).title(fullDescription).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("q", "\"AfterWork\"");
            parameters.putString("type", "event");
            parameters.putString("center", latitude + "," + longitude);
            parameters.putString("distance", (range * 10) + "");
            parameters.putString("limit", "500");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    public void getEventById(final String id) {
        if (accessToken == null) {
            new GraphRequest(
                    accessToken,
                    "oauth/access_token?client_id=1186047631431496&client_secret=6dc0f55ed3e8220a20fda56f4ac3e26f&grant_type=client_credentials",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {

                            JSONObject json = response.getJSONObject();
                            try {
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

                                new GraphRequest(
                                        AppAT,
                                        "/" + id,
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {

                                                JSONObject event = response.getJSONObject();

                                                try {
                                                    String lat = event.getJSONObject("place").getJSONObject("location").get("latitude").toString();
                                                    String lon = event.getJSONObject("place").getJSONObject("location").get("longitude").toString();

                                                    String name = event.get("name").toString();
                                                    String description = event.get("description").toString();
                                                    String startTime = event.get("start_time").toString();
                                                    String address = event.getJSONObject("place").getJSONObject("location").get("street").toString() + ", "
                                                            + event.getJSONObject("place").getJSONObject("location").get("zip").toString() + " "
                                                            + event.getJSONObject("place").getJSONObject("location").get("city").toString() + " "
                                                            + event.getJSONObject("place").getJSONObject("location").get("country").toString();

                                                    String fullDescription = name + "##" + description + "##" + startTime + "##" + address;

                                                    LatLng location = new LatLng(Float.parseFloat(lat), Float.parseFloat(lon));

                                                    mMap.addMarker(new MarkerOptions().position(location).title(fullDescription));

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                events.put(event);
                                            }
                                        }
                                ).executeAsync();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }
            ).executeAsync();
        } else {
            new GraphRequest(
                    accessToken,
                    "/" + id,
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            JSONObject event = response.getJSONObject();

                            try {
                                String lat = event.getJSONObject("place").getJSONObject("location").get("latitude").toString();
                                String lon = event.getJSONObject("place").getJSONObject("location").get("longitude").toString();

                                String name = event.get("name").toString();
                                String description = event.get("description").toString();
                                String startTime = event.get("start_time").toString();
                                String address = event.getJSONObject("place").getJSONObject("location").get("street").toString() + ", "
                                        + event.getJSONObject("place").getJSONObject("location").get("zip").toString() + " "
                                        + event.getJSONObject("place").getJSONObject("location").get("city").toString() + " "
                                        + event.getJSONObject("place").getJSONObject("location").get("country").toString();

                                String fullDescription = name + "##" + description + "##" + startTime + "##" + address;

                                LatLng location = new LatLng(Float.parseFloat(lat), Float.parseFloat(lon));

                                mMap.addMarker(new MarkerOptions().position(location).title(fullDescription));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            events.put(event);
                        }
                    }
            ).executeAsync();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                range = (int) data.getIntExtra("range", 10);
                events = new JSONArray();

                getEventsFacebook();
            }
        }
    }
}


