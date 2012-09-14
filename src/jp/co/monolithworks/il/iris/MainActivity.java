package jp.co.monolithworks.il.iris;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;

import java.util.*;
import android.content.Context;
import java.io.IOException;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.google.zxing.LuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.android.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.BarcodeFormat;

public class MainActivity extends Activity
{
    private Preview mPreview;
    public int numberOfCameras;
    public int defaultCameraId;
    private Camera mCamera;
    private CameraOverlayView mCameraOverLayView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume(){
        super.onResume();

        //プレビュー画面を作成
        mPreview = new Preview(this);
        setContentView(mPreview);

        //オーバーレイ画面を作成
        //XXX
        //Galaxy Nexusでレイアウトが崩れる（左上に縮小表示される）
        //原因：アスペクト比が特殊（1196×720）実際は（1280×720）
        //理由：システムバーが領域を確保しているから
        mCameraOverLayView = new CameraOverlayView(this);
        addContentView(mCameraOverLayView,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

        View view = this.getLayoutInflater().inflate(R.layout.main,null);
        addContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));

        //カメラ数を取得
        numberOfCameras = Camera.getNumberOfCameras();

        //複数カメラがあった場合に、backカメラを指定
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++){
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK){
                defaultCameraId = i;
            }
        }

        try{
            mCamera = Camera.open();
            mPreview.setCamera(mCamera);
        }catch(Exception e){
            e.printStackTrace();
            String message = "カメラの起動に失敗しました。　\nカメラを使用する場合は、端末を再起動してください";
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mCamera != null){
            mPreview.setCamera(null);
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
