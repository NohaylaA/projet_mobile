package com.example.anoada_nohayla_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ProfileActivity extends AppCompatActivity
{
    private static final int IMAGE_PICKER_REQUEST_CODE = 1;
    private ImageView imageProfile;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText ancienPasswordEditText;
    private EditText nouveauPasswordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneEditText;
    private EditText filiereEditText;
    private RadioGroup genderRadioGroup;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DBHelper(this);

        ImageView edit = findViewById(R.id.id_edit);
        imageProfile = findViewById(R.id.id_image);
        nameEditText = findViewById(R.id.id_name);
        emailEditText = findViewById(R.id.id_email);
        ancienPasswordEditText = findViewById(R.id.id_ancien_password);
        nouveauPasswordEditText = findViewById(R.id.id_password);
        confirmPasswordEditText = findViewById(R.id.id_confirm_password);
        phoneEditText = findViewById(R.id.id_phone);
        filiereEditText = findViewById(R.id.id_filiere);
        genderRadioGroup = findViewById(R.id.radio_group_gender);
        Button editButton = findViewById(R.id.edit_button);

        edit.setOnClickListener(view -> ImagePicker.with(ProfileActivity.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST_CODE));

        loadUserProfile();

        editButton.setOnClickListener(view -> updateUserProfile());
    }

    private void loadUserProfile()
    {
        try (Cursor cursor = dbHelper.howIsConnected())
        {
            if (cursor != null && cursor.moveToFirst())
            {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("nom"));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("phone"));
                @SuppressLint("Range") String filiere = cursor.getString(cursor.getColumnIndex("filiere"));
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));

                nameEditText.setText(name);
                emailEditText.setText(email);
                phoneEditText.setText(phone);
                filiereEditText.setText(filiere);

                Bitmap profileImage = dbHelper.getProfileImage(id);
                if (profileImage != null)
                {
                    imageProfile.setImageBitmap(profileImage);
                }
                else
                {
                    imageProfile.setImageResource(R.drawable.baseline_person_color_24);
                }
                Toast.makeText(this, "Utilisateur connecté", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private void updateUserProfile()
    {
        try (Cursor cursor = dbHelper.howIsConnected())
        {
            if (cursor != null && cursor.moveToFirst())
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);

                String gender;
                if (selectedGenderRadioButton != null)
                {
                    gender = selectedGenderRadioButton.getText().toString();
                }
                else
                {
                    gender = cursor.getString(cursor.getColumnIndex("sexe"));
                }

                String name = nameEditText.getText().toString();
                String ancienPassword = ancienPasswordEditText.getText().toString();
                String nouveauPassword = nouveauPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String filiere = filiereEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || ancienPassword.isEmpty() || nouveauPassword.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || filiere.isEmpty())
                {
                    Toast.makeText(ProfileActivity.this, "Veuillez saisir tous les champs", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (dbHelper.checkUserEmail(id, email))
                    {
                        if (dbHelper.checkIdPassword(id, ancienPassword))
                        {
                            if (nouveauPassword.equals(confirmPassword))
                            {
                                if (dbHelper.updateUser(id, name, gender, email, nouveauPassword, phone, filiere))
                                {
                                    Toast.makeText(ProfileActivity.this, "Enregistrement réussi", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this, "Échec de l'enregistrement", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Le nouveau e-mail est déjà utilisé par un autre utilisateur", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Toast.makeText(ProfileActivity.this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            imageProfile.setImageURI(uri);
            try (InputStream inputStream = getContentResolver().openInputStream(uri))
            {
                try (Cursor cursor = dbHelper.howIsConnected())
                {
                    if (cursor != null && cursor.moveToFirst())
                    {
                        @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                        byte[] imageData = getBytes(inputStream);
                        if (dbHelper.saveProfileImage(id, imageData))
                        {
                            Toast.makeText(this, "Image de profil enregistrée avec succès", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(this, "Échec de l'enregistrement de l'image de profil", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la récupération de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1)
        {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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
        Toast.makeText(ProfileActivity.this, "profile", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
    }

    public void deconnecter(MenuItem item)
    {
        try (Cursor cursor = dbHelper.howIsConnected())
        {
            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                    dbHelper.connecteOrDeconnecte(email, "false");
                }
                while (cursor.moveToNext());
                Toast.makeText(this, "Déconnecté avec succès", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
            else
            {
                Toast.makeText(this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void userActivity(View view)
    {
        startActivity(new Intent(ProfileActivity.this, UserActivity.class));
        finish();
    }
    public void homeActivity(View view)
    {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        finish();
    }
    public void mapActivity(View view)
    {
        startActivity(new Intent(ProfileActivity.this, MapActivity.class));
        finish();
    }

}