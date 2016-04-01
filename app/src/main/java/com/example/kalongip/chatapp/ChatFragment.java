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
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String encodedImage = null;

    private EditText mInputMessageView;
    private RecyclerView mMessagesView;
    private RecyclerView.Adapter mAdapter;
    private static final String TAG = "ChatFragment";
    private List<Message> mMessages = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private Socket socket;

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    String imageText;

                    try{
                        Log.i(TAG, "Parse Text");
                        message = data.getString("message").toString();
                        addMessage(message);
                    } catch (JSONException e) {
                      //  return;
                    }
                    try {
                        Log.i(TAG, "Parse Image");
                        imageText = data.getString("image");
                        addImage(decodeImage(imageText));
                    } catch (JSONException e) {
                        //return;
                    }
                }
            });
        }
    };

    {
        try{
            socket = IO.socket("http://192.168.1.60:3000");
        }catch (URISyntaxException e){
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter (mMessages);
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
        addMessage(message);
//        JSONObject sendText = new JSONObject();
//        try{
//            sendText.put("text", message);
//            socket.emit("message", sendText);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        socket.emit("message", message);

    }

    private void addMessage(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).message(message).build());
        Log.d(TAG, message);
        mAdapter = new MessageAdapter(mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    public void sendImage(Bitmap bitmap)
    {
        Log.i(TAG, "sendImage");
        JSONObject sendData = new JSONObject();
        try{
            sendData.put("image", encodeImage(bitmap));
  //          Bitmap bmp = decodeImage(sendData.getString("image"));
            addImage(bitmap);
            socket.emit("message",sendData);
        }catch(JSONException e){

        }
    }

    private void addImage(Bitmap bmp){
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .image(bmp).build());
        mAdapter = new MessageAdapter( mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    private String encodeImage(Bitmap bitmap){
        encodeImageInBackground encode = new encodeImageInBackground(bitmap, new ImageEncodeCallback() {
            @Override
            public void onEncodeCompleted(String image) {
                encodedImage = image;
                Log.i(TAG, "Encoded Image: "+encodedImage);
            }
        });
        encode.execute();
        return encodedImage;
    }

    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data,Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }
    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    private class encodeImageInBackground extends AsyncTask <Void, Void, String>{
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
