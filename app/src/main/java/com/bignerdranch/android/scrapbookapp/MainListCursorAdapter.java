package com.bignerdranch.android.scrapbookapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by miked on 10/18/2016.
 */

public class MainListCursorAdapter extends CursorAdapter{

    private static final String TAG = "MAIN LIST CURSOR ADAPTER";
    ItemContentsChangedListener mItemContentsChangedListener;

    private static final int PICTURE_COL = 0;
    private static final int COMMENTS_COL = 1;
//    private static final int
//    private static final int
//    private static final int

    public MainListCursorAdapter(Context context, Cursor cursor, boolean autoReQuery){
       super(context, cursor, autoReQuery);

        if (context instanceof ItemContentsChangedListener){
            this.mItemContentsChangedListener = (ItemContentsChangedListener) context;

        }else{
            throw new RuntimeException(context.toString() + " must implement ItemContentsChangedListener");
        }

    }
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.pictures_comments_list_item, parent, false);
        return v;
    }
    public void bindView(View view, Context context, final Cursor cursor){
        //TODO FINISH bindView 
        ImageView pictureTaken = (ImageView) view.findViewById(R.id.item_list_picture_image_view);
        TextView commentsMade = (TextView) view.findViewById(R.id.item_list_comment_text_view);

        commentsMade.setText(cursor.getString(COMMENTS_COL));

    }

    interface ItemContentsChangedListener {
        void notifyItemsChanged(Image pictures, String newComments);
    }
}
