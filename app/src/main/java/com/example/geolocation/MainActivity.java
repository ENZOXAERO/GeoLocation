package com.example.geolocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static int REQUEST_CODE = 100;

    private LocationManager locationManager;
    Location current ;
    FusedLocationProviderClient fusedLocationProviderClient;

    private TextView lblLocationLatitude, lblLocationLongitude, lblLocationAddress;
    private Button btnShare;

    private void loadControls(){
        lblLocationLatitude = findViewById(R.id.lblLocationLatitude);
        lblLocationLongitude = findViewById(R.id.lblLocationLongitude);
        lblLocationAddress = findViewById(R.id.lblLocationAddress);
        btnShare = findViewById(R.id.btnShare);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadControls();

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            askPermissions();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener)this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener)this);


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if(task.isSuccessful()){
                    current = task.getResult();
                try {
                    List<Address> addresses = geocoder.getFromLocation(current.getLatitude(), current.getLongitude(),1);
                    Address address = addresses.get(0);
                    lblLocationAddress.setText(address.getAddressLine(0));
                } catch (IOException e) {
                }
                    Fragment fragment = new Map_Fragment(current, lblLocationAddress.getText().toString());
                    getSupportFragmentManager().beginTransaction().replace(R.id.frmLayout, fragment).commit();
                }
            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationMap = "https://maps.google.com/?q="+current.getLatitude()+"," + current.getLongitude();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(intent.EXTRA_TEXT,"Hi, i shared my location: \n" + locationMap);
                if(intent.resolveActivity(getPackageManager()) == null){
                    Toast.makeText(MainActivity.this, "You don't have whatsapp installed", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
            }
        });
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE) ;

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE)
            return;

        if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lblLocationLatitude.setText("" + location.getLatitude());
        lblLocationLongitude.setText("" + location.getLongitude());
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
        lblLocationLatitude.setText("" + locations.get(0).getLatitude());
        lblLocationLongitude.setText("" + locations.get(0).getLongitude());
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}