package edu.csulb.android.photonotes.db;

import android.provider.BaseColumns;

/**
 * Created by vaibhavjain on 3/4/2017.
 */

public final class NotesContract {
    private NotesContract(){}

    public static class Notes implements BaseColumns{
        public static final String TABLE_NAME = "Notes";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_CAPTION = "Caption";
        public static final String COLUMN_NAME_IMAGE_PATH = "Image_Path";
    }
}
