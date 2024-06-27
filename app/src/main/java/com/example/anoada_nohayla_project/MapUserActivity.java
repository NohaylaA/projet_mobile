package com.example.anoada_nohayla_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapUserActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private TextView dateDebutTextView,dateFinTextView,localisationDebutTextView,localisationFinTextView,activiteTextView,pourcentageTextView;
    String dateDebut ,dateFin ,localisationDebut,localisationFin,activite,pourcentage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user);

        dateDebutTextView = findViewById(R.id.dateDebutTextView);
        dateFinTextView = findViewById(R.id.dateFinTextView);
        activiteTextView = findViewById(R.id.activiteTextView);

        Intent intent = getIntent();
        dateDebut = intent.getStringExtra("dateDebut");
        dateFin = intent.getStringExtra("dateFin");
        localisationDebut = intent.getStringExtra("localisationDebut");
        localisationFin = intent.getStringExtra("localisationFin");
        activite = intent.getStringExtra("activite");
        pourcentage = intent.getStringExtra("pourcentage");

        String text=activite + " avec " + pourcentage +"%";

        dateDebutTextView.setText(dateDebut);
        dateFinTextView.setText(dateFin);
        activiteTextView.setText(text);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        String lcDebut[] = localisationDebut.split(",");
        double latitudeDebut = Double.parseDouble(lcDebut[0]);
        double longitudeDebut = Double.parseDouble(lcDebut[1]);
        LatLng locationDebut = new LatLng(latitudeDebut, longitudeDebut);

        String lcFin[] = localisationFin.split(",");
        double latitudeFin = Double.parseDouble(lcFin[0]);
        double longitudeFin = Double.parseDouble(lcFin[1]);
        LatLng locationFin = new LatLng(latitudeFin, longitudeFin);

        mMap.addMarker(new MarkerOptions()
                .position(locationDebut)
                .title("Debut de l'activité " + activite));

        mMap.addMarker(new MarkerOptions()
                .position(locationFin)
                .title("Fin de l'activitée " + activite));

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(locationDebut)
                .add(locationFin)
                .color(Color.DKGRAY)
                .width(2);

        mMap.addPolyline(polylineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(locationDebut);
        builder.include(locationFin);
        LatLngBounds bounds = builder.build();
        int padding = 100;
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }
    public void showSettingsMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_settings, popup.getMenu());
        popup.show();
    }

    public void profile(MenuItem item) {
        Toast.makeText(MapUserActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MapUserActivity.this, ProfileActivity.class));
    }

    public void deconnecter(MenuItem item) {
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
        startActivity(new Intent(MapUserActivity.this, MainActivity.class));
        finish();
    }

    public void userActivity(View view)
    {
        startActivity(new Intent(MapUserActivity.this, UserActivity.class));
        finish();
    }
    public void homeActivity(View view)
    {
        startActivity(new Intent(MapUserActivity.this, HomeActivity.class));
        finish();
    }
    public void mapActivity(View view)
    {
        startActivity(new Intent(MapUserActivity.this, MapActivity.class));
        finish();
    }
}
