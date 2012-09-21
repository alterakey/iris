package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class DetailActivity extends Activity {

    private final static int REQUEST_ITEM = 0;
    private ResultData mResultData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int x = display.getWidth() * 8 / 10;
        int y = x * 6 / 10;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(x,y);
        lp.setMargins(0,40,0,10);

        mResultData = (ResultData)FridgeRegister.getState().get(ResultActivity.SELECTED_ITEM_KEY);
        FridgeRegister.getState().remove(ResultActivity.SELECTED_ITEM_KEY);

        Bitmap bmp = mResultData.thumbnailBitmap;
        ImageView thumb = (ImageView)findViewById(R.id.thumbnail);
        thumb.setLayoutParams(lp);
        thumb.setImageBitmap(bmp);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_ITEM && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
            }
            Toast.makeText(this, "戻りました。", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v){
        Intent intent = new Intent();
        if(v.getId() == R.id.categoryButton){
            intent.setClass(this,CategoryActivity.class);
            startActivityForResult(intent,REQUEST_ITEM);
        }else if(v.getId() == R.id.okButton){
            register();
            intent.setClass(this,ResultActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.cancelButton){
            intent.setClass(this,ResultActivity.class);
            startActivity(intent);
        }
    }

    private void register(){

        String jan_code = mResultData.categoryText;
        String bar_code = mResultData.thumbnailFileName;

        Toast.makeText(this, "database insert jan_code:"+jan_code, Toast.LENGTH_SHORT).show();

	    ContentValues values = new ContentValues();
	    values.put("jan_code", jan_code);
	    values.put("category_name","たまご");
	    values.put("category_icon", "123.jpg");
	    values.put("bar_code", bar_code);
	    values.put("consume_limit", "3");
	    DB db = new DB(this);
	    db.insert(values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
        case R.id.menu_settings:
            //intent.setClass(this, SettingActivity.class);
            //startActivity(intent);
            break;
        case R.id.menu_fridge:
            intent.setClass(this, FridgeActivity.class);
            startActivity(intent);
            break;
        }
        return true;
    }
}
