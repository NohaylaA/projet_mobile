package com.example.anoada_nohayla_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener
{
    private GoogleMap mMap;
    private LocationActivity locationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationActivity = new LocationActivity(this, this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationActivity.checkPermissionsAndGetLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationActivity.LOCATION_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                locationActivity.getLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationActivity.stopLocationUpdates();
    }
    public void showSettingsMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_settings, popup.getMenu());
        popup.show();
    }

    public void profile(MenuItem item) {
        Toast.makeText(MapActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MapActivity.this, ProfileActivity.class));
    }

    public void deconnecter(MenuItem item)
    {
        DBHelper DB = new DBHelper(this);
        Cursor cursor = DB.howIsConnected();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                DB.connecteOrDeconnecte(email, "false");
            } while (cursor.moveToNext());
            cursor.close();
        }

        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MapActivity.this, MainActivity.class));
        finish();
    }

    public void userActivity(View view) {
        startActivity(new Intent(MapActivity.this, UserActivity.class));
        finish();
    }

    public void homeActivity(View view) {
        startActivity(new Intent(MapActivity.this, HomeActivity.class));
        finish();
    }

    public void mapActivity(View view)
    {
        startActivity(new Intent(MapActivity.this, MapActivity.class));
        finish();
    }
}
