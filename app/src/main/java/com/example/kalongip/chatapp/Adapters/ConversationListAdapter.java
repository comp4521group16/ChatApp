package com.example.kalongip.chatapp.Adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kalongip.chatapp.ChatFragment;
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timothy on 8/4/2016.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder>{
    private List<RealmMessages> messages = new ArrayList<>(); // A List of messages passed from ConversationListFragment
    private Context context;

    public ConversationListAdapter(Context context, List <RealmMessages> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversationlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sender.setText(messages.get(position).getSender());
        holder.content.setText(messages.get(position).getContent());
        holder.date.setText(messages.get(position).getDate().toString());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO set OnClick
                ChatFragment chatFragment = ChatFragment.newInstance("", "");
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, chatFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView sender;
        TextView content;
        TextView date;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            // Get reference to the view
            sender = (TextView) itemView.findViewById(R.id.receiver);
            content = (TextView) itemView.findViewById(R.id.content);
            date = (TextView) itemView.findViewById(R.id.sendDate);
            card = (CardView) itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
