package com.nlrd.socialevents;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private double latitude;
    private double longitude;

    Float zoom = new Float(15);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        GPSTracker gps = new GPSTracker(MainActivity.this);

        if (gps.canGetLocation)
        {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            LatLng myLocation = new LatLng(latitude, longitude);

            map.addMarker(new MarkerOptions().position(myLocation).title("My Position"));

            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
