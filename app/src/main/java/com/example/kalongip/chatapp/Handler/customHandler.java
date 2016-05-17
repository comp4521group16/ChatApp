package com.example.kalongip.chatapp.Handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kalongip.chatapp.Model.RealmMessages;
import com.example.kalongip.chatapp.SocketActivity;
import com.pushbots.push.PBNotificationIntent;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.PBConstants;

import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;

/**
 * This handler handles the action after receiving push notifications
 */
public class customHandler extends BroadcastReceiver
{
    private static final String TAG = "customHandler";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        Log.i(TAG, "action=" + action);
        // Handle Push Message when opened
        if (action.equals(PBConstants.EVENT_MSG_OPEN)) {
            //Check for Pushbots Instance
            Pushbots pushInstance = Pushbots.sharedInstance();
            if(!pushInstance.isInitialized()){
                Log.i(TAG, "Initializing Pushbots.");
                Pushbots.sharedInstance().init(context.getApplicationContext());
            }

            //Clear Notification array
            if(PBNotificationIntent.notificationsArray != null){
                PBNotificationIntent.notificationsArray = null;
            }

            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);
            Log.i(TAG, "User clicked notification with Message: " + PushdataOpen.get("message"));

            //Report Opened Push Notification to Pushbots
            if(Pushbots.sharedInstance().isAnalyticsEnabled()){
                Pushbots.sharedInstance().reportPushOpened( (String) PushdataOpen.get("PUSHANALYTICS"));
            }

            //Start lanuch Activity
            String packageName = context.getPackageName();
            //Intent resultIntent = new Intent(context.getPackageManager().getLaunchIntentForPackage(packageName));
            Intent resultIntent = new Intent(context, SocketActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

            resultIntent.putExtras(intent.getBundleExtra("pushData"));
            Pushbots.sharedInstance().startActivity(resultIntent);

            // Handle Push Message when received
        }else if(action.equals(PBConstants.EVENT_MSG_RECEIVE)){
            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_RECEIVE);
            Log.i(TAG, "User Received notification with Message: " + PushdataOpen.get("message"));
            String sender = (String) PushdataOpen.get("sender");
            String receiver = (String) PushdataOpen.get("receiver");
            String content = (String) PushdataOpen.get("content");
            boolean isPhoto;
            if(PushdataOpen.get("isPhoto").equals("true")){
                isPhoto = true;
            }else{
                isPhoto = false;
            }
            Log.i(TAG, "CustomFields: " + sender + " " + receiver + " " + content + " " + isPhoto);
            RealmMessages realmMessages = new RealmMessages(sender, receiver, content, false, isPhoto, new Date());
            // Add the message received to the local db
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            realm.copyToRealm(realmMessages);
            realm.commitTransaction();

        }
    }
}


