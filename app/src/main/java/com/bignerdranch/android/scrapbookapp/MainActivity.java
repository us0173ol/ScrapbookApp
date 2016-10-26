package com.bignerdranch.android.scrapbookapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MainListCursorAdapter.ItemContentsChangedListener {

    private static final String TAG = "MAIN LIST ACTIVITY";
    private static final int REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE = 123;
    DatabaseManager mDatabaseManager;
    MainListCursorAdapter cursorListAdapter;

    private Bitmap mImage;
    private String mImagePath;
    ImageView mCameraPicture;

    private static int TAKE_PICTURE = 0;
    private static final String IMAGE_FILEPATH_KEY = "image filepath key";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseManager = new DatabaseManager(this);

        if (savedInstanceState != null){
            mImagePath = savedInstanceState.getString(IMAGE_FILEPATH_KEY);
        }
        Button add = (Button) findViewById(R.id.add_button);
        final Button searchCommentsButton = (Button) findViewById(R.id.search_comments_button);
        final EditText searchCommentsET = (EditText)findViewById(R.id.search_comments_edittext);
        final Button takePictureButton = (Button) findViewById(R.id.take_picture_button);
        final ListView itemsListView = (ListView) findViewById(R.id.pictures_comments_listview);
        final EditText commentsET = (EditText) findViewById(R.id.comments_edit_text);
        Cursor cursor = mDatabaseManager.getAllInfo();
        cursorListAdapter = new MainListCursorAdapter(this, cursor,true );
        itemsListView.setAdapter(cursorListAdapter);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //updateDate();TODO
                String commentsMade = commentsET.getText().toString();
                mDatabaseManager.addObject(commentsMade);
                cursorListAdapter.changeCursor(mDatabaseManager.getAllInfo());
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        mDatabaseManager.close();

    }
    @Override
    protected void onResume(){
        super.onResume();
        mDatabaseManager = new DatabaseManager(this);
    }

    private void takePhoto() {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check to see if there is a camera on this device.
        if (pictureIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(MainActivity.this, "Your device does not have a camera", Toast.LENGTH_LONG).show();
        }

        else {
            // Create a File object from the specified filename
            String imageFilename = "scrapbookapp_" + new Date().getTime();  //Create a unique filename including a timestamp

            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = null;
            Uri imageFileUri = null;

            try {

                imageFile = File.createTempFile(imageFilename, ".jpg", storageDirectory);
                Log.i(TAG, "image file " + imageFile);

                mImagePath = imageFile.getAbsolutePath();
                Log.i(TAG, "image file path  " + mImagePath);

                imageFileUri = FileProvider.getUriForFile(MainActivity.this, "com.bignerdranch.android.scrapbookapp", imageFile);

            } catch (IOException ioe) {
                Log.e(TAG, "Error creating file for photo storage", ioe);
                return;
            }

            //So if the file creation worked, should have a value for imageFileUri. Include this URI as an extra
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

            //And then request the camera is launched
            startActivityForResult(pictureIntent, TAKE_PICTURE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Log.d(TAG, "On Activity Result");

        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE){
            scaleBitmap();
            saveToMediaStore();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged");

        if(hasFocus && mImagePath != null){
            scaleBitmap();
            mCameraPicture.setImageBitmap(mImage);
        }
    }

    private void saveToMediaStore() {
        //Add image to device's MediaStore - this makes the image accessible to the
        //gallery app, and other apps that can read from the MediaStore

        //Need to request permission on Nougat and above

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            MediaStore.Images.Media.insertImage(getContentResolver(), mImage, "scrapbookapp", "Photo taken by scrapbookapp");
        } else {
            //This request opens a dialog box for the user to accept the permission request.
            // When the user clicks ok or cancel, the onRequestPermission method (below) is called with the results
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults ){
        if (requestCode == REQUEST_SAVE_IMAGE_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MediaStore.Images.Media.insertImage(getContentResolver(), mImage,"scrapbookapp", "Photo taken by ScrapbookApp" );
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outBundle){
        outBundle.putString(IMAGE_FILEPATH_KEY, mImagePath);
    }

    private void scaleBitmap(){
        int imageViewHeight = mCameraPicture.getHeight();
        int imageViewWidth = mCameraPicture.getWidth();

        BitmapFactory.Options bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mImagePath, bOptions);

        int pictureHeight = bOptions.outHeight;
        int pictureWidth = bOptions.outWidth;

        int scaleFactor = Math.min(pictureHeight/imageViewHeight, pictureWidth/imageViewWidth);

        bOptions.inJustDecodeBounds = false;
        bOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, bOptions);
        mImage = bitmap;
    }



    @Override
    public void notifyItemsChanged(int objectID, String newComments) {
        //TODO
    }
}
