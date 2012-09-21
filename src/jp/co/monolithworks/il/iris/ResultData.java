package jp.co.monolithworks.il.iris;

import android.graphics.Bitmap;

public class ResultData {
	Bitmap thumbnailBitmap,iconBitmap;
    String categoryText,consumelimitText;

    public ResultData(Bitmap thumbnailBitmap,Bitmap iconBitmap,String categoryText,String consumelimitText){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
    }
    
    public ResultData(Bitmap thumbnailBitmap,String categoryText){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
    }

}
