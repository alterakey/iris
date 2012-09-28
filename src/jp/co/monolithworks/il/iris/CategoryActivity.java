package jp.co.monolithworks.il.iris;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class CategoryActivity extends Activity {

	private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mPosition = getIntent().getIntExtra("position",0);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this));

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_category, menu);
        return true;
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
            holder.category.setText(mConsumeLimit.limit[position][0] + "");
            holder.consumelimit.setText(mConsumeLimit.limit[position][1] + "日");
            return convertView;
        }
    }
}
