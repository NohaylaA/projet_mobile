package com.example.anoada_nohayla_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity
{
    DBHelper DB;
    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, phoneEditText, filiereEditText;
    RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.id_name);
        emailEditText = findViewById(R.id.id_email);
        passwordEditText = findViewById(R.id.id_password);
        confirmPasswordEditText = findViewById(R.id.id_confirm_password);
        phoneEditText = findViewById(R.id.id_phone);
        filiereEditText = findViewById(R.id.id_filiere);
        genderRadioGroup = findViewById(R.id.radio_group_gender);
        Button registerButton = findViewById(R.id.register_button);

        NotificationHelper.createNotificationChannel(this);

        DB = new DBHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerUser();
            }
        });
    }

    private void registerUser()
    {
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        String gender = "";
        if (selectedGenderId != -1)
        {
            RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
            gender = selectedGenderRadioButton.getText().toString();
        }

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String filiere = filiereEditText.getText().toString();

        if(name.equals("")||email.equals("")||password.equals("")||confirmPassword.equals("")||phone.equals("")||filiere.equals("")||gender.equals(""))
        {
            Toast.makeText(RegisterActivity.this, "Veuillez saisir tous les champs", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(password.equals(confirmPassword))
            {

                Boolean checkEmail = DB.checkEmail(email);
                if(!checkEmail)
                {
                    Boolean insert = DB.insertData( RegisterActivity.this,name, gender, email, password, phone, filiere);
                    if(insert)
                    {
                        String notificationTitle = "Enregistrement réussi";
                        String notificationMessage = " Bienvenue a WalkMate ";
                        NotificationHelper.showNotification(this, notificationTitle, notificationMessage);

                        Toast.makeText(RegisterActivity.this, "Enregistrement réussi", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Échec de l'enregistrement", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "e-mail est déjà utilisé par un autre utilisateur", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(RegisterActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void login(View view)
    {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
