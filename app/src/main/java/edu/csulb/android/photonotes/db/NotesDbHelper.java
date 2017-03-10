package edu.csulb.android.photonotes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.csulb.android.photonotes.db.NotesContract.Notes;

/**
 * Created by vaibhavjain
 */

public class NotesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Notes.db";
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE IF NOT EXISTS " + Notes.TABLE_NAME + "( " + Notes.COLUMN_NAME_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Notes.COLUMN_NAME_CAPTION + " TEXT NOT NULL, "
                    + Notes.COLUMN_NAME_IMAGE_PATH + " TEXT NOT NULL)";

    private static final String DROP_TABLE_NOTES =
            "DROP TABLE IF EXISTS " + Notes.TABLE_NAME;

    private static NotesDbHelper dbHelper = null;
    private static SQLiteDatabase dbConnection = null;

    public NotesDbHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_NOTES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static SQLiteDatabase getOpenConnection(Context ctx){
        if(dbHelper == null){
            dbHelper = new NotesDbHelper(ctx);
        }
        if(dbConnection == null){
            dbConnection = dbHelper.getWritableDatabase();
            return dbConnection;
        }
        else if(!dbConnection.isOpen()){
            dbConnection = dbHelper.getWritableDatabase();
        }
        return dbConnection;
    }

    public static void destroyOpenConnection(){
        dbHelper.close();
        dbConnection.close();
    }
}
