package com.barbeaudev.mapsdemo;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String> mDir = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
        initIcons();

        int NUM_MARKERS = 200;
        float baseLat = 28.0580f;
        float baseLong = -82.4168f;
        LatLng l;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i = 0; i < NUM_MARKERS; i++) {
            // Randomly position the markers
            float latOffset = Util.randInt(2, 100) * 0.00001f;
            float longOffset = Util.randInt(2, 100) * 0.00001f;

            // Randomly get direction
            int index = Util.randInt(0, 8);

            // Add a marker
            l = new LatLng(baseLat + latOffset, baseLong + longOffset);
            mMap.addMarker(new MarkerOptions()
                            .position(l)
                            .icon(MarkerUtil.getBitmapDescriptorForBusStopDirection(mDir.get(index)))
                            .flat(true)
                            .anchor(MarkerUtil.getXPercentOffsetForDirection(mDir.get(index)),
                                    MarkerUtil.getYPercentOffsetForDirection(mDir.get(index)))

            );
            builder.include(l);
        }

        // Why doesn't camera movement to bounds work here??
        // See http://stackoverflow.com/questions/30935649/when-can-you-call-googlemap-movecamera-after-onmapready-from-onmapreadycallback
        // We'll just put it in a handler for now...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            }
        }, 500);
    }

    private void initMap() {
        UiSettings uiSettings = mMap.getUiSettings();
        // Show the location on the map
        mMap.setMyLocationEnabled(true);
        // Hide MyLocation button on map, since we have our own button
        uiSettings.setMyLocationButtonEnabled(false);
        // Hide Zoom controls
        uiSettings.setZoomControlsEnabled(false);
    }

    private void initIcons() {
        // Load directions so they can be randomized
        mDir.add(MarkerUtil.NORTH);
        mDir.add(MarkerUtil.NORTH_WEST);
        mDir.add(MarkerUtil.WEST);
        mDir.add(MarkerUtil.SOUTH_WEST);
        mDir.add(MarkerUtil.SOUTH);
        mDir.add(MarkerUtil.SOUTH_EAST);
        mDir.add(MarkerUtil.EAST);
        mDir.add(MarkerUtil.NORTH_EAST);
        mDir.add(MarkerUtil.NO_DIRECTION);

        // Init icons
        MarkerUtil.loadIcons(this);
    }
}
