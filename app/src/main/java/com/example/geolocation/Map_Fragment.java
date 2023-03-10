package com.example.geolocation;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Map_Fragment extends Fragment {

    private String address;
    private GoogleMap map = null;

    private Location location;

    public Map_Fragment(Location _location, String _address){
        address = _address;
        location = _location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.Map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
//                map.setMyLocationEnabled(true);

                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                markerOptions.title("Location: " + address);
                map.clear();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng).zoom(14).bearing(90).tilt(45).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.addMarker(markerOptions);
            }
        });

        return view;
    }


}