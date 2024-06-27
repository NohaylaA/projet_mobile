package com.example.anoada_nohayla_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    DBHelper DB;
    EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DB = new DBHelper(this);

        Cursor cursor = DB.howIsConnected();
        if (cursor != null && cursor.moveToFirst())
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        Button loginButton = findViewById(R.id.id_login);
        emailEditText = findViewById(R.id.id_email);
        passwordEditText = findViewById(R.id.id_password);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                loginUser();
            }
        });
    }

    private void loginUser()
    {
        String user = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        if (user.equals("") || pass.equals(""))
        {
            Toast.makeText(MainActivity.this, "Veuillez saisir tous les champs", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Boolean checkUserPass = DB.checkEmailPassword(user, pass);
            if (checkUserPass)
            {
                DB.connecteOrDeconnecte(user, "true");
                Toast.makeText(MainActivity.this, "Enregistrement réussi", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Informations de connexion invalides", Toast.LENGTH_SHORT).show();
            }
        }
    }

     public void register(View view)
    {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

}