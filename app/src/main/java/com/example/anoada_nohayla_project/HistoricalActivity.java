package com.example.anoada_nohayla_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.SimpleAdapter;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoricalActivity extends AppCompatActivity
{
    private LinearLayout linearLayoutActivities;
    private DBHelper dbHelper;
    private SimpleAdapter adapter;
    private List<Map<String, String>> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);

        linearLayoutActivities = findViewById(R.id.linear_layout_activities);
        dbHelper = new DBHelper(this);

        refreshActivityList();
    }

    private void refreshActivityList() {
        linearLayoutActivities.removeAllViews();
        activityList = getAllActivities();

        String[] from = {"name", "date_debut","date_fin", "name"};
        int[] to = {R.id.activity_name, R.id.activity_date_debut, R.id.activity_date_fin, R.id.activity_image};

        adapter = new SimpleAdapter(this, activityList, R.layout.activity_item, from, to);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView && data instanceof String) {
                    ImageView imageView = (ImageView) view;
                    String activityName = (String) data;
                    int imageResource = getResources().getIdentifier(activityName.toLowerCase(), "drawable", getPackageName());
                    imageView.setImageResource(imageResource);
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < activityList.size(); i++) {
            Map<String, String> activity = activityList.get(i);
            View view = adapter.getView(i, null, null);
            ImageView deleteIcon = view.findViewById(R.id.delete_icon);
            ImageView locationIcon = view.findViewById(R.id.location_icon); // Ajout de la récupération de l'icône de localisation
            deleteIcon.setTag(i); // Set tag with position or activity ID
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Map<String, String> activity = (Map<String, String>) adapter.getItem(position);
                    int activityId = Integer.parseInt(activity.get("id")); // Assuming "id" is the key for activity ID
                    deleteActivity(activityId);
                }
            });
            locationIcon.setTag(i); // Set tag with position or activity ID
            locationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Map<String, String> activity = (Map<String, String>) adapter.getItem(position);
                    String dateDebut = activity.get("date_debut");
                    String dateFin = activity.get("date_fin");
                    String activite = activity.get("name");
                    String pourcentage = activity.get("pourcentage");
                    String localisationDebut = activity.get("localisation_debut");
                    String localisationFin = activity.get("localisation_fin");

                    openTestActivity(dateDebut, dateFin, localisationDebut, localisationFin, activite, pourcentage);
                }
            });
            linearLayoutActivities.addView(view);
        }
    }

    @SuppressLint("Range")
    private List<Map<String, String>> getAllActivities() {
        List<Map<String, String>> activityList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllActivities();

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> activity = new HashMap<>();
                activity.put("id", cursor.getString(cursor.getColumnIndex("id")));
                activity.put("date_debut", cursor.getString(cursor.getColumnIndex("date_debut")));
                activity.put("date_fin", cursor.getString(cursor.getColumnIndex("date_fin")));
                activity.put("name", cursor.getString(cursor.getColumnIndex("activite")));
                activity.put("pourcentage", cursor.getString(cursor.getColumnIndex("pourcentage")));
                activity.put("localisation_debut", cursor.getString(cursor.getColumnIndex("localisation_debut")));
                activity.put("localisation_fin", cursor.getString(cursor.getColumnIndex("localisation_fin")));
                activityList.add(activity);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return activityList;
    }

    private void deleteActivity(int activityId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cette activité ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleted = dbHelper.deleteActivity(activityId);
                        if (deleted) {
                            Toast.makeText(HistoricalActivity.this, "Activité supprimée avec succès", Toast.LENGTH_SHORT).show();
                            refreshActivityList();
                        } else {
                            Toast.makeText(HistoricalActivity.this, "Échec de la suppression de l'activité", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Fermer le dialogue sans rien faire
                    }
                })
                .show();
    }


    private void openTestActivity(String dateDebut, String dateFin, String localisationDebut, String localisationFin, String activite, String pourcentage) {
        Intent intent = new Intent(HistoricalActivity.this, MapUserActivity.class);
        intent.putExtra("dateDebut", dateDebut);
        intent.putExtra("dateFin", dateFin);
        intent.putExtra("localisationDebut", localisationDebut);
        intent.putExtra("localisationFin", localisationFin);
        intent.putExtra("activite", activite);
        intent.putExtra("pourcentage", pourcentage);
        startActivity(intent);
    }



    public void showSettingsMenu(View view)
    {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_settings, popup.getMenu());
        popup.show();
    }

    public void profile(MenuItem item)
    {
        Toast.makeText(HistoricalActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HistoricalActivity.this, ProfileActivity.class));
    }

    public void deconnecter(MenuItem item)
    {
        DBHelper DB = new DBHelper(this);
        Cursor cursor = DB.howIsConnected();

        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                DB.connecteOrDeconnecte(email, "false");
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HistoricalActivity.this, MainActivity.class));
        finish();
    }

    public void userActivity(View view)
    {
        startActivity(new Intent(HistoricalActivity.this, UserActivity.class));
        finish();
    }

    public void homeActivity(View view)
    {
        startActivity(new Intent(HistoricalActivity.this, HomeActivity.class));
        finish();
    }
    public void mapActivity(View view)
    {
        startActivity(new Intent(HistoricalActivity.this, MapActivity.class));
        finish();
    }
}