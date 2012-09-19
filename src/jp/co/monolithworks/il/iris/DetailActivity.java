package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;

public class DetailActivity extends Activity {

    private final static int REQUEST_ITEM = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_ITEM && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
            }
        }
    }

    public void onClick(View v){
        Intent intent = new Intent();
        if(v.getId() == R.id.categoryButton){
            intent.setClass(this,CategoryActivity.class);
            startActivityForResult(intent,REQUEST_ITEM);
        }else if(v.getId() == R.id.registerButton){
            intent.setClass(this,ResultActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }
}
