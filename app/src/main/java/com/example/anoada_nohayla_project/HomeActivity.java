package com.example.anoada_nohayla_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        Toast.makeText(HomeActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    public void userActivity(View view)
    {
        startActivity(new Intent(HomeActivity.this, UserActivity.class));
        finish();
    }
    public void homeActivity(View view)
    {
        startActivity(new Intent(HomeActivity.this, HomeActivity.class));
        finish();
    }
    public void mapActivity(View view)
    {
        startActivity(new Intent(HomeActivity.this, MapActivity.class));
        finish();
    }

}