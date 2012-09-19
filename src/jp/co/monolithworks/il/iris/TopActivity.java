package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TopActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_top);
        
        Button scanButton = (Button)findViewById(R.id.barcodeButton);
        scanButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		Intent intent = new Intent();
        		intent.setClass(TopActivity.this,ScanActivity.class);
        		startActivity(intent);
        	}
        });

    Button fridgeButton = (Button)findViewById(R.id.fridgeButton);
    fridgeButton.setOnClickListener(new OnClickListener(){
    	@Override
    	public void onClick(View v){
    		Intent intent = new Intent();
    		intent.setClass(TopActivity.this,FridgeActivity.class);
    		startActivity(intent);
    	}
    });
}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_top, menu);
        return true;
    }
}
