package jp.co.monolithworks.il.iris;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapManager {
	
    public static String saveBitmap(byte[] data,Context context,int previewWidth,int previewHeight){
        //画像を保存(data/data/package_name/files)
        int[] rgb = new int[(previewWidth * previewHeight)];//ARGB8888の画素の配列
        String fileName = null;
        Bitmap bmp=null;
        
        Decoder decoder = new Decoder();

        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        Log.w("ScanActivity","mPreviewWidth"+previewWidth);
        Log.w("ScanActivity","mPreviewHeight"+previewHeight);

	        try{
	            //ARGB8888でからのビットマップ作成
	            bmp = Bitmap.createBitmap(previewWidth,previewHeight,Bitmap.Config.ARGB_8888);
	            decoder.decodeYUV420SP(rgb,data,previewWidth,previewHeight);//変換
	            //変換した画素からビットマップにセット
	            bmp.setPixels(rgb,0,previewWidth,0,0,previewWidth,previewHeight);
	            try{
	                //画像保存処理
	                FileOutputStream out = context.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
	                bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
	                out.close();
	            }catch(Exception e){
	                Log.w("ScanActivity","itemPhoto save Exception");
	            }
	        }catch(Exception e){
	            Log.w("ScanActivity","itemPhoto create Exception");
	        }
        return fileName;
    }

    public static String pictureSaveBitmap(byte[] data,Context context,int previewWidth,int previewHeight){
        String fileName = null;
        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,null);
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true);
        try{
            //画像保存処理
            FileOutputStream out = context.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
        }catch(Exception e){
            Log.w("ScanActivity","itemPhoto save Exception");
        }
        return fileName;
    }
    
    public static Bitmap readBitmap(String fileName,Context context){
        //画像読み込み(data/data/package_name/files)
        Bitmap bm = null;
        try{
            FileInputStream in = context.openFileInput(fileName);
            BufferedInputStream binput = new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] w = new byte[1024];
            while (binput.read(w) >= 0){
                out.write(w,0,1024);
            }
            byte[] byteData = out.toByteArray();
            bm = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
            in.close();
            out.close();
        }catch(FileNotFoundException e){
            Log.w("ScanActivity","itemPhoto read fileNotFoundException");
        }catch(IOException e){
            Log.w("ScanActivity","itemPhoto read IOException");
        }
    	return bm;
    }

}
