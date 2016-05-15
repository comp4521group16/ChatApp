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
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Model.User;
import com.example.kalongip.chatapp.Value.Cache;
import com.example.kalongip.chatapp.Value.Const;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timothy on 7/4/2016.
 */
public class ConversationListFragment extends Fragment{

    private static final String TAG = ConversationListFragment.class.getSimpleName();

    private Cache cache;
    private User user;

    ConversationListAdapter adapter;
    // A List contains all the messages related to the user
    List<RealmMessages> messages = new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cache = new Cache(getContext());
        user = cache.getUser();
        Log.d(TAG, "Current user: " + user.toString());
//        Date date = new Date();
//        for (int i = 0; i < 3; i++){
//            RealmMessages msg1 = new RealmMessages(user.getUsername(), "abc@gmail.com", "hello", true, true, date);
//            messages.add(msg1);
//        }

        Firebase userRef = new Firebase(Const.FIREBASE_URL + "/users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User users = snapshot.getValue(User.class);
//                    Log.d(TAG, users.getUsername());
                    Date date = new Date();
                    RealmMessages msg = new RealmMessages(user.getUsername(), users.getUsername(), "hello", true, true, date);
                    messages.add(msg);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversationlist, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ConversationListAdapter(this.getContext(), messages);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
