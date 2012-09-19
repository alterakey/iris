package jp.co.monolithworks.il.iris;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultAdapter extends ArrayAdapter<ResultData>{
    LayoutInflater mInflater;

    public ResultAdapter(Context context,List<ResultData> objects){
        super(context,0,objects);
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = this.mInflater.inflate(R.layout.result_item,parent,false);
            holder = new ViewHolder();

            ImageView thumbnailView,iconView;
            TextView categoryText,consumelimitText;

            holder.thumbnailView = (ImageView)convertView.findViewById(R.id.thumbnail);
            holder.iconView = (ImageView)convertView.findViewById(R.id.icon);
            holder.categoryText = (TextView)convertView.findViewById(R.id.category);
            holder.consumelimitText = (TextView)convertView.findViewById(R.id.consumelimit);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ResultData data = (ResultData)getItem(position);

        holder.thumbnailView.setImageBitmap(data.thumbnailBitmap);
        holder.iconView.setImageBitmap(data.iconBitmap);
        holder.categoryText.setText(data.categoryText);
        holder.consumelimitText.setText(data.consumelimitText);

        return convertView;
    }
    
    class ViewHolder{
        ImageView thumbnailView,iconView;
        TextView categoryText,consumelimitText;
    }

}