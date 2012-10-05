package jp.co.monolithworks.il.iris;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.GridView;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.content.*;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class CategoryActivity extends BaseActionbarSherlockActivity {

    private int mPosition;
    private int mMeet = Integer.parseInt(ConsumeLimit.MEET);
    private int mFish = Integer.parseInt(ConsumeLimit.FISH);
    private int mVegetable = Integer.parseInt(ConsumeLimit.VEGETABLE);
    private int mDrink = Integer.parseInt(ConsumeLimit.DRINK);
    private int mDairy_products= Integer.parseInt(ConsumeLimit.DAIRY_PRODUCTS);
    private int mFruit = Integer.parseInt(ConsumeLimit.FRUIT);
    private int mProcessed_food = Integer.parseInt(ConsumeLimit.PROCESSED_FOOD);
    private int mCondiment = Integer.parseInt(ConsumeLimit.CONDIMENT);
    private int mFrozen_food = Integer.parseInt(ConsumeLimit.FROZEN_FOOD);
    private int mAll = 0;
    private int isCategory;
    private ImageAdapter mImageAdapter;
    private Context mContext;

    ActionBar.OnNavigationListener mNavigationCallback = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            if (itemPosition == mMeet) {
                isCategory = mMeet;
                Log.w("category","mMeet;"+mMeet);
                mImageAdapter = new ImageAdapter(mContext);
                mImageAdapter.notifyDataSetChanged();
                reload();
            } else if (itemPosition == mFish) {
                
            } else if (itemPosition == mVegetable) {
                
            } else if (itemPosition == mDrink) {
                
            } else if (itemPosition == mDairy_products) {
                
            } else if (itemPosition == mFruit) {
                
            } else if (itemPosition == mProcessed_food) {
                
            } else if (itemPosition == mCondiment) {
                
            } else if (itemPosition == mFrozen_food) {
                
            } else if (itemPosition == mAll) {
                isCategory = mAll;
                mImageAdapter.notifyDataSetChanged();
            }
            return false;
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mPosition = getIntent().getIntExtra("position",0);
        
        mContext = (Context)this.getApplicationContext();

        GridView gridview = (GridView) findViewById(R.id.gridView);
        mImageAdapter = new ImageAdapter(this);
        gridview.setAdapter(mImageAdapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String category = ((TextView)v.findViewById(R.id.category)).getText().toString();
                String consumelimit = ((TextView)v.findViewById(R.id.consumelimit)).getText().toString();
                ResultData rd = ScanData.getScanData().lists.get(mPosition);
                rd.categoryText = category;
                rd.consumelimitText = consumelimit;
                ScanData.getScanData().lists.set(mPosition,rd);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        
        setListNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_category, menu);
        return true;
    }
        
    public void setListNavigation(){
        String[] data = {"すべて表示","肉類","魚介","野菜","飲料","乳製品","果実","加工品","調味料","冷凍品"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.sherlock_spinner_dropdown_item,data);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionbar.setListNavigationCallbacks(adapter, mNavigationCallback);
        
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setTitle("カテゴリー");
    }
        
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context mContext;
        private ConsumeLimit mConsumeLimit;

        public ImageAdapter(Context c) {
            mContext = c;
            mConsumeLimit = new ConsumeLimit();
            this.inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mConsumeLimit.limit.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            ImageView thumb;
            ImageView icon;
            TextView category;
            TextView consumelimit;
            int position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
          ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_category, parent, false);

                holder = new ViewHolder();
                holder.position = position;
                holder.category = (TextView) convertView.findViewById(R.id.category);
                holder.consumelimit = (TextView) convertView.findViewById(R.id.consumelimit);
                holder.thumb = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
            }
            if(isCategory == mMeet){
                Log.w("category","mMeet;"+mMeet);
                if(mConsumeLimit.limit[position][2].equals(ConsumeLimit.MEET)){
                    holder.category.setText(mConsumeLimit.limit[position][0] + "");
                    holder.consumelimit.setText(mConsumeLimit.limit[position][1] + "日");
                    Log.w("category","item is meet");
                }
            }else{
                holder.category.setText(mConsumeLimit.limit[position][0] + "");
                holder.consumelimit.setText(mConsumeLimit.limit[position][1] + "日");
                Log.w("category","item is not meet");
            }
            return convertView;
        }
    }
    
    public void reload(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
    
}

