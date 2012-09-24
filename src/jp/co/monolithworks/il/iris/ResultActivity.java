package jp.co.monolithworks.il.iris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ResultActivity extends Activity {

    private ScanData mScanData;
    private SQLiteDatabase mDb;
    private List<ResultData> mLists;
    private final int POSITION_NOT_DELETE = -1;
    public static final String SELECTED_ITEM_KEY = "ResultActivity_item_selected";
    public static final String SELECTED_ITEM_DELETE_KEY = "ResultActivity_item_delete";
    public static final String SELECTED_ADAPTER_DELETE_KEY = "ResultActivity_adapter_delete";

    private Context mContext = this;

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

    }

    @Override
    public void onResume(){
        super.onResume();

        mScanData = ScanData.getScanData();
        mLists = new ArrayList<ResultData>();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_search);
        Bitmap cabbage = BitmapFactory.decodeResource(getResources(), R.drawable.cabbage);

        if(mScanData.lists!=null){
            mLists = mScanData.lists;
            Log.w("resultActivity","mList is not null");
        }else{
           Log.w("resultActivity","mList is null");
        }

        ListView lv = (ListView)findViewById(R.id.result_listView);

        int position = POSITION_NOT_DELETE;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            position = extras.getInt("listDeletePosition");
        }

        if(position == POSITION_NOT_DELETE){
            lv.setAdapter(new ResultAdapter(this,mLists));
            Log.w("resultActivity","position is POSITION_NOT_DELETE");
        }else{
            ArrayAdapter<ResultData> adapter  = (ArrayAdapter<ResultData>)FridgeRegister.getState().get(ResultActivity.SELECTED_ADAPTER_DELETE_KEY);
            FridgeRegister.getState().remove(ResultActivity.SELECTED_ADAPTER_DELETE_KEY);
            ResultData item  = (ResultData)FridgeRegister.getState().get(ResultActivity.SELECTED_ITEM_DELETE_KEY);
            FridgeRegister.getState().remove(ResultActivity.SELECTED_ITEM_DELETE_KEY);
            adapter.remove(item);
            lv.setAdapter(adapter);
            lv.invalidateViews();
            Log.w("resultActivity","position is not POSITION_NOT_DELETE");
        }

        lv.setAdapter(new ResultAdapter(this,mLists));
        lv.setScrollingCacheEnabled(false);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) parent;

                ResultData item = (ResultData)listView.getItemAtPosition(position);
                ArrayAdapter<ResultData> adapter = (ArrayAdapter<ResultData>)listView.getAdapter();

                Intent intent = new Intent();
                intent.setClass(ResultActivity.this, DetailActivity.class);
                startActivity(intent);

                FridgeRegister.getState().put(SELECTED_ITEM_KEY,mLists.get(position));
                FridgeRegister.getState().put(SELECTED_ADAPTER_DELETE_KEY,adapter);
                FridgeRegister.getState().put(SELECTED_ITEM_DELETE_KEY,item);
                FridgeRegister.setListPosition(position);
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
