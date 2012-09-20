package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.TextView;

public class SettingActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        PackageInfo packageInfo = null;
        TextView tv = (TextView)findViewById(R.id.version_name);
        try {
                packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        tv.setText("fridge register : "+packageInfo.versionName);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_setting, menu);
        return true;
    }
}
