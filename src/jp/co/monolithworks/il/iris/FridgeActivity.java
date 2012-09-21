package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;

import java.util.*;

public class FridgeActivity extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);
        Button b = (Button)findViewById(R.id.scan_button);
        b.setVisibility(View.GONE);
        TextView tv = (TextView)findViewById(R.id.listview_empty);
        tv.setVisibility(View.GONE);

        List<ConsumeLimit_Items> consumelimit_list = new LinkedList<ConsumeLimit_Items>();

        String[] category_name = {"野菜","肉","魚","加工食品","調味料"};
        String[] consumelimit = {"後１日","後2日","後3日","後4日","後5日"};

        DB db = new DB(this);
        List<Map<String,String>> items = db.query();

        for(Map<String,String> item : items){
            ConsumeLimit_Items ci = new ConsumeLimit_Items();
            ci.category = item.get("category_name");
            ci.consumelimit = item.get("consume_limit");
            consumelimit_list.add(ci);
        }

        ListView lv = (ListView)findViewById(R.id.result_listView);
        lv.setAdapter(new LimitAdapter(this,consumelimit_list));
    }

    private class LimitAdapter extends ArrayAdapter<ConsumeLimit_Items> {
        private LayoutInflater inflater;
        private Activity activity;

        public LimitAdapter() {
          super(FridgeActivity.this, R.layout.result_item);
          activity = FridgeActivity.this;
        }

        public LimitAdapter(Context context,List<ConsumeLimit_Items> object) {
            super(context,0,object);
            this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            LinearLayout backcolor;
            ImageView imageview1;
            ImageView imageview2;
            TextView textview1;
            TextView textview2;
            int position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ConsumeLimit_Items limit_items = this.getItem(position);
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.result_item, null, false);
                holder = new ViewHolder();
                holder.position = position;
                holder.backcolor = (LinearLayout)findViewById(R.id.item_layout);
                holder.textview1 = (TextView) convertView.findViewById(R.id.category);
                holder.textview2 = (TextView) convertView.findViewById(R.id.consumelimit);
                holder.imageview1 = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.imageview2 = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
            }

            // switch(position){
            //case 1:
            //  holder.backcolor.setBackgroundColor(Color.parseColor("#aa2222"));
            //  break;
            //case 2:
            //  holder.backcolor.setBackgroundColor(Color.parseColor("#33dd72"));
            //  break;
            //case 3:
            //  holder.backcolor.setBackgroundColor(Color.parseColor("#4455bb"));
            //  break;
            //case 4:
            //  holder.backcolor.setBackgroundColor(Color.parseColor("#2222ee"));
            //  break;
            //}

            holder.textview1.setText(limit_items.category);
            holder.textview2.setText(limit_items.consumelimit);
            holder.imageview1.setImageResource(R.drawable.ncm_0188);
            holder.imageview2.setImageResource(R.drawable.cabbage);
            //new ImageLoader(holder, position, card).executeParallel();
            return convertView;
        }
    }

    private class ConsumeLimit_Items{
        String category;
        String consumelimit;
        ImageView thumb;
        ImageView icon;

        //public ConsumeLimit_Items(String category,String consumelimit,ImageView thumb,ImageView icon){
        public ConsumeLimit_Items(){
            //this.category = category;
            //this.consumelimit = consumelimit;
            //this.thumb = thumb;
            //this.icon = icon;
        }

        public String getCategory(){
            return category;
        }

        public String getConsumeLimit(){
            return consumelimit;
        }

        public ImageView getThumb(){
            return thumb;
        }

        public ImageView getIcon(){
            return icon;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fridge, menu);
        return true;
    }

    @Override
public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
        case R.id.menu_settings:
            //intent.setClass(this, SettingActivity.class);
            //startActivity(intent);
            break;
        case R.id.menu_delete:
            intent.setClass(this, DetailActivity.class);
            startActivity(intent);
            break;
        case R.id.menu_scan:
            intent.setClass(this, ScanActivity.class);
            startActivity(intent);
            break;
        }
        return true;
    }
}