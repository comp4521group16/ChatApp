package com.example.kalongip.chatapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.kalongip.chatapp.Value.BitmapRotate;
import com.example.kalongip.chatapp.Value.Cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class SocketActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener {

    private static final String TAG = SocketActivity.class.getSimpleName();

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA = 10;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final String CHAT_FRAGMENT_TAG = "ChatFragment";
    private Realm realm;
    private RealmConfiguration realmConfig;
    public static Handler mHandler;
    private String mCurrentPhotoPath = null;
    PopupWindow popupWindow;
    boolean notConnected = false;

    private String receiver;
    private boolean goChatRoomDirectly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        goChatRoomDirectly = intent.getBooleanExtra("goChatRoomDirectly", false);
        Log.d(TAG, "receiver = " + receiver);
        Log.d(TAG, "goChatRoomDirectly = " + goChatRoomDirectly);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        if (frameLayout != null) {
            if (goChatRoomDirectly) {
                ChatFragment chatFragment = ChatFragment.newInstance(receiver);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment, CHAT_FRAGMENT_TAG).commit();
            } else {
                ConversationListFragment conversationListFragment = new ConversationListFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment).commit();
            }
        }
        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this).build();
        // Open the Realm for the UI thread.
        realm = Realm.getInstance(realmConfig);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: // Showing the image in full screen
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);
                        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.fullscreen, null);
                        ImageView image = (ImageView) container.findViewById(R.id.fullImage);
                        image.setImageBitmap((Bitmap) msg.obj);

                        //The popupWindow contain an ImageView showing the image
                        popupWindow = new PopupWindow(container);
                        popupWindow.setFocusable(true);
                        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

                        container.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_socket, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_camera) {
            // open the camera app
            //openCamera();
            if (notConnected == false)
                dispatchTakePictureIntent();
        } else if (id == R.id.action_gallery) {
            // open the gallery to choose photo
            if (notConnected == false){
                openGallery();
            }

        } else if (id == R.id.action_logout) {
            // logout
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            Cache cache = new Cache(getApplicationContext());
            cache.setLoggedIn(false);
            cache.clearUser();
            finish();
            startActivity(intent);
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_draw){
            Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openCamera() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    private void openGallery() {
        startActivityForResult(Intent.createChooser(new Intent().setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT), "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @TargetApi(19)
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
                ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
                fragment.sendImage(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Image captured
            Bitmap bitmap = null;
            Bitmap rotatedbm = null;
            try (InputStream is = new URL(mCurrentPhotoPath).openStream()) {
                bitmap = BitmapFactory.decodeStream(is);
                //minibm = ThumbnailUtils.extractThumbnail(bitmap, 640, 480);
                rotatedbm = BitmapRotate.rotate(bitmap, 90);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
            if (fragment == null) {
                Log.i(TAG, "Fragment is null");
            }
            fragment.sendImage(ThumbnailUtils.extractThumbnail(rotatedbm, 640, 480));

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onFragmentInteraction(boolean result) {
        Log.i(TAG, "Callback: " + result);
        notConnected = result;
    }
}
