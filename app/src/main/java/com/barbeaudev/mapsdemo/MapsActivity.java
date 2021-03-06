package com.barbeaudev.mapsdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.barbeaudev.mapsdemo.util.MarkerUtil;
import com.barbeaudev.mapsdemo.util.Util;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Attempt to replicate jumping markers issue on Maps API v2
 *
 * See http://stackoverflow.com/questions/32276570/jumping-markers-on-android-maps-api-v2
 */
public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private static final int NUM_MARKERS = 200;

    private Context mContext;
    private BaseMapFragment mMapFragment;
    private FloatingActionButton mFabMyLocation;
    private boolean mRefresh = true;
    private ArrayList<String> mDir = new ArrayList<>(9);
    private ArrayList<Marker> mMarkers = new ArrayList<>(NUM_MARKERS);
//    private Handler mRefreshHandler = new Handler();
//    private Runnable mRefreshRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Toast.makeText(mContext, "Refreshing!", Toast.LENGTH_SHORT).show();
//            addMarkers();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mMapFragment = BaseMapFragment.newInstance();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.main_fragment_container, mMapFragment).commit();

        mMapFragment.getMapAsync(this);
        mContext = this;

        initButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel refreshes
//        mRefresh = false;
//        mRefreshHandler.removeCallbacks(mRefreshRunnable);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        initMap();
        initIcons();
        addMarkers();
    }

    private void addMarkers() {
        // Clear any existing markers on map
        for (Marker m : mMarkers) {
            m.remove();
        }
        mMarkers.clear();

        float baseLat = 28.050f;
        float baseLong = -82.425f;
        LatLng l;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i = 0; i < NUM_MARKERS; i++) {
            // Randomly position the markers
            float latOffset = Util.randInt(2, 100) * 0.0005f;
            float longOffset = Util.randInt(2, 100) * 0.0005f;

            // Randomly get direction
            int index = Util.randInt(0, 8);

            // Add a marker
            l = new LatLng(baseLat + latOffset, baseLong + longOffset);
            Marker m = mMapFragment.getMap().addMarker(new MarkerOptions()
                            .position(l)
                            .icon(MarkerUtil
                                    .getBitmapDescriptorForBusStopDirection(mDir.get(index)))
                            .flat(true)
                            .anchor(MarkerUtil.getXPercentOffsetForDirection(mDir.get(index)),
                                    MarkerUtil.getYPercentOffsetForDirection(mDir.get(index)))

            );

            // Add marker to list
            mMarkers.add(m);

            // Include marker in bounds builder so we can zoom to bounds
            builder.include(l);
        }

        // Why doesn't camera movement to bounds work here??
        // See http://stackoverflow.com/questions/30935649/when-can-you-call-googlemap-movecamera-after-onmapready-from-onmapreadycallback
        // We'll just put it in a handler for now...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMapFragment != null) {
                    GoogleMap m = mMapFragment.getMap();
                    if (m != null) {
                       m.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }
                }
            }
        }, 500);

//        if (mRefresh) {
//            // Schedule a refresh of the markers in 60 seconds
//            mRefreshHandler.postDelayed(mRefreshRunnable, TimeUnit.SECONDS.toMillis(60));
//        }
    }

    private void initButton() {
        mFabMyLocation = (FloatingActionButton) findViewById(R.id.btnMyLocation);
        mFabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Open another Maps Activity on top of this one
                Intent myIntent = new Intent(mContext, MapsActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void initMap() {
        UiSettings uiSettings = mMapFragment.getMap().getUiSettings();
        // Show the location on the map
        mMapFragment.getMap().setMyLocationEnabled(true);
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
