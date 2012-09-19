package jp.co.monolithworks.il.iris;

import android.os.Bundle;
import android.app.Activity;
import android.content.*;
import android.view.*;
import android.widget.*;

import java.util.*;

public class FridgeActivity extends Activity {

    private List<Map<String,String>> mConsumelimit_list;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //List<ConsumeLimit_Items> consumelimit_list = new List<consumelimit_list>();


        String[] category_name = {"野菜","肉","魚","加工食品","調味料"};
        String[] consumelimit = {"後１日","後2日","後3日","後4日","後5日"};

        List<Map<String,String>> consumelimit_list = new LinkedList<Map<String,String>>();

        for(int i = 0;i < 5 ; ++i){
            Map<String,String> m = new HashMap<String,String>();
            m.put("category",category_name[i]);
            m.put("consumelimit",consumelimit[i]);
            consumelimit_list.add(m);
        }

        ListView lv = (ListView)findViewById(R.id.listView1);
        //lv.setAdapter(new LimitAdapter(this,mConsumelimit_list));
        SimpleAdapter adapter = new SimpleAdapter(this,consumelimit_list,R.layout.result_item,new String[] {"category","consumelimit"},new int[] {R.id.category,R.id.consumelimit});
        lv.setAdapter(adapter);
    }

    private class LimitAdapter extends ArrayAdapter<List<Map<String,String>>> {
        private LayoutInflater inflater;
        private Activity activity;

        public LimitAdapter() {
          super(FridgeActivity.this, R.layout.result_item);
          activity = FridgeActivity.this;
        }

        public LimitAdapter(Context context,List<Map<String,String>> object) {
            super(context,0);
            this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            ImageView imageview1;
            ImageView imageview2;
            TextView textview1;
            TextView textview2;
            int position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String,String> limit_items = mConsumelimit_list.get(position);
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.result_item, null, false);
                holder = new ViewHolder();
                holder.position = position;
                holder.textview1 = (TextView) convertView.findViewById(R.id.category);
                holder.textview2 = (TextView) convertView.findViewById(R.id.consumelimit);
                holder.imageview1 = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.imageview2 = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
            }

            holder.textview1.setText(limit_items.get("category"));
            holder.textview2.setText(limit_items.get("consumelimit"));
            holder.imageview1.setImageResource(android.R.drawable.ic_dialog_alert);
            holder.imageview2.setImageResource(android.R.drawable.ic_dialog_dialer);
            //new ImageLoader(holder, position, card).executeParallel();
            return convertView;
        }
    }

    private class ConsumeLimit_Items{
        String category;
        String consumelimit;
        ImageView thumb;
        ImageView icon;

        public ConsumeLimit_Items(String category,String consumelimit,ImageView thumb,ImageView icon){
            this.category = category;
            this.consumelimit = consumelimit;
            this.thumb = thumb;
            this.icon = icon;
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
}