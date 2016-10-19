package com.bignerdranch.android.scrapbookapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN LIST ACTIVITY";
    DatabaseManager mDatabaseManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseManager = new DatabaseManager(this);

        final Button addCommentButton = (Button) findViewById(R.id.add_comment_button);
        final EditText searchCommentsET = (EditText)findViewById(R.id.search_comments_edittext);
        final Button addPictureButton = (Button) findViewById(R.id.add_picture_button);
        final ListView itemsListView = (ListView) findViewById(R.id.pictures_comments_listview);

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

}
