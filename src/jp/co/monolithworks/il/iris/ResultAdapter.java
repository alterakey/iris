package jp.co.monolithworks.il.iris;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultAdapter extends ArrayAdapter<ResultData>{
    LayoutInflater mInflater;
    
    int meet,fish,vegetable,drink,fruit,ham;
    int count=100;

    public ResultAdapter(Context context,List<ResultData> objects){
        super(context,0,objects);
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        Resources res = getContext().getResources();
        meet = res.getColor(R.color.meet);
        fish = res.getColor(R.color.fish);
        vegetable = res.getColor(R.color.vegetable);
        drink = res.getColor(R.color.drink);
        fruit = res.getColor(R.color.fruit);
        ham = res.getColor(R.color.ham);
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
            holder.frameLayout = (FrameLayout)convertView.findViewById(R.id.f_layout);

            switch(count){
            case 0:
                holder.frameLayout.setBackgroundColor(meet);
                break;
            case 1:
                holder.frameLayout.setBackgroundColor(fish);
                break;
            case 2:
                holder.frameLayout.setBackgroundColor(vegetable);
                break;
            case 3:
                holder.frameLayout.setBackgroundColor(drink);
                break;
            case 4:
                holder.frameLayout.setBackgroundColor(fruit);
                break;
            case 5:
                holder.frameLayout.setBackgroundColor(ham);
                break;
            }
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ResultData data = (ResultData)getItem(position);

        holder.thumbnailView.setImageBitmap(data.thumbnailBitmap);
        holder.iconView.setImageBitmap(data.iconBitmap);
        holder.categoryText.setText(data.categoryText);
        holder.categoryText.setTypeface( Typeface.DEFAULT_BOLD, Typeface.BOLD );
        holder.consumelimitText.setText(data.consumelimitText);
        holder.consumelimitText.setTypeface( Typeface.DEFAULT_BOLD, Typeface.BOLD );
        return convertView;
    }

    class ViewHolder{
        ImageView thumbnailView,iconView;
        TextView categoryText,consumelimitText;
        FrameLayout frameLayout;
    }

}