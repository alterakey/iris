package jp.co.monolithworks.il.iris;

import android.graphics.Bitmap;

public class ResultData {
	public static Bitmap thumbnailBitmap,iconBitmap;
	public static String categoryText,consumelimitText,thumbnailFileName;

    public ResultData(Bitmap thumbnailBitmap,Bitmap iconBitmap,String categoryText,String consumelimitText){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
    }
    
    public ResultData(Bitmap thumbnailBitmap,Bitmap iconBitmap,String categoryText,String consumelimitText,String thumbnailFileName){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
        this.thumbnailFileName = thumbnailFileName;
    }
    
    public ResultData(Bitmap thumbnailBitmap,String categoryText){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
    }

}
