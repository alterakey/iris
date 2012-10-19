package jp.co.monolithworks.il.iris;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ConsumeLimitCutService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Context context = FridgeRegister.getContext();
        DB db = new DB(context);
        db.update();
        
        FridgeService.setAlarm();
        
        return START_STICKY;
    }

}
