package edu.csulb.android.photonotes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.csulb.android.photonotes.db.NotesContract.Notes;
import edu.csulb.android.photonotes.db.NotesDbHelper;

public class ListActivity extends AppCompatActivity {
    static SimpleCursorAdapter adapter = null;
    SQLiteDatabase dbConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.capture_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPhotoIntent = new Intent(ListActivity.this, AddPhotoActivity.class);
                ListActivity.this.startActivity(addPhotoIntent);
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view1);
        String[] columns = {Notes.COLUMN_NAME_CAPTION};
        int[] data = {android.R.id.text1};
        dbConnection = NotesDbHelper.getOpenConnection(getApplicationContext());
        Cursor cursor = dbConnection.rawQuery("SELECT _id," + Notes.COLUMN_NAME_CAPTION + " FROM " +
                Notes.TABLE_NAME, null);
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor ,columns, data, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewActivityIntent = new Intent(ListActivity.this, ViewPhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                viewActivityIntent.putExtras(bundle);
                ListActivity.this.startActivity(viewActivityIntent);

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Cursor cursor = dbConnection.rawQuery("SELECT _id," + Notes.COLUMN_NAME_CAPTION + " FROM " +
                Notes.TABLE_NAME, null);
        adapter.swapCursor(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.uninstall) {
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
            uninstallIntent.setData(Uri.parse("package:edu.csulb.android.photonotes"));
            startActivity(uninstallIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        NotesDbHelper.destroyOpenConnection();
    }
}
