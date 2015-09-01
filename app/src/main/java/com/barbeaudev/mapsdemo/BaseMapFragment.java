package com.barbeaudev.mapsdemo;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

/**
 * Created by barbeau on 9/1/2015.
 */
public class BaseMapFragment extends SupportMapFragment {

    public static BaseMapFragment newInstance() {
        return new BaseMapFragment();
    }
}
