package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.util.*;

import java.util.*;

public class FridgeActivity extends Activity {

    protected List<ConsumeLimit_Items> consumelimit_list;
    private LimitAdapter adapter = null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);
        Button b = (Button)findViewById(R.id.scan_button);
        b.setVisibility(View.GONE);
        //ToutchListView tlv=(ToutchListView)getListView();

        consumelimit_list = new LinkedList<ConsumeLimit_Items>();
        item_read();


        adapter = new LimitAdapter(this,consumelimit_list);
        setListView();

        //tlv.setDropListener(onDrop);
		//tlv.setRemoveListener(onRemove);

    }

    private void item_read(){
        DB db = new DB(this);
        List<Map<String,String>> items = db.query();

        for(Map<String,String> item : items){
            ConsumeLimit_Items ci = new ConsumeLimit_Items();
            ci.category = item.get("category_name");
            ci.consumelimit = item.get("consume_limit");
            ci.jan_code = item.get("jan_code");
            consumelimit_list.add(ci);
        }
    }

    private void setListView(){
        final TextView tv = (TextView)findViewById(R.id.listview_empty);

        if(consumelimit_list.size() != 0){
            tv.setVisibility(View.GONE);
            ListView lv = (ListView)findViewById(R.id.result_listview);
            lv.setAdapter(adapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }else{
            tv.setText("冷蔵庫の中にはぞうが入っています。");
            tv.setVisibility(View.VISIBLE);
        }

    }

    private class LimitAdapter extends ArrayAdapter<ConsumeLimit_Items> {
        private LayoutInflater inflater;
        private Activity activity;
        public  List<ConsumeLimit_Items> consumelimit_list;
        public LimitAdapter() {
          super(FridgeActivity.this, R.layout.result_item);

          activity = FridgeActivity.this;
        }

        public LimitAdapter(Context context,List<ConsumeLimit_Items> object) {
            super(context,0,object);
            this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            consumelimit_list = object;
        }

        private class ViewHolder {
            ImageButton delete;
            ImageView imageview1;
            ImageView imageview2;
            TextView textview1;
            TextView textview2;
            int position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ConsumeLimit_Items limit_items = this.getItem(position);
            ViewHolder holder;


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.result_item, null, false);
                holder = new ViewHolder();
                holder.position = position;
                holder.delete = (ImageButton) convertView.findViewById(R.id.deleteButton);
                holder.textview1 = (TextView) convertView.findViewById(R.id.category);
                holder.textview2 = (TextView) convertView.findViewById(R.id.consumelimit);
                holder.imageview1 = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.imageview2 = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
            }
            holder.delete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        LimitAdapter la = new LimitAdapter();
                        ConsumeLimit_Items item = limit_items;
                        ViewHolder holder = new ViewHolder();
                        String[] code = {item.jan_code};
                        DB db = new DB(FridgeActivity.this);
                        db.delete(code);
                        item_read();
                        la.consumelimit_list.remove(holder.position);
                        if(la.consumelimit_list.size() != 0){
                            ListView listView = (ListView)FridgeActivity.this.findViewById(R.id.result_listview);
                            listView.setAdapter(new LimitAdapter(FridgeActivity.this,la.consumelimit_list));
                            //}else{
                            //tv.setText("冷蔵庫の中にはぞうが入っています。");
                            //tv.setVisibility(View.VISIBLE);
                        }
                    }
                });
            holder.textview1.setText(limit_items.category);
            holder.textview2.setText("後" + limit_items.consumelimit + "日");
            holder.imageview1.setImageResource(R.drawable.ncm_0188);
            holder.imageview2.setImageResource(R.drawable.cabbage);
            //new ImageLoader(holder, position, card).executeParallel();
            return convertView;
        }
    }

    private class ConsumeLimit_Items{
        String category;
        String consumelimit;
        String jan_code;
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

        public String getJan_code(){
            return jan_code;
        }

        public ImageView getThumb(){
            return thumb;
        }

        public ImageView getIcon(){
            return icon;
        }
    }





    //練習

    private ToutchListView.DropListener onDrop = new ToutchListView.DropListener(){
        @Override
        public void drop(int from , int to){
            ConsumeLimit_Items item = adapter.getItem(from);
            adapter.remove(item);
            adapter.insert(item,to);
        }
    };

    private ToutchListView.RemoveListener onRemove = new ToutchListView.RemoveListener(){
        @Override
        public void remove(int which){
            adapter.remove(adapter.getItem(which));
        }
    };







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
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
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