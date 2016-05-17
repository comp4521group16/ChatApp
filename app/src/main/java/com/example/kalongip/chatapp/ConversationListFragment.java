package com.example.kalongip.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kalongip.chatapp.Adapters.ConversationListAdapter;
import com.example.kalongip.chatapp.Model.RealmFriendList;
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Model.User;
import com.example.kalongip.chatapp.Value.Cache;
import com.example.kalongip.chatapp.Value.Const;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by timothy on 7/4/2016.
 */
public class ConversationListFragment extends Fragment{

    private static final String TAG = ConversationListFragment.class.getSimpleName();

    private Cache cache;
    private User user;

    private String searchName;

    ConversationListAdapter adapter;
    // A List contains all the messages related to the user
    List<RealmMessages> messages = new ArrayList<>();

    Realm realm;

    final Date date = new Date();


    public ConversationListFragment() {
        // Required empty public constructor
    }

    public static ConversationListFragment newInstance(String searchName){
        Log.d(TAG, "newInstance(String) called, searchName = " + searchName);
        ConversationListFragment conversationListFragment = new ConversationListFragment();
        Bundle extra = new Bundle();
        extra.putString("search", searchName);
        conversationListFragment.setArguments(extra);
        return conversationListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.searchName = bundle.getString("search");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cache = new Cache(getContext());
        user = cache.getUser();
        Log.d(TAG, "Current user: " + user.toString());

        if (searchName != null){
            //Call from SearchActivity
            final Firebase userRef = new Firebase(Const.FIREBASE_URL + "/users");
            Query queryRef = userRef.orderByChild("username").equalTo(searchName);
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Query called!" + dataSnapshot);
                    messages.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User users = snapshot.getValue(User.class);
                        RealmMessages msg = new RealmMessages(user.getUsername(), users.getUsername(), "hello", true, true, date);
                        messages.add(msg);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversationlist, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (searchName != null) {
            adapter = new ConversationListAdapter(messages, this.getContext(), true);
        }else {
            adapter = new ConversationListAdapter(messages, this.getContext(), false);
        }
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Call from SocketActivity
        if (searchName == null) {
            Log.d(TAG, "Friend list called!");
            messages.clear();
            realm = Realm.getInstance(getContext());
            RealmQuery query = new RealmQuery(getContext());
            RealmResults<RealmFriendList> results = query.retrieveFriendList();
            for (RealmFriendList fd: results){
                RealmMessages msg = new RealmMessages(user.getUsername(), fd.getaFriend(), "last msg", false, false, date);
                messages.add(msg);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
