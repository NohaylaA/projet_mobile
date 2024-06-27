package com.example.anoada_nohayla_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.widget.PopupMenu;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;


public class UserActivity extends AppCompatActivity implements SensorEventListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long SAVE_INTERVAL = 5000;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private int[] confidences = new int[5];
    private TextView confidenceAssis, confidenceMarcher, confidenceSauter, confidenceDebout;
    private DBHelper DB;
    private int userId;
    private String dateDebut;
    private String localisationDebut;
    private LocationManager locationManager;
    private Handler handler;
    private Runnable saveActivityRunnable;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        confidenceAssis = findViewById(R.id.assis_id);
        confidenceMarcher = findViewById(R.id.marcher_id);
        confidenceSauter = findViewById(R.id.sauter_id);
        confidenceDebout = findViewById(R.id.debout_id);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        NotificationHelper.createNotificationChannel(this);

        DB = new DBHelper(this);
        Cursor cursor = DB.getConnectedUser();

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
        }

        dateDebut = getCurrentDateTime();
        checkLocationPermission();

        handler = new Handler(Looper.getMainLooper());
        saveActivityRunnable = this::saveActivityPeriodically;
        handler.postDelayed(saveActivityRunnable, SAVE_INTERVAL);
    }

    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getLocation();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    private void getLocation()
    {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            displayLocation(lastKnownLocation);
        }
        else
        {
            Toast.makeText(this, "Location provider is not enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        localisationDebut = latitude + "," + longitude + "," + altitude;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            int activite = TypeActivite(linear_acceleration);
            confidences[activite]++;

            updateConfidenceDisplay();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateConfidenceDisplay() {
        int totalConfidence = getTotalConfidence();
        if (totalConfidence > 0) {
            int assis = confidences[0] * 100 / totalConfidence;
            int debout = confidences[1] * 100 / totalConfidence;
            int marcher = confidences[2] * 100 / totalConfidence;
            int sauter = confidences[3] * 100 / totalConfidence;

            confidenceAssis.setText(String.format("%d%%", assis));
            confidenceMarcher.setText(String.format("%d%%", marcher));
            confidenceSauter.setText(String.format("%d%%", sauter));
            confidenceDebout.setText(String.format("%d%%", debout));

            setupCircularChart(R.id.circularChart1, assis, R.color.violet, R.color.light_violet);
            setupCircularChart(R.id.circularChart2, sauter, R.color.blue, R.color.light_blue);
            setupCircularChart(R.id.circularChart3, marcher, R.color.green, R.color.light_green);
            setupCircularChart(R.id.circularChart4, debout, R.color.red, R.color.light_red);
        }
    }

    private int TypeActivite(float[] acceleration) {
        float x = acceleration[0];
        float y = acceleration[1];
        float z = acceleration[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        if (magnitude < 2.0f) return 0;
        else if (magnitude < 4.0f) return 1;
        else if (magnitude < 7.0f) return 2;
        else if (magnitude < 10.0f) return 3;
        else return 4;
    }

    private void saveActivityPeriodically() {
        saveActivity(getMostConfidentActivity());
        handler.postDelayed(saveActivityRunnable, SAVE_INTERVAL); // Schedule the next save
    }

    private int getMostConfidentActivity() {
        int maxConfidence = 0;
        int mostConfidentActivity = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                mostConfidentActivity = i;
            }
        }
        return mostConfidentActivity;
    }

    private void saveActivity(int activite) {
        String dateFin = getCurrentDateTime();
        String localisationFin = localisationDebut; // Replace with actual end location if needed
        String activiteStr = getActivityName(activite);
        int pourcentage = confidences[activite] * 100 / getTotalConfidence();

        DB.insertActivity(userId, dateDebut, dateFin, activiteStr, pourcentage, localisationDebut, localisationFin);

        dateDebut = dateFin;
        localisationDebut = localisationFin;

        confidences = new int[5];

        String notificationTitle = "Activité Detectée ";
        String notificationMessage = activiteStr + " avec " + pourcentage + "% de confiance";
        NotificationHelper.showNotification(this, notificationTitle, notificationMessage);
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String getActivityName(int activite) {
        switch (activite) {
            case 0: return "Assis";
            case 1: return "Debout";
            case 2: return "Marcher";
            case 3: return "Sauter";
            default: return "Marcher";
        }
    }

    private int getTotalConfidence() {
        int totalConfidence = 0;
        for (int value : confidences) {
            totalConfidence += value;
        }
        return totalConfidence;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(saveActivityRunnable); // Stop saving activities when the activity is paused
    }

    private void setupCircularChart(int chartId, float percentage, int colorResId, int backgroundColorResId) {
        CircularChartView circularChartView = findViewById(chartId);
        circularChartView.setPercentageAndColor(percentage, colorResId, backgroundColorResId);
    }

    public void showSettingsMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_settings, popup.getMenu());
        popup.show();
    }

    public void profile(MenuItem item) {
        Toast.makeText(UserActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UserActivity.this, ProfileActivity.class));
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
        startActivity(new Intent(UserActivity.this, MainActivity.class));
        finish();
    }

    public void userActivity(View view) {
        startActivity(new Intent(UserActivity.this, UserActivity.class));
        finish();
    }

    public void homeActivity(View view) {
        startActivity(new Intent(UserActivity.this, HomeActivity.class));
        finish();
    }
    public void historicalActivity(View view)
    {
        startActivity(new Intent(UserActivity.this, HistoricalActivity.class));
        finish();
    }
    public void mapActivity(View view)
    {
        startActivity(new Intent(UserActivity.this, MapActivity.class));
        finish();
    }
}
