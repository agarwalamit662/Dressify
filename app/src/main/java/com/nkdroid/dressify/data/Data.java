
package com.nkdroid.dressify.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


import com.nkdroid.dressify.ClothesApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static String[] projectionShirt = {
            UserProvider._SHIRT_ID,
            UserProvider._SHIRT_DATA
    };
    private final static String[] projectionPants = {
            UserProvider._PANTS_ID,
            UserProvider._PANTS_DATA
    };
    private static ContentResolver mediaResolver;
    private String uri;
    private int id;
    public static Context context;

    public Data(String uri, int id) {
        this.uri = uri;
        this.id = id;

    }

    public static ArrayList<Data> getPantsInDirectory() {
        ArrayList<Data> pantsList = new ArrayList<>();
        Context contexts = ClothesApplication.getContext();

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._PANTS_ID + " desc ";
        Uri uri = UserProvider.CONTENT_URI_PANTS;

        Cursor cursor = resolver.query(uri, projectionPants, null, null, sortOrder);
        int id = cursor.getColumnIndex(UserProvider._PANTS_ID);
        int sdata = cursor.getColumnIndex(UserProvider._PANTS_DATA);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int mid = cursor.getInt(id);
                String data = cursor.getString(sdata);


                pantsList.add(new Data(data,mid));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return pantsList;
    }


    public static ArrayList<Data> getRandomShirts(){

        ArrayList<Data> random = getShirtsInDirectory();

        long seed = System.nanoTime();
        Collections.shuffle(random);
        return random;
    }

    public static ArrayList<Data> getRandomPants(){

        ArrayList<Data> random = getPantsInDirectory();

        long seed = System.nanoTime();
        Collections.shuffle(random);
        return random;
    }

    public static ArrayList<Data> getShirtsInDirectory() {
        ArrayList<Data> shirtsList = new ArrayList<>();
        Context contexts = ClothesApplication.getContext();



        String[] selectionArgs = new String[]{String.valueOf(0)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SHIRT_ID + " desc ";
        Uri uri = UserProvider.CONTENT_URI_SHIRTS;

        Cursor cursor = resolver.query(uri, projectionShirt, null, null, sortOrder);
        int id = cursor.getColumnIndex(UserProvider._SHIRT_ID);
        int sdata = cursor.getColumnIndex(UserProvider._SHIRT_DATA);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int mid = cursor.getInt(id);
                String data = cursor.getString(sdata);


                shirtsList.add(new Data(data,mid));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return shirtsList;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

