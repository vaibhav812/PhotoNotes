package edu.csulb.android.photonotes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import edu.csulb.android.photonotes.db.NotesContract.Notes;
import edu.csulb.android.photonotes.db.NotesDbHelper;

public class ViewPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        TextView textView = (TextView) findViewById(R.id.text_view);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        Bundle bundle = this.getIntent().getExtras();
        long id = bundle.getLong("id");
        SQLiteDatabase db = NotesDbHelper.getOpenConnection(getApplicationContext());
        Cursor cursor = db.rawQuery("SELECT _id, " + Notes.COLUMN_NAME_CAPTION + ", " +Notes.COLUMN_NAME_IMAGE_PATH +
                " FROM " + Notes.TABLE_NAME + " where _id =" + String.valueOf(id), null);
        cursor.moveToFirst();
        textView.setText(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NAME_CAPTION)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        imageView.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NAME_IMAGE_PATH)), options));
    }
}
