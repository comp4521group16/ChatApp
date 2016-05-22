package com.example.kalongip.chatapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kalongip.chatapp.Callbacks.ImageEncodeCallback;
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Model.User;
import com.example.kalongip.chatapp.Value.Cache;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECEIVER = "receiver";
    private static final String INDEX = "index";
    private static final String JOIN = "join";
    private static final String OFFLINE = "offline";
    private static  final  String RETRIEVE = "retrievePhoto";
    // TODO: Rename and change types of parameters
    private String receiverName;

    private String encodedImage = null;

    private EditText mInputMessageView;
    private RecyclerView mMessagesView;
    private RecyclerView.Adapter mAdapter;
    private static final String TAG = "ChatFragment";
    //    private List<Message> mMessages = new ArrayList<>();
    private List<RealmMessages> messages = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private Realm realm;

    boolean syncSuccess = true;
    private ImageButton imageButton = null;
    private Socket socket;
    private Cache cache;
    private User user;
    int index = -1;

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) { // Listen to incoming messages
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String imageText;

                    try {
                        Log.i(TAG, "Parse Text");
                        message = data.getString("message").toString();
                        addMessage(message, false);
                    } catch (JSONException e) {
                        //  return;
                    }
                    try {
                        Log.i(TAG, "Parse Image");
                        imageText = data.getString("image");
                        addImage(imageText, false);
                    } catch (JSONException e) {
                        //return;
                    }
                    syncSuccess = true;
                }
            });
        }
    };
    /**
     * Handler to handle the case that the connection to socket is not established
     */
    private Emitter.Listener handleConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Cannot connect to socket");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disableSending();
                        mListener.onFragmentInteraction(true);
                        // Prompt the user of not connecting to the socket
                        Toast.makeText(getContext(), "Error connecting socket......", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    };

    private Emitter.Listener handleRegistration = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    joinSocket();
                    mListener.onFragmentInteraction(false);
                    enableSending();
                    if(syncSuccess = false){
                        retrievePendingImage();
                    }
                }
            });
        }
    };

    {
        try {
            socket = IO.socket("http://192.168.1.124:3000");
            socket.on("connect", handleRegistration);
            socket.on("connect_error", handleConnectionError);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String name, int index) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(RECEIVER, name);
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            receiverName = getArguments().getString(RECEIVER);
            index = getArguments().getInt(INDEX);
            Log.i(TAG, "Index: " + index);
        }
        Log.d(TAG, "Receiver: " + receiverName);
        socket.connect();
        socket.on("message", handleIncomingMessages);
        cache = new Cache(getContext());
        user = cache.getUser();
        initializeChatHistory(receiverName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(receiverName);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
         joinSocket();
        if(index > 0){
            syncSuccess = false;
            retrievePendingImage();
        }
    }

    private void retrievePendingImage(){
        Log.i(TAG, "retrievePendingImage()");
        JSONObject username = new JSONObject();
        try {
            username.put("receiver", user.getUsername());
            username.put("index", index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(RETRIEVE, username);
    }
    /**
     * This method Send the username obtained from shared preference to the socket to indicate online status
     * Called in onResume();
     */
    private void joinSocket() {
        Log.i(TAG, "joinSocket");
        JSONObject username = new JSONObject();
        try {
            username.put("username", user.getUsername());
            username.put("receiver", receiverName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(JOIN, username);
    }

    /**
     * This method Delete the username from the socket record to indicate offline status.
     * Called in onStop().
     */
    private void offlineSocket() {
        Log.i(TAG, "offlineSocket");
        JSONObject username = new JSONObject();
        try {
            username.put("username", user.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(OFFLINE, username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // mAdapter = new MessageAdapter(mMessages);
        mAdapter = new MessageAdapter(messages);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        mListener = (OnFragmentInteractionListener) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        imageButton = sendButton;
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        scrollToBottom();
    }


    private void disableSending() {
        if (imageButton != null) {
            imageButton.setEnabled(false);
        }
    }

    private void enableSending() {
        if (imageButton != null) {
            imageButton.setEnabled(true);
        }
    }

    private void sendMessage() {
        Log.i(TAG, "sendMessage");
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message, true);
        JSONObject sendText = new JSONObject();
        try {
            sendText.put("text", message);
            sendText.put("receiver", receiverName);
            sendText.put("sender", user.getUsername());
            sendText.put("isPhoto", "false");
            socket.emit("message", sendText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(String message, boolean fromME) {
//        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).message(message).build());
        RealmMessages realmMessages = null;
        if (fromME) {
            Log.i(TAG, "A text message sent to " + receiverName);
            realmMessages = new RealmMessages(user.getUsername(), receiverName, message, fromME, false, new Date());
        } else {
            Log.i(TAG, "A text message received from " + receiverName);
            realmMessages = new RealmMessages(receiverName, user.getUsername(), message, fromME, false, new Date());
        }
        storeToLocalDB(realmMessages);
        messages.add(realmMessages);
        Log.i(TAG, "Timestamp for the message: " + new Date().toString());
        Log.d(TAG, message);
//        mAdapter = new MessageAdapter(mMessages);
        mAdapter = new MessageAdapter(messages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    public void sendImage(Bitmap bitmap) {
        Log.i(TAG, "sendImage");
        encodeImage(bitmap);
    }

    private void addImage(String imageString, boolean fromME) {
        //       mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).image(bmp).build());
        RealmMessages realmMessages = null;
        if (fromME) {
            Log.i(TAG, "A photo sent to " + receiverName);
            realmMessages = new RealmMessages(user.getUsername(), receiverName, imageString, fromME, true, new Date());
        } else {
            Log.i(TAG, "A photo received from " + receiverName);
            realmMessages = new RealmMessages(receiverName, user.getUsername(), imageString, fromME, true, new Date());
        }
        storeToLocalDB(realmMessages);
        messages.add(realmMessages);
        //       mAdapter = new MessageAdapter(mMessages);
        mAdapter = new MessageAdapter(messages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    private String encodeImage(Bitmap bitmap) {
        encodeImageInBackground encode = new encodeImageInBackground(bitmap, new ImageEncodeCallback() {
            @Override
            public void onEncodeCompleted(String image) {
                encodedImage = image;
                addImage(encodedImage, true);
                Log.i(TAG, "Encoded Image: " + encodedImage);
                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("receiver", receiverName);
                    sendData.put("image", encodedImage);
                    sendData.put("sender", user.getUsername());
                    sendData.put("isPhoto", "true");
                    socket.emit("message", sendData);
                } catch (JSONException e) {

                }
            }
        });
        encode.execute();
        return encodedImage;
    }

    private void storeToLocalDB(RealmMessages realmMessages) {
        realm = Realm.getInstance(getContext());
        realm.beginTransaction();
        realm.copyToRealm(realmMessages);
        realm.commitTransaction();

    }

    /**
     * This class loads all the related chat history from local database and show it on screen
     */
    private void initializeChatHistory(String name) {
        // Query to retrieve the chat history involving the user and his friend
        RealmResults<RealmMessages> realmResults = new RealmQuery(getContext()).retrieveChatHistoryByUserName(name);
        for (int i = 0; i < realmResults.size(); i++) {
            messages.add(realmResults.get(i));
        }
        mAdapter = new MessageAdapter(messages);
        Log.i(TAG, "Initializing chat history" + " " + realmResults.size());
        mAdapter.notifyDataSetChanged();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Texter");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean notConnected);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.getItem(0).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(5).setVisible(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        offlineSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        Log.i(TAG, "onDestroy()");
    }

    /**
     * Performing the image encoding action in the background.
     */
    private class encodeImageInBackground extends AsyncTask<Void, Void, String> {
        Bitmap bitmap;
        ImageEncodeCallback imageEncodeCallback;

        public encodeImageInBackground(Bitmap bitmap, ImageEncodeCallback imageEncodeCallback) {
            this.bitmap = bitmap;
            this.imageEncodeCallback = imageEncodeCallback;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imageEncodeCallback.onEncodeCompleted(s);
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            String encodeImage = Base64.encodeToString(array, Base64.DEFAULT);
            return encodeImage;
        }
    }
}
