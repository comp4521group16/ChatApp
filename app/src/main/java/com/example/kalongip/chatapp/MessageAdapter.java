package com.example.kalongip.chatapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Message;

import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Value.DateFormat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;


/**
 * Created by kalongip on 21/3/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private List<RealmMessages> messages = new ArrayList<>();
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVE = 2;

/*    public MessageAdapter(List<Message> messages) {
        mMessages = messages;
    }*/
    public MessageAdapter(List<RealmMessages> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isFromMe()) {
            return TYPE_SEND;
        } else {
            return TYPE_RECEIVE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     /*   View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_message, parent, false);
        return new ViewHolder(v);*/
        switch (viewType) {
            case TYPE_SEND:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_message, parent, false));
            case TYPE_RECEIVE:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_received_message, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    /*    Message message = mMessages.get(position);
        holder.setMessage(message.getMessage());
        holder.setImage(message.getImage());*/
        final RealmMessages realmMessages = messages.get(position);
        if(realmMessages.isPhoto() == false){
            // If it is a text message
            holder.mImageView.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.VISIBLE);
            holder.setMessage(realmMessages.getContent());
        }else if(realmMessages.isPhoto() == true){
            // If it is a photo message
            holder.mMessageView.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the photo is clicked the photo is shown in a full screen mode by calling
                    // the Socket Activity to show a popup window
                    Message message = new Message();
                    message.obj = decodeImage(realmMessages.getContent());
                    message.what = 1;
                    SocketActivity.mHandler.sendMessage(message);
                }
            });

            holder.setImage(ThumbnailUtils.extractThumbnail(decodeImage(realmMessages.getContent()), 640, 480));
        }
        holder.setTime(realmMessages.getDate());
    }

    @Override
    public int getItemCount() {
       // return mMessages.size();
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mMessageView;
        private TextView mTimeView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mTimeView = (TextView) itemView.findViewById(R.id.timestamp);
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            if (null == message) return;
            mMessageView.setText(message);
        }

        public void setImage(Bitmap bmp) {
            if (null == mImageView) return;
            if (null == bmp) return;
            mImageView.setImageBitmap(bmp);
            mMessageView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }

        public void setTime(Date date){
            if(date != null){
                mTimeView.setText(DateFormat.getTime(date));
            }
        }
    }

    private Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bmp;
    }
}
