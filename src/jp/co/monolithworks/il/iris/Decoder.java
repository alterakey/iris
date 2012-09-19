package jp.co.monolithworks.il.iris;

import android.hardware.Camera.Size;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.*;
import android.content.Context;
import java.io.IOException;
import android.util.Log;
import android.widget.Toast;
import android.os.Vibrator;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.widget.ImageView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import com.google.zxing.LuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.android.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.BarcodeFormat;

public class Decoder {

    private static final int TRY_PREVIEW = 5;
    private RectFactory mRectFactory = RectFactory.getRectFactory();
    private Context mContext;
    private String mResultText = null;
    private MultiFormatReader reader = null;

    public Decoder(){

    }

    //プレビュー画像からバーコード解析をする
    public Result barcodeDecoder(byte[] data,Context context){

        mContext = context;

        Result result = null;
        //シングルトンから読み込み
        int left = mRectFactory.finderLeftX;
        int top = mRectFactory.finderTopY;
        int width = mRectFactory.finderWidth;
        int height = mRectFactory.finderHeight;
        int previewWidth = mRectFactory.previewWidth;
        int previewHeight = mRectFactory.previewHeight;

        Log.w("barcodeDecode","left:"+left);
        Log.w("barcodeDecode","top:"+top);
        Log.w("barcodeDecode","width:"+width);
        Log.w("barcodeDecode","height:"+height);
        Log.w("barcodeDecode","previewWidth:"+previewWidth);
        Log.w("barcodeDecode","previewHeight:"+previewHeight);


        //引数（byte配列の画像データ、プレビューサイズの横幅、プレビューサイズの高さ、読み取りエリアの開始X座標、読み取りエリアの開始Y座標、読み取りエリアの横幅、読み取りエリアの高さ、反転の有無）
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
            data,previewWidth,previewHeight,left,top,width,height,false);

        //画像を変換（YUVからBITMAP）
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        reader = new MultiFormatReader();

        try{
            //画像から解析情報を取り出す
            result = reader.decode(binaryBitmap);

        }catch (Exception e){
            Log.w("onPreviewFrame","result false");
        }
        return result;
    }

    //プレビュー画像からビットマップへ変換
   public static final void decodeYUV420SP(int[] rgb,byte[] yuv420sp,int width,int height){
        final int frameSize = width * height;
        for (int j=0,yp = 0; j<height; j++){
            int uvp = frameSize + (j>>1)*width,u=0,v=0;
            for(int i = 0; i<width; i++ ,yp++){
                int y = (0xff & ((int)yuv420sp[yp])) -16;
                if(y<0) y=0;
                if((i & 1) == 0){
                v = (0xff & yuv420sp[uvp++]) -128;
                u = (0xff & yuv420sp[uvp++]) -128;
                }
            int y1192 = 1192 * y;
            int r = (y1192 +1634 * v);
            int g = (y1192 -833 * v -400 *u);
            int b = (y1192 +2066 *u);
            if(r < 0) r = 0;else if(r > 262143) r = 262143;
            if(g < 0) g = 0;else if(g > 262143) g = 262143;
            if(b < 0) b = 0;else if(b > 262143) b = 262143;
            rgb[yp] = 0xff000000 | ((r<<6) & 0xff0000) | ((g>>2) & 0xff00) | ((b>>10) & 0xff);
            }
        }
   }
}
