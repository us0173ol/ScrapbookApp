package com.bignerdranch.android.scrapbookapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by miked on 10/18/2016.
 */

public class DatabaseManager {

    private Context mContext;
    private SQLHelper mHelper;
    private SQLiteDatabase db;

    protected static final String DB_NAME = "stuff and things";
    protected static final int DB_VERSION = 1;
    protected static final String DB_TABLE = "pics and comments";

    protected static final String ID_COL= "_id";
    protected static final String PICTURE_COL = "pictures";
    protected static final String COMMENTS_COL = "comments";
    protected static final String DATE_MODIFIED_COL = "date modified";

    private static final String DB_TAG = "DatabaseManager";
    private static final String SQLTAG = "SQLHelper";

    public DatabaseManager(Context context){
        this.mContext = context;

    }
    public void close(){
        mHelper.close();//closes the database
    }
    public Cursor getAllInfo(){
        Cursor cursor = db.query(DB_TABLE,null, null, null, null, null, DATE_MODIFIED_COL);
        return cursor;
    }
    //TODO add method to add pictures and comments(add exception handling)
public class SQLHelper extends SQLiteOpenHelper{
    public SQLHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createSQLbase = "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s IMAGE, %s TEXT)";
        String createSQL = String.format(createSQLbase, DB_TABLE, ID_COL, COMMENTS_COL );
        db.execSQL(createSQL);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
        Log.w(SQLTAG, "Upgrade table - drop and recreate it");
    }
}
}
