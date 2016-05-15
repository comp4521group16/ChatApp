package com.example.kalongip.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kalongip.chatapp.Adapters.ConversationListAdapter;
import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.Model.User;
import com.example.kalongip.chatapp.Value.Cache;

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
        Date date = new Date();
        for (int i = 0; i < 3; i++){
            RealmMessages msg1 = new RealmMessages(user.getUsername(), "abc@gmail.com", "hello", true, true, date);
            messages.add(msg1);
        }
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ConversationListAdapter(this.getContext(), messages);
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversationlist, container, false);
    }
}
