package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TopActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_top, menu);
        return true;
    }
}
