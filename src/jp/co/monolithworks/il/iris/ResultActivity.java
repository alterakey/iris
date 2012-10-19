package jp.co.monolithworks.il.iris;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ResultActivity extends BaseActionbarSherlockActivity {

    private ScanData mScanData;
    private SQLiteDatabase mDb;
    private List<ResultData> mLists;
    private final int POSITION_NOT_DELETE = -1;
    public static final String SELECTED_ITEM_KEY = "ResultActivity_item_selected";
    public static final String SELECTED_ITEM_DELETE_KEY = "ResultActivity_item_delete";
    public static final String SELECTED_ADAPTER_DELETE_KEY = "ResultActivity_adapter_delete";
    private final static int REQUEST_ITEM = 0;
    private ListView mListView;
    private Context mContext = this;
    private ArrayAdapter<ResultData> mAdapter;
    private int mSelectListPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setTitle("商品登録");

        //ストレージのチェック
        String storageCheck = Environment.getExternalStorageState();
        if(!storageCheck.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"SDカードが使用出来ません。終了します。",Toast.LENGTH_SHORT).show();
            finish();
        }

        String directory = ConstantDefinition.directory;
        File file = new File(directory);
        if(!file.exists()){
            file.mkdir();
            Log.w("scanactivity","mkdir");
        }else{
        	Log.w("scanactivity","not mkdir");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mScanData = ScanData.getScanData();
        mLists = new ArrayList<ResultData>();

        mListView = (ListView)findViewById(R.id.result_listView);
        mListView.setCacheColorHint(Color.TRANSPARENT);

        if(mScanData.lists!=null){
            mLists = mScanData.lists;
        }else{
        }

        int position = POSITION_NOT_DELETE;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            position = extras.getInt("listDeletePosition");
        }
        setListView(position);

        
        
        View emptyView = (View)findViewById(R.id.listview_empty);
        mListView.setEmptyView(emptyView);

        if(mAdapter.getCount() == 0){
            LinearLayout ll = (LinearLayout)findViewById(R.id.result_linearlayout);
            ll.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent();
                    intent.setClass(ResultActivity.this,ScanActivity.class);
                    startActivity(intent);

                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(40);
                }
            });
        }
    }

    private void setListView(int position){

        if(position == POSITION_NOT_DELETE){
            mAdapter = new ResultAdapter(this,mLists);
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter  = (ArrayAdapter<ResultData>)FridgeRegister.getState().get(ResultActivity.SELECTED_ADAPTER_DELETE_KEY);
            FridgeRegister.getState().remove(ResultActivity.SELECTED_ADAPTER_DELETE_KEY);
            ResultData item  = (ResultData)FridgeRegister.getState().get(ResultActivity.SELECTED_ITEM_DELETE_KEY);
            FridgeRegister.getState().remove(ResultActivity.SELECTED_ITEM_DELETE_KEY);
            mAdapter.remove(item);
            mListView.setAdapter(mAdapter);
            mListView.invalidateViews();
            position = POSITION_NOT_DELETE;
        }
        mListView.setSelection(mSelectListPosition);
        mListView.setScrollingCacheEnabled(false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) parent;

                ResultData item = (ResultData)listView.getItemAtPosition(position);
                ArrayAdapter<ResultData> adapter = (ArrayAdapter<ResultData>)listView.getAdapter();

                Intent intent = new Intent();
                intent.setClass(ResultActivity.this, CategoryActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent,REQUEST_ITEM);

                FridgeRegister.getState().put(SELECTED_ITEM_KEY,mLists.get(position));
                FridgeRegister.getState().put(SELECTED_ADAPTER_DELETE_KEY,adapter);
                FridgeRegister.getState().put(SELECTED_ITEM_DELETE_KEY,item);
                FridgeRegister.setListPosition(position);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_ITEM && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            setListView();
            if (extras != null) {
                int position = extras.getInt("position");
                mSelectListPosition = position;
            }
            Toast.makeText(this, "戻りました。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        Intent intent = new Intent();
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            intent.setClass(ResultActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_fridge) {
            intent.setClass(ResultActivity.this, FridgeActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_scan) {
            intent.setClass(ResultActivity.this, ScanActivity.class);
             startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){

            int count = mAdapter.getCount();
            if(count > 0){
                fridgeMoveAskDialog(count);
                return true;
            }else{
                ResultActivity.this.finish();
                return true;
            }
        }
        return false;
    }

    public void applicationFinishDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setMessage(getText(R.string.dialog_finish_message))
                .setCancelable(false)
                .setPositiveButton(getText(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ResultActivity.this.finish();
                    }
                })
                .setNegativeButton(getText(R.string.dialog_negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    public void fridgeMoveAskDialog(final int count){
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setMessage(getText(R.string.dialog_fridge_message))
                .setCancelable(false)
                .setPositiveButton(getText(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(int i=0; i < count; i++){
                            ResultData rd = mAdapter.getItem(i);
                            rd.record_date = DateDecrement.setDate();
                            item_insert(rd);
                        }
                        mAdapter.clear();
                        Intent intent = new Intent();
                        intent.setClass(ResultActivity.this,FridgeActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton(getText(R.string.dialog_neutral_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ResultActivity.this.finish();
                    }
                })
                .setNegativeButton(getText(R.string.dialog_negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    public void item_insert(ResultData data){
        DB db = new DB(this);
        ContentValues cv = new ContentValues();
        cv.put("jan_code", data.jan_code);
        cv.put("category_name",data.categoryText);
        cv.put("bar_code", data.thumbnailFileName);
        cv.put("consume_limit",data.consumelimitText);
        cv.put("record_date", data.record_date);
        db.insert(cv);
    }

    public void setListView(){
        mLists = mScanData.lists;
        ListView mListView = (ListView)findViewById(R.id.result_listView);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mAdapter = new ResultAdapter(this,mLists);
        mListView.setAdapter(mAdapter);
    }
    
}
