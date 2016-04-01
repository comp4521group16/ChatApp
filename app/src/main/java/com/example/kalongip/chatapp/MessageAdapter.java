package com.example.kalongip.chatapp;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by kalongip on 21/3/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;

    public MessageAdapter(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.setMessage(message.getMessage());
        holder.setImage(message.getImage());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageView;
        private TextView mMessageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            if(null == message) return;
            mMessageView.setText(message);
        }
        public void setImage(Bitmap bmp){
            if(null == mImageView) return;
            if(null == bmp) return;
            mImageView.setImageBitmap(bmp);
        }
    }
}
