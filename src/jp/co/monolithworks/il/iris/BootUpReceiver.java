package jp.co.monolithworks.il.iris;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Intent serviceIntent = new Intent(context, FridgeService.class);
        context.startService(serviceIntent);
    }

}
