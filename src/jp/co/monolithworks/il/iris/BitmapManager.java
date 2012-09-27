package jp.co.monolithworks.il.iris;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
        
        String directory = ConstantDefinition.directory;
        
        //画像を保存
        int[] rgb = new int[(previewWidth * previewHeight)];//ARGB8888の画素の配列
        String fileName = null;
        Bitmap bmp=null;
        
        Decoder decoder = new Decoder();

        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        Log.w("ScanActivity","mPreviewWidth"+previewWidth);
        Log.w("ScanActivity","mPreviewHeight"+previewHeight);
        
        Log.w("ScanActivity","filename:"+fileName);
        Log.w("ScanActivity","directory:"+directory);
        
            try{
            //ARGB8888でからのビットマップ作成
                bmp = Bitmap.createBitmap(previewWidth,previewHeight,Bitmap.Config.ARGB_8888);
                decoder.decodeYUV420SP(rgb,data,previewWidth,previewHeight);//変換
                //変換した画素からビットマップにセット
                bmp.setPixels(rgb,0,previewWidth,0,0,previewWidth,previewHeight);
                try{
                   //画像保存処理
                    FileOutputStream out = new FileOutputStream(directory+fileName);
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
        String directory = ConstantDefinition.directory;
        String fileName = null;
        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,null);
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true);
        try{
            //画像保存処理
        	FileOutputStream out = new FileOutputStream(directory+fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
        }catch(Exception e){
            Log.w("ScanActivity","itemPhoto save Exception");
        }
        return fileName;
    }
    
    public static Bitmap readBitmap(String fileName,Context context){
        //画像読み込み()
        String directory = ConstantDefinition.directory;
        Bitmap bm = null;
        try{
            FileInputStream in = new FileInputStream(directory+fileName);
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
    
    public static Boolean deleteBitmap(String fileName){
        String directory = ConstantDefinition.directory;
        try{
            File file = new File(directory+fileName);
            file.delete();
            return true;
        }catch(Exception e){
            
            return false;
        }
    }

}
