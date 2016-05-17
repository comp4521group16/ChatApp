package com.example.kalongip.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.kalongip.chatapp.Model.RealmFriendList;
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Model.User;
import com.example.kalongip.chatapp.R;
import com.example.kalongip.chatapp.RealmQuery;
import com.example.kalongip.chatapp.SocketActivity;
import com.example.kalongip.chatapp.Value.Cache;
import com.example.kalongip.chatapp.Value.Const;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by timothy on 8/4/2016.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder>{
    private static final String TAG = ConversationListAdapter.class.getSimpleName();

    private List<RealmMessages> messages = new ArrayList<>(); // A List of messages passed from ConversationListFragment
    private Context context;
    static final String CHAT_FRAGMENT_TAG = "ChatFragment";
    private boolean isSearch;
    private Realm realm;

    public ConversationListAdapter(List<RealmMessages> messages, Context context, boolean isSearch) {
        this.messages = messages;
        this.context = context;
        this.isSearch = isSearch;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversationlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.receiver.setText(messages.get(position).getReceiver());
        holder.content.setText(messages.get(position).getContent());
//        holder.date.setText(messages.get(position).getDate().toString());
        holder.date.setText("1/1/2011");

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO set OnClick
                String receiver = (String) holder.receiver.getText();
                if (!isSearch){
                    ChatFragment chatFragment = ChatFragment.newInstance(receiver);
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, chatFragment, CHAT_FRAGMENT_TAG);
                    ft.addToBackStack(null).commit();
                } else {
                    //update friendlist in sharepref
                    Cache cache = new Cache(context);
                    User user = cache.getUser();
//                    Log.d(TAG, "Add friend by the user:" + user.toString());
//                    List<String> friends = user.getFriends();
//                    friends.add(receiver);
//                    user.setFriends(friends);
//                    cache.setUser(user);

                    //Get the current fdlist from realm
                    List<String> friends= new ArrayList<>();
                    RealmQuery query = new RealmQuery(context);
                    RealmResults<RealmFriendList> friendList = query.retrieveFriendList();
                    for (RealmFriendList fd: friendList) {
                        friends.add(fd.getaFriend());
                    }
                    //Add a new fd to realm
                    RealmFriendList fd = new RealmFriendList(friends.size(), receiver);
                    realm = Realm.getInstance(context);
                    realm.beginTransaction();
                    realm.copyToRealm(fd);
                    realm.commitTransaction();

                    friends.add(receiver);
                    //update friendlist in firebase
                    Firebase ref = new Firebase(Const.FIREBASE_URL + "/users");
                    ref.child("/" + user.getUid()).child("friends").setValue(friends);

                    ((AppCompatActivity) context).finish();
                    Intent intent = new Intent(context, SocketActivity.class);
                    intent.putExtra("goChatRoomDirectly", true);
                    intent.putExtra("receiver", receiver);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView receiver;
        TextView content;
        TextView date;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            // Get reference to the view
            receiver = (TextView) itemView.findViewById(R.id.receiver);
            content = (TextView) itemView.findViewById(R.id.content);
            date = (TextView) itemView.findViewById(R.id.sendDate);
            card = (CardView) itemView.findViewById(R.id.card);
        }
    }
}
