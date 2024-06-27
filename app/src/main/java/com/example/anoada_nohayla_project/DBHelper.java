package com.example.anoada_nohayla_project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DBNAME = "WalkMate.db";

    public DBHelper(Context context)
    {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB)
    {
        MyDB.execSQL("create Table users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT unique,nom TEXT ,password TEXT,phone TEXT,sexe TEXT,filiere TEXT,photo BLOB,is_connected TEXT)");
        MyDB.execSQL("CREATE TABLE activities(id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, date_debut TEXT, date_fin TEXT, activite TEXT, pourcentage INTEGER, localisation_debut TEXT, localisation_fin TEXT, FOREIGN KEY(userId) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1)
    {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
        MyDB.execSQL("DROP TABLE IF EXISTS activities");
        onCreate(MyDB);
    }

    private byte[] getBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Boolean insertData(Context context,String name,String gender,String email,String password,String phone,String filiere)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues contentValues= new ContentValues();

        contentValues.put("email", email);
        contentValues.put("nom" , name);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        contentValues.put("sexe", gender);
        contentValues.put("filiere", filiere);

        Bitmap defaultProfileImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile);
        byte[] imageData = getBytes(defaultProfileImage);
        contentValues.put("photo", imageData);

        contentValues.put("is_connected", "false");

        long result = MyDB.insert("users", null, contentValues);

        MyDB.close();

        return result != -1;
    }

    public Boolean insertActivity(int userId, String dateDebut, String dateFin, String activite, int pourcentage, String localisationDebut, String localisationFin)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("date_debut", dateDebut);
        contentValues.put("date_fin", dateFin);
        contentValues.put("activite", activite);
        contentValues.put("pourcentage", pourcentage);
        contentValues.put("localisation_debut", localisationDebut);
        contentValues.put("localisation_fin", localisationFin);

        long result = MyDB.insert("activities", null, contentValues);

        MyDB.close();

        return result != -1;
    }

    public Cursor getConnectedUser()
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("SELECT * FROM users WHERE is_connected = 'true'", null);
    }

    public Boolean updateUser(int id,String name,String gender,String email,String password,String phone,String filiere)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("nom" , name);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        contentValues.put("sexe", gender);
        contentValues.put("email", email);
        contentValues.put("filiere", filiere);

        long result = MyDB.update("users", contentValues, "id = ?", new String[]{String.valueOf(id)});

        MyDB.close();

        return result != -1;
    }

    public boolean saveProfileImage(int id,byte[] imageData)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("photo", imageData);
        long result = MyDB.update("users", contentValues, "id = ?", new String[]{String.valueOf(id)});

        MyDB.close();
        return result != -1;
    }


    public Boolean checkEmail(String email)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        MyDB.close();
        return exists;
    }

    public Boolean checkUserEmail(int userId, String newEmail)
    {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM users WHERE email = ? AND id != ?", new String[]{newEmail, String.valueOf(userId)});
        boolean available = cursor.getCount() == 0;
        cursor.close();
        MyDB.close();
        return available;
    }

    public Boolean checkEmailPassword(String email,String password)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ? and password = ?", new String[] {email,password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        MyDB.close();
        return valid;
    }

    public Boolean checkIdPassword(int userId,String password)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where id = ? and password = ?", new String[] {String.valueOf(userId),password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        MyDB.close();
        return valid;
    }

    public void connecteOrDeconnecte(String email, String isConnected)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_connected", isConnected);
        MyDB.update("users", contentValues, "email = ?", new String[]{email});
        MyDB.close();
    }

    public Cursor howIsConnected()
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("Select * from users where is_connected = 'true'", null);
    }

    public Bitmap getProfileImage(int userId)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where id = ? ", new String[] {String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex("photo"));
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        cursor.close();
        MyDB.close();
        return null;
    }

    public Cursor getAllActivities() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM activities", null);
    }

    public boolean deleteActivity(int activityId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("activities", "id = ?", new String[]{String.valueOf(activityId)});
        db.close();
        return result > 0;
    }

}

