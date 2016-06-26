package com.nkdroid.dressify;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.dressify.data.Data;
import com.nkdroid.dressify.data.UserProvider;
import com.nkdroid.dressify.tindercard.FlingCardListener;
import com.nkdroid.dressify.tindercard.ScheduleCombination;
import com.nkdroid.dressify.tindercard.SwipeFlingAdapterView;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.markushi.ui.CircleButton;


public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    private ProgressDialog dialog;
    public static MyAppAdapter myAppAdapter;
    public static MyAppAdapterPants myAppAdapterPants;
    public static ViewHolder viewHolder;
    public static ViewHolderPants viewHolderPants;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ArrayList<Data> al;
    private ArrayList<Data> alPants;
    private SwipeFlingAdapterView flingContainer;
    private SwipeFlingAdapterView flingContainerPants;
    private CircleButton addShirtsButton;
    private CircleButton addPantsButton;
    private CircleButton shuffleButton;
    private CircleButton favoriteButton;
    private LinearLayout lLayout;
    private FrameLayout fLayout;
    private final int REQUEST_IMAGE_SELECTOR_SHIRTS = 1;
    private final int REQUEST_IMAGE_SELECTOR_PANTS = 2;


    public static void removeBackground() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ScheduleCombination.class);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        // Set the alarm to start at approximately 6:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 06);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);


        dialog = new ProgressDialog(this);
        lLayout = (LinearLayout) findViewById(R.id.linearLayout);
        fLayout = (FrameLayout) findViewById(R.id.frameLayout);
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame_shirts);
        flingContainerPants = (SwipeFlingAdapterView) findViewById(R.id.frame_jeans);
        addShirtsButton = (CircleButton) findViewById(R.id.addShirtButton);
        addPantsButton = (CircleButton) findViewById(R.id.addPantsButton);
        shuffleButton = (CircleButton) findViewById(R.id.toggleCombinationButton);
        favoriteButton = (CircleButton) findViewById(R.id.likeCombinationButton);
        al = Data.getShirtsInDirectory();
        alPants = Data.getPantsInDirectory();

        flingCallerShirt();


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                al.clear();
                alPants.clear();
                myAppAdapter.notifyDataSetChanged();
                myAppAdapterPants.notifyDataSetChanged();

                dialog.setMessage("Shuffling Pants And Shirts");
                dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                dialog.setCancelable(false);
                dialog.show();

                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {

                        dialog.dismiss();
                        al = new ArrayList<Data>();
                        al = Data.getRandomShirts();
                        myAppAdapter.notifyDataSetChanged();
                        alPants = new ArrayList<Data>();
                        alPants = Data.getRandomPants();
                        myAppAdapterPants.notifyDataSetChanged();
                        flingCallerShirt();

                    }
                }.start();


            }
        });

        addShirtsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchPhotoSelectionIntent(REQUEST_IMAGE_SELECTOR_SHIRTS);

            }
        });

        addPantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchPhotoSelectionIntent(REQUEST_IMAGE_SELECTOR_PANTS);

            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alPants != null && alPants.size() > 0 && al != null && al.size() > 0 ){

                    if(al.get(0).getUri().equals("ADDMORESHIRTS") || alPants.get(0).getUri().equals("ADDMOREPANTS")){
                        Toast.makeText(getApplicationContext(),"Combination is incorrect! Check whether both shirt and pant are there in the combination",Toast.LENGTH_LONG).show();
                    }
                    else {
                        ContentValues values = new ContentValues();

                        int shirtId = al.get(0).getId();
                        String sdata = al.get(0).getUri();
                        int pantid = alPants.get(0).getId();
                        String pdata = alPants.get(0).getUri();
                        values.put(UserProvider._SHIRT_ID_FAV, shirtId);
                        values.put(UserProvider._SHIRT_DATA_FAV, sdata);
                        values.put(UserProvider._PANTS_ID_FAV, pantid);
                        values.put(UserProvider._PANTS_DATA_FAV, pdata);

                        String where = UserProvider._SHIRT_ID + " =? AND " + UserProvider._PANTS_ID_FAV + " =?";
                        String[] whereArgs = {String.valueOf(shirtId), String.valueOf(pantid)};
                        ContentResolver contentResolver = getApplicationContext().getContentResolver();
                        String[] projectionFav = {
                                UserProvider._SHIRT_ID_FAV,
                                UserProvider._PANTS_ID_FAV
                        };
                        Cursor cursor = contentResolver.query(UserProvider.CONTENT_URI_FAVORITE, projectionFav, where, whereArgs, null);
                           if (cursor.getCount() > 0) {

                               Toast.makeText(getApplicationContext(),"Already Marked As Favorite",Toast.LENGTH_LONG).show();

                           } else {

                                    Uri uriDb = getApplicationContext().getContentResolver().insert(
                                    UserProvider.CONTENT_URI_FAVORITE, values);
                                    Toast.makeText(getApplicationContext(), "You liked this combination", Toast.LENGTH_SHORT).show();
                            }


                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"Combination is incorrect! Check whether both shirt and pant are there",Toast.LENGTH_LONG).show();
                }


            }
        });


    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.getSerializable("shirts") != null){


                al = (ArrayList<Data>) savedInstanceState.getSerializable("shirts");
            }

            if(savedInstanceState.getSerializable("pants") != null){


                alPants = (ArrayList<Data>) savedInstanceState.getSerializable("pants");
            }

        }
        flingCallerShirt();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(al != null ){
            ArrayList<Data> shirtsList = new ArrayList<Data>(al);
            savedInstanceState.putSerializable("shirts", shirtsList);
        }
        if(alPants != null){

            ArrayList<Data> pantsList = new ArrayList<Data>(alPants);
            savedInstanceState.putSerializable("pants", pantsList);

        }


    }

    private void dispatchPhotoSelectionIntent(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(galleryIntent, requestCode);
    }

    public void flingCallerShirt(){


        myAppAdapter = new MyAppAdapter(al, MainActivity.this);

        myAppAdapterPants = new MyAppAdapterPants(alPants, MainActivity.this);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame_shirts);
        flingContainerPants = (SwipeFlingAdapterView) findViewById(R.id.frame_jeans);

        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                if (al.size() > 1) {
                    al.remove(0);
                    myAppAdapter.notifyDataSetChanged();
                } else {
                    al.remove(0);
                    al.add(0, new Data("ADDMORESHIRTS", 0));
                    myAppAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Click + to Add More Shirts", Toast.LENGTH_SHORT).show();
                }
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                if (al.size() > 1) {

                    al.remove(0);

                    myAppAdapter.notifyDataSetChanged();
                } else {
                    al.remove(0);
                    al.add(0, new Data("ADDMORESHIRTS", 0));
                    myAppAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Click + to Add More Shirts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                if (itemsInAdapter == 0) {
                    al.add(0, new Data("ADDMORESHIRTS", 0));

                    myAppAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();

            }
        });


        flingContainerPants.setAdapter(myAppAdapterPants);
        flingContainerPants.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                if(alPants.size() > 1){
                    alPants.remove(0);
                    myAppAdapterPants.notifyDataSetChanged();
                }
                else{
                    alPants.remove(0);
                    alPants.add(0,new Data("ADDMOREPANTS",0));
                    myAppAdapterPants.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"Click + to Add More Pants",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                if(alPants.size() > 1){

                    alPants.remove(0);
                    myAppAdapterPants.notifyDataSetChanged();
                }
                else{
                    alPants.remove(0);
                    alPants.add(0,new Data("ADDMOREPANTS",0));
                    myAppAdapterPants.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"Click + to Add More Pants",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                if(itemsInAdapter == 0){
                    alPants.add(0, new Data("ADDMOREPANTS", 0));
                    myAppAdapterPants.notifyDataSetChanged();

                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainerPants.getSelectedView();

            }
        });




    }


    private final static String[] projection = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_SELECTOR_SHIRTS:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    myAppAdapter.notifyDataSetChanged();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getApplicationContext().getContentResolver().query(data.getData(), projection, null, null, null);
                    if (cursor == null || cursor.getCount() < 1) {
                        
                        break;
                    }
                    cursor.moveToFirst();
                    int idcolumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int datacolumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                    int id = cursor.getInt(idcolumnIndex);
                    String uri = cursor.getString(datacolumnIndex);
                    
                    if(al.get(0).getUri().equals("ADDMORESHIRTS")){
                        ContentValues values = new ContentValues();
                        values.put(UserProvider._SHIRT_ID, id);
                        values.put(UserProvider._SHIRT_DATA, uri);
                        try{
                        Uri uriDb = getApplicationContext().getContentResolver().insert(
                                UserProvider.CONTENT_URI_SHIRTS, values);

                            final ArrayList<Data> newListShirt = new ArrayList<Data>();

                            newListShirt.add(0,new Data(uri,id));

                            al.clear();

                            myAppAdapter.notifyDataSetChanged();


                            dialog.setMessage("Refreshing Shirts");
                            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                            dialog.setCancelable(false);
                            dialog.show();

                            new CountDownTimer(500, 250) {

                                public void onTick(long millisUntilFinished) {
                                    //nothing
                                }

                                public void onFinish() {

                                    dialog.dismiss();

                                    al = new ArrayList<Data>();
                                    al = newListShirt;
                                    myAppAdapter.notifyDataSetChanged();
                                    flingCallerShirt();
                                }
                            }.start();

                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Already added previously",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(al != null){

                        ContentValues values = new ContentValues();
                        values.put(UserProvider._SHIRT_ID, id);
                        values.put(UserProvider._SHIRT_DATA, uri);
                        try {
                            Uri uriDb = getApplicationContext().getContentResolver().insert(
                                    UserProvider.CONTENT_URI_SHIRTS, values);

                            final ArrayList<Data> newListShirt = new ArrayList<Data>();

                            newListShirt.add(0,new Data(uri,id));

                            for(int i = 1; i< al.size();i++){
                                newListShirt.add(i,al.get(i-1));
                            }

                            al.clear();

                            myAppAdapter.notifyDataSetChanged();


                            dialog.setMessage("Refreshing Shirts");
                            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                            dialog.setCancelable(false);
                            dialog.show();

                            new CountDownTimer(1000, 500) {

                                public void onTick(long millisUntilFinished) {
                                    //nothing
                                }

                                public void onFinish() {

                                    dialog.dismiss();

                                    al = new ArrayList<Data>();
                                    al = newListShirt;
                                    myAppAdapter.notifyDataSetChanged();
                                    flingCallerShirt();
                                }
                            }.start();
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Already added previously",Toast.LENGTH_SHORT).show();
                        }


                    }


                    cursor.close();
                } else {
                  //  mCurrentPhoto = null;
                }
                break;

            case REQUEST_IMAGE_SELECTOR_PANTS:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    myAppAdapterPants.notifyDataSetChanged();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getApplicationContext().getContentResolver().query(data.getData(), projection, null, null, null);
                    if (cursor == null || cursor.getCount() < 1) {
                        //mCurrentPhoto = null;
                        break;
                    }
                    cursor.moveToFirst();
                    int idcolumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int datacolumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                    int id = cursor.getInt(idcolumnIndex);
                    String uri = cursor.getString(datacolumnIndex);

                    if(alPants.get(0).getUri().equals("ADDMOREPANTS")){
                        //alPants.remove(0);
                        ContentValues values = new ContentValues();
                        values.put(UserProvider._PANTS_ID,id);
                        values.put(UserProvider._PANTS_DATA, uri);
                     try{
                        Uri uriDb = getApplicationContext().getContentResolver().insert(
                                UserProvider.CONTENT_URI_PANTS, values);

                         final ArrayList<Data> newListPants = new ArrayList<Data>();

                         newListPants.add(0,new Data(uri,id));

                         alPants.clear();

                         myAppAdapterPants.notifyDataSetChanged();


                         dialog.setMessage("Refreshing Pants");
                         dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                         dialog.setCancelable(false);
                         dialog.show();

                         new CountDownTimer(1000, 500) {

                             public void onTick(long millisUntilFinished) {
                                 //nothing
                             }

                             public void onFinish() {

                                 dialog.dismiss();

                                 alPants = new ArrayList<Data>();
                                 alPants = newListPants;
                                 myAppAdapterPants.notifyDataSetChanged();
                                 flingCallerShirt();
                             }
                         }.start();


                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Already added previously",Toast.LENGTH_SHORT).show();
                    }

                    }
                    else if(alPants != null){

                        ContentValues values = new ContentValues();
                        values.put(UserProvider._PANTS_ID,id);
                        values.put(UserProvider._PANTS_DATA, uri);
                     try{
                        Uri uriDb = getApplicationContext().getContentResolver().insert(
                                UserProvider.CONTENT_URI_PANTS, values);

                         final ArrayList<Data> newListPants = new ArrayList<Data>();

                         newListPants.add(0,new Data(uri,id));

                         for(int i = 1; i< alPants.size();i++){
                             newListPants.add(i,alPants.get(i-1));
                         }

                         alPants.clear();

                         myAppAdapterPants.notifyDataSetChanged();


                         dialog.setMessage("Refreshing Pants");
                         dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                         dialog.setCancelable(false);
                         dialog.show();

                         new CountDownTimer(1000, 500) {

                             public void onTick(long millisUntilFinished) {
                                 //nothing
                             }

                             public void onFinish() {

                                 dialog.dismiss();

                                 alPants = new ArrayList<Data>();
                                 alPants = newListPants;
                                 myAppAdapterPants.notifyDataSetChanged();
                                 flingCallerShirt();
                             }
                         }.start();


                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Already added previously",Toast.LENGTH_SHORT).show();
                    }

                    }


                    cursor.close();
                } else {
                    //  mCurrentPhoto = null;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /*//Open system camera application to capture a photo.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getApplication().getPackageManager()) != null) {
            try {
                createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (mCurrentPhoto != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhoto));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }*/

    @Override
    public void onActionDownPerform() {
        
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView AddMoreText;
        public ImageView cardImage;


    }

    public static class ViewHolderPants {
        public static FrameLayout background;
        public TextView AddMoreText;
        public ImageView cardImage;


    }

    public class MyAppAdapter extends BaseAdapter {

        public List<Data> adapterList;
        public Context context;

        private MyAppAdapter(List<Data> apps, Context context) {

            this.adapterList = apps;
            this.context = context;
        }

        public List<Data> getadapterList() {
            return adapterList;
        }

        public void setadapterList(List<Data> adapterList) {
            this.adapterList = adapterList;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                viewHolder.AddMoreText = (TextView) rowView.findViewById(R.id.clickToAddMore);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(!adapterList.get(position).getUri().equals("ADDMORESHIRTS")){
                viewHolder.cardImage.setVisibility(View.VISIBLE);
                Glide.with(MainActivity.this).load(adapterList.get(position).getUri()).into(viewHolder.cardImage);

                viewHolder.AddMoreText.setVisibility(View.INVISIBLE);
            }
            else{
                viewHolder.cardImage.setVisibility(View.INVISIBLE);
                viewHolder.AddMoreText.setVisibility(View.VISIBLE);
                viewHolder.AddMoreText.setText("Click + to Add \n More Shirts");
            }

            return rowView;
        }

        public void updateReceiptsList(List<Data> newlist) {
            adapterList.clear();
            adapterList.addAll(newlist);
            this.notifyDataSetChanged();

        }

        public void swapItems(List<Data> items) {
            this.adapterList = items;
            notifyDataSetChanged();
        }

        public void setAtZero(){

            viewHolder.cardImage.setVisibility(View.VISIBLE);
            Glide.with(MainActivity.this).load(adapterList.get(0).getUri()).into(viewHolder.cardImage);

            viewHolder.AddMoreText.setVisibility(View.INVISIBLE);

        }

    }

    public class MyAppAdapterPants extends BaseAdapter {


        public List<Data> adapterList;
        public Context context;

        private MyAppAdapterPants(List<Data> apps, Context context) {
            this.adapterList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.itempants, parent, false);
                // configure view holder
                viewHolderPants = new ViewHolderPants();
                viewHolderPants.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                viewHolderPants.AddMoreText = (TextView) rowView.findViewById(R.id.clickToAddMore);
                rowView.setTag(viewHolderPants);

            } else {
                viewHolderPants = (ViewHolderPants) convertView.getTag();
            }


            if(!adapterList.get(position).getUri().equals("ADDMOREPANTS")){
                viewHolderPants.cardImage.setVisibility(View.VISIBLE);
                Glide.with(MainActivity.this).load(adapterList.get(position).getUri()).into(viewHolderPants.cardImage);

                viewHolderPants.AddMoreText.setVisibility(View.INVISIBLE);
            }
            else{
                viewHolderPants.cardImage.setVisibility(View.INVISIBLE);
                viewHolderPants.AddMoreText.setVisibility(View.VISIBLE);
                viewHolderPants.AddMoreText.setText("Click + to Add \n More Pants");
            }



            return rowView;
        }

        public void swapItems(List<Data> items) {
            this.adapterList = items;
            notifyDataSetChanged();
        }
    }


}
