package com.example.kalongip.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kalongip.chatapp.Handler.customHandler;
import com.pushbots.push.Pushbots;

public class SocketActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        // Pushbots setting
        Pushbots.sharedInstance().init(this);
        Pushbots.sharedInstance().setCustomHandler(customHandler.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_socket, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            return true;
        }else if (id == R.id.action_camera){
            // open the camera app
            openCamera();
        }else if (id == R.id.action_gallery){
            // open the gallery to choose photo
            openGallery();
        }else if (id == R.id.action_logout){
            // logout
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            Cache cache = new Cache(getApplicationContext());
//            cache.setUser(null);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openCamera(){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    private void openGallery(){
        startActivityForResult(Intent.createChooser(new Intent().setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT), "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            // Image captured
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat);
            fragment.sendImage(bitmap);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Thumbnail image
                Bitmap minibm = ThumbnailUtils.extractThumbnail(bitmap, 640, 480);
                ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat);
                fragment.sendImage(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
