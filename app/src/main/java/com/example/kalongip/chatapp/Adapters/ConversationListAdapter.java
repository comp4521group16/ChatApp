package com.example.kalongip.chatapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timothy on 8/4/2016.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder>{
    private List<RealmMessages> messages = new ArrayList<>(); // A List of messages passed from ConversationListFragment
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView sender, content;

        public ViewHolder(View itemView) {
            super(itemView);
            // Get reference to the view
            sender = (TextView) itemView.findViewById(R.id.sender);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    public ConversationListAdapter(List <RealmMessages> messages){
        this.messages = messages;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sender.setText(messages.get(position).getSender());
        holder.content.setText(messages.get(position).getContent());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversationlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
