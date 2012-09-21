package jp.co.monolithworks.il.iris;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ResultActivity extends Activity {

    private ScanData mScanData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);
        
        Button scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(ResultActivity.this,ScanActivity.class);
                startActivity(intent);
            }
        });
        
        Log.w("resultActivity","onCreate");
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put("jan_code", "123");
	    values.put("category_name","たまご");
	    values.put("category_icon", "123.jpg");
	    values.put("barcode", "321.jpg");
	    values.put("consume_limit", "3");
	        
	    long ret;
	    try{
	        ret = db.insert("fridge_table", null, values);
	    }finally{
	        db.close();
	    }
	        
	    if(ret == -1){
	        Toast.makeText(this,"Insert失敗",Toast.LENGTH_SHORT).show();
	         Log.w("resultActivity","Insert失敗");
	    }else{
	        Toast.makeText(this,"Insert成功",Toast.LENGTH_SHORT).show();
	        Log.w("resultActivity","Insert成功");
	    }
    }
    
    @Override
    public void onResume(){
        super.onResume();
        
        mScanData = ScanData.getScanData();
        List<ResultData> lists = new ArrayList<ResultData>();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_search);
        Bitmap cabbage = BitmapFactory.decodeResource(getResources(), R.drawable.cabbage);

        ScanData scanData = ScanData.getScanData();
        Bitmap thumbnail = scanData.thumbnail;

        if(mScanData.lists!=null){
            lists = mScanData.lists;
        }
        
        ListView lv = (ListView)findViewById(R.id.result_listView);
        lv.setAdapter(new ResultAdapter(this,lists));
        lv.setScrollingCacheEnabled(false);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) parent;

                Intent intent = new Intent();
                intent.setClass(ResultActivity.this, DetailActivity.class);
                startActivity(intent);
                }
        });
        
        View emptyView = (View)findViewById(R.id.listview_empty);
        lv.setEmptyView(emptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_settings:
                intent.setClass(ResultActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_fridge:
                intent.setClass(ResultActivity.this, FridgeActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_scan:
                intent.setClass(ResultActivity.this, ScanActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
