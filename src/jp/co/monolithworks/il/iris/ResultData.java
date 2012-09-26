package jp.co.monolithworks.il.iris;

import android.graphics.Bitmap;

public class ResultData {
	public Bitmap thumbnailBitmap,iconBitmap;
	public String categoryText,consumelimitText,thumbnailFileName,jan_code;

    public ResultData(Bitmap thumbnailBitmap,Bitmap iconBitmap,String categoryText,String consumelimitText,String thumbnailFileName){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
        this.thumbnailFileName = thumbnailFileName;
    }
    
    public ResultData(Bitmap thumbnailBitmap,Bitmap iconBitmap,String categoryText,String consumelimitText,String thumbnailFileName,String jan_code){
        this.thumbnailBitmap = thumbnailBitmap;
        this.iconBitmap = iconBitmap;
        this.categoryText = categoryText;
        this.consumelimitText = consumelimitText;
        this.thumbnailFileName = thumbnailFileName;
        this.jan_code = jan_code;
    }
    
    public ResultData(Bitmap thumbnailBitmap,String categoryText){
        this.thumbnailBitmap = thumbnailBitmap;
        this.categoryText = categoryText;
    }

}
