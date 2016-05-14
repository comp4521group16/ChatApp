package com.example.kalongip.chatapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String JOIN = "join";
    private static final String OFFLINE = "offline";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String encodedImage = null;

    private EditText mInputMessageView;
    private RecyclerView mMessagesView;
    private RecyclerView.Adapter mAdapter;
    private static final String TAG = "ChatFragment";
//    private List<Message> mMessages = new ArrayList<>();
    private List<RealmMessages> messages = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private Realm realm;

    private Socket socket;
    private Cache cache;
    private User user;

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) { // Listen to incoming messages
            if(getActivity() == null){return;}
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
                }
            });
        }
    };

    {
        try {
            socket = IO.socket("http://192.168.1.60:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        socket.connect();
        socket.on("message", handleIncomingMessages);
        cache = new Cache(getContext());
        user = cache.getUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        joinSocket();
    }

    /**
     * This method Send the username obtained from shared preference to the socket to indicate online status
     * Called in onResume();
     */
    private void joinSocket() {
        JSONObject username = new JSONObject();
        try {
            username.put("username", user.getUsername());
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message, true);
        JSONObject sendText = new JSONObject();
        try{
            sendText.put("text", message);
            sendText.put("receiver", user.getUsername());
            socket.emit("message", sendText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(String message, boolean fromME) {
//        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).message(message).build());
        RealmMessages realmMessages= new RealmMessages(user.getUsername(), "kalong925@gmail.com", message, fromME, false, new Date());
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
        messages.add(new RealmMessages(user.getUsername(), "kalong925@gmail.com", imageString, fromME, true, new Date()));
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
                    sendData.put("receiver", user.getUsername());
                    sendData.put("image", encodedImage);
                    socket.emit("message", sendData);
                } catch (JSONException e) {

                }
            }
        });
        encode.execute();
        return encodedImage;
    }

    private void storeToLocalDB(RealmMessages realmMessages){
        realm = Realm.getInstance(getContext());
        realm.beginTransaction();
        realm.copyToRealm(realmMessages);
        realm.commitTransaction();

    }
    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
