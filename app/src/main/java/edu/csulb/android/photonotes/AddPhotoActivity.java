package edu.csulb.android.photonotes;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import edu.csulb.android.photonotes.db.NotesContract.Notes;
import edu.csulb.android.photonotes.db.NotesDbHelper;
import edu.csulb.android.photonotes.util.CustomToast;

import static android.R.attr.data;

public class AddPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PERMISSION = 2;
    ImageView thumbnail;
    EditText caption;
    Button captureButton;
    Random randomGenerator = new Random();
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        thumbnail = (ImageView) findViewById(R.id.image_thumbnail);
        caption = (EditText) findViewById(R.id.caption_edit_text);
        captureButton = (Button) findViewById(R.id.capture_image_button);
    }

    public void onClickTakePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageFile = null;
            try {
                imageFile = createImageFileToSave();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                if(isStoragePermissionGranted()) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private File createImageFileToSave() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Photo Notes");
        if(!dir.exists()){
            dir.mkdirs();
        }
        int fileNumber = randomGenerator.nextInt();
        if(fileNumber < 0){
            fileNumber = fileNumber * -1;
        }
        String fileName = fileNumber + ".jpg";
        File pic = new File(dir, fileName);
        return pic;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            thumbnail.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            refreshGallery();
            captureButton.setEnabled(false);
        }
    }

    private void refreshGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        AddPhotoActivity.this.sendBroadcast(mediaScanIntent);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //Since a new file will be created, we delete the one we created earlier
                imageFile.delete();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_PERMISSION);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_IMAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onClickTakePicture(getWindow().getDecorView().getRootView());
        }
        else if (requestCode == REQUEST_IMAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_DENIED){
            CustomToast.getToast(this, "STORAGE permission not granted. Cannot save the note!", CustomToast.LENGTH_LONG).show();
            finish();
        }
    }

    public void onClickSaveNote(View view){
        if(!caption.getText().toString().trim().isEmpty() && thumbnail.getDrawable() != null) {
            final String captionStr = caption.getText().toString();
            final String imageStr = imageFile.getAbsolutePath();
            AsyncTask dbTasks = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    SQLiteDatabase db = NotesDbHelper.getOpenConnection(getApplicationContext());
                    ContentValues row = new ContentValues();
                    row.put(Notes.COLUMN_NAME_CAPTION, captionStr);
                    row.put(Notes.COLUMN_NAME_IMAGE_PATH, imageStr);
                    db.insert(Notes.TABLE_NAME, null, row);
                    return null;
                }

                @Override
                protected void onPostExecute(Object obj){
                    CustomToast.getToast(AddPhotoActivity.this, "Note Saved!", CustomToast.LENGTH_SHORT).show();
                }
            };
            dbTasks.execute();
            finish();
        }
        else{
            CustomToast.getToast(this, "Both caption and image are required fields!", CustomToast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            if (imageFile != null) {
                if (imageFile.delete()) {
                    refreshGallery();
                }
            }
    }
}
