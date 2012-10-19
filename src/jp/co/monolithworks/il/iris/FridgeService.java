package jp.co.monolithworks.il.iris;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class FridgeService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        setAlarm();
        
        return START_STICKY;
    }
    
    public static void setAlarm(){
        long currentTime = System.currentTimeMillis();
        // 翌日00:00:00の時間を設定
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Context context = FridgeRegister.getContext();
        Intent cutIntent = new Intent(context, ConsumeLimitCutService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, -1, cutIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
    
}
