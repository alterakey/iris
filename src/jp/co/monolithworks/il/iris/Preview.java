package jp.co.monolithworks.il.iris;

import android.view.*;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
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

import java.io.BufferedOutputStream;
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

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    private static final int TRY_PREVIEW = 5;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private Camera mCamera;
    private String mResultText = null;
    private MultiFormatReader reader = null;
    private int mFalsePreviewCounts = 0;
    private boolean mAutoFocusFlag = false;
    private boolean mBarcodeModeFlag = false;
    private boolean mIsFocusRunning = false;
    private int mPreviewCounts = 0;
    private Context mContext = getContext();
    private Decoder mDecoder = new Decoder();
    private boolean isDecodeBitmapPreview = false;
    private int mLeft;
    private int mTop;
    private int mWidth;
    private int mHeight;
    private int mPreviewWidth;
    private int mPreviewHeight;
    
    //シングルトン
    private RectFactory mRectFactory = RectFactory.getRectFactory();
    private ScanData mScanData = ScanData.getScanData();

    Preview(Context context) {
        super(context);

        //surfaceViewインスタンス作成
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

        //holderを取得
        mHolder = mSurfaceView.getHolder();

        //holderにコールバックを追加
        mHolder.addCallback(this);

        //surfaceViewのtypeを設定
        //ARの場合は、「SURFACE_TYPE_NORMAL」を使用
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    //アプリケーションからコントロールに対してカメラをセット
    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            //プレビュー領域のサイズを計算し、requestLayout()を呼び出して、レイアウトを初期化
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCameta(Camera camera){
        setCamera(camera);
        try{
            camera.setPreviewDisplay(mHolder);
        }catch(IOException exception){
            Log.e("preview","IOException coused by setPreviewDisplay()",exception);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
        requestLayout();
        camera.setParameters(parameters);
    }

    //オートフォーカスモードがあるか判定、あればセットする
    public void isCheckAutoFocus(){
        Camera.Parameters cp = mCamera.getParameters();
        List<String> supportedList = cp.getSupportedFocusModes();
        if(supportedList != null){
            for(String supported : supportedList){
                Log.w("preview","scene mode supported list :" + supported);
                //接写モード
                if(supported.equals(Camera.Parameters.FOCUS_MODE_MACRO)){
                    cp.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                    mCamera.setParameters(cp);
                    mAutoFocusFlag = true;
                    //オートフォーカスード
                }else if(supported.equals(Camera.Parameters.FOCUS_MODE_AUTO)){
                    cp.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(cp);
                    mAutoFocusFlag = true;
                }
            }
        }
    }

    //シーンモードがあるか判定、あればバーコード読み取りモードをセット
    public void isCheckSceneMode(){
        Camera.Parameters cp = mCamera.getParameters();
        List<String> supportedList = cp.getSupportedSceneModes();
        if(supportedList != null){
            for(String supported : supportedList){
                Log.w("preview","scene mode supported list :" + supported);
                if(supported.equals(Camera.Parameters.SCENE_MODE_BARCODE)){
                    cp.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
                    mBarcodeModeFlag = true;
                }
            }
        }
    }

    public void getRectFactorySingleton(){
        //シングルトンから読み込み
        mLeft = mRectFactory.finderLeftX;
        mTop = mRectFactory.finderTopY;
        mWidth = mRectFactory.finderWidth;
        mHeight = mRectFactory.finderHeight;
        mPreviewWidth = mRectFactory.previewWidth;
        mPreviewHeight = mRectFactory.previewHeight;

        Log.w("fridge preview","mPreviewHeight:"+mPreviewHeight);
        Log.w("fridge preview","mPreviewWidth:"+mPreviewWidth);
    }

    //requestLayout()を呼ぶとonMesure()が呼び出される
    //リサイズの要求時に呼ばれるメソッド
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.

        //resolveSizeで幅と高さを求める(resplveSize(int size,int measureSpec))
        //制約と要求サイズを一致させる
        //getsSuggestedMinimumWidth()、getSuggestedMinimumHeight()は画面の最小幅と最小高さを求める
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        Log.w("cameraoverlay","onMeasure width:"+width);
        Log.w("cameraoverlay","onMeasure height:"+height);

        Log.w("cameraoverlay","onMeasure widthMeasureSpec:"+widthMeasureSpec);
        Log.w("cameraoverlay","onMeasure heightMeasureSpec:"+heightMeasureSpec);

        Log.w("cameraoverlay","onMeasure getSuggestedMinimumWidth():"+getSuggestedMinimumWidth());
        Log.w("cameraoverlay","onMeasure getSuggestedMinimumHeight():"+getSuggestedMinimumHeight());

        //最小幅と高さをsetMeasuredDimension()に与えてViewのサイズを設定
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            //getOptimalPreviewSize()でプレビュー領域のサイズを計算
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

            //シングルトンにプレビュー画面の大きさを書き込み
            mRectFactory.previewWidth = mPreviewSize.width;
            mRectFactory.previewHeight = mPreviewSize.height;
        }
    }

    //onLayout()はビューが子コントロールのサイズと位置を決定する際に呼び出される
    //引数はコントロールの座標（Left,Top,Right,Bottom)。ここからコントロールの幅と高さを求める
    //求めた値から、子コントロール（child）を画面の中心に配置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.

    	getRectFactorySingleton();

        Log.w("surface created","ok");

        try {
            if (mCamera != null) {

                //カメラのプレビュー画面を設定
                mCamera.setPreviewDisplay(holder);

                isCheckAutoFocus();
                Log.w("preview","mAutoFocusFlag:" + mAutoFocusFlag);
                isCheckSceneMode();
                Log.w("preview","mBarcodeModeFlag:" + mBarcodeModeFlag);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.w("preview","surface destroyed ok");

        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            //プレビューを停止
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        //アスペクト比で判定
        //final double ASPECT_TOLERANCE = 0.1;//標準
        final double ASPECT_TOLERANCE = 0.2;//緩くした
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        int left = mRectFactory.finderLeftX;
        int top = mRectFactory.finderTopY;
        int width = mRectFactory.finderWidth;
        int height = mRectFactory.finderHeight;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        Log.w("cameraoverlay","WtargetHeight:"+targetHeight);

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {

            Log.w("cameraoverlay","getOptimalPreviewSize mSupportedPreviewSizes.width:"+size.width);
            Log.w("cameraoverlay","getOptimalPreviewSize mSupportedPreviewSizes.height:"+size.height);

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
                Log.w("cameraoverlay","optionalSize.height:"+optimalSize.height);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                    Log.w("cameraoverlay","2nd optionalSize.height:"+optimalSize.height);
                }
            }
        }
        Log.w("cameraoverlay","return optionalSize.height:"+optimalSize.height);
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.

        Log.w("surface changed","ok");

        Camera.Parameters parameters = mCamera.getParameters();

        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        //parameters.setPreviewSize(480, 320);
        Log.w("cameraoverlay","mPreviewSize.width"+mPreviewSize.width);
        Log.w("camerapverlay","mPreviewSize.height"+mPreviewSize.height);

        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
        requestPreview();
        requestAutoFocus();

    }

    //オートフォーカスコールバック
    private Camera.AutoFocusCallback mAutoFocusListener = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera){
            //camera.autoFocus(null);//null入れるときもautofocus実行してしまう
            Log.w("onAutoFocus","auto focus callback");
            requestPreview();
            mIsFocusRunning = false;
        }
    };

    //オートフォーカス起動
    private void requestAutoFocus(){
        //if(mCamera != null || mAutoFocusFlag != false){
            mCamera.autoFocus(mAutoFocusListener);
            mIsFocusRunning = true;
            Log.w("autofocus","auto focus 実行");
            //}
    }

    //プレビュー画像取得
    private void requestPreview(){
        if(mCamera != null){
            mCamera.setPreviewCallback(previewCallback);
        }
    }

    //プレビューコールバック
    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback(){
        //毎フレーム実行
        public void onPreviewFrame(byte[] data, Camera camera){

            mPreviewCounts++;

            camera.setPreviewCallback(null);
            camera.setPreviewCallback(this);

            Log.w("autofocus","previewCount:"+mPreviewCounts);

            //解析を3フレームに1回の割合で行う
            if(mPreviewCounts % 3 == 0){
                //オートフォーカス中は解析しない　かつ　デコード中は解析しない
                if((mIsFocusRunning != true)&&(isDecodeBitmapPreview != true)){
                    decodeBitmapPreview(data);
                    //Log.w("preview","decode exec");
                }
             }
        }
    };


    //プレビュー画像からバーコード解析をして、画像を保存して表示
    public void decodeBitmapPreview(byte[] data){
        Result result = null;

        result = mDecoder.barcodeDecoder(data,mContext);

        if(result != null){
            isDecodeBitmapPreview = true;
            String contents = result.getText();
            if(!(contents.equals(mResultText))){
                mResultText = contents;

                //解析情報からバーコード情報を取り出し
                BarcodeFormat format = result.getBarcodeFormat();

                Log.w("autofocus","フォーマット:"+format);
                Log.w("autofocus:","コンテンツ:"+contents);

                Toast.makeText(mContext,String.format("format:%s , contens:%s",format,contents),Toast.LENGTH_LONG).show();

                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                
                String barcodeImageName = saveBitmap(data);
                Bitmap bmp = readBitmap(barcodeImageName);
                
                if(bmp != null){
                	mScanData.thumbnail = bmp;
                	mScanData.barcode = String.format("%s",format);
                	mScanData.barcode = contents;
                }

                //トースト表示
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bmp);
                Toast toast = new Toast(mContext);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(imageView);
                toast.show();
                
                //画像削除(data/data/package_name/files)
                //mContext.deleteFile(barcodeImageName);
            }
            isDecodeBitmapPreview = false;
        }else{
            //失敗したと判定
            if(mFalsePreviewCounts < TRY_PREVIEW){
                Log.w("autofocus","retry preview");
                mFalsePreviewCounts++;
                Log.w("autofocus","mFalsePreviewCounts:"+mFalsePreviewCounts);
                requestPreview();
            }else{
                //5回連続失敗したので、オートフォーカスからやり直し
                Log.w("autofocus","preview reset");
                Log.w("autofocus","mFalsePreviewCounts:"+mFalsePreviewCounts);
                mFalsePreviewCounts = 0;
                requestAutoFocus();
            }
        }
    }

    public void takePicture(){
        requestAutoFocus();

        //1秒待って撮影処理(autofocusが効く前に撮影してしまう為)
        Thread trd = new Thread(new Runnable(){
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                	
                }
                mCamera.takePicture(null,null,mPictureListener);
            }
         });
         trd.start();
    }

    private Camera.PictureCallback mPictureListener = new Camera.PictureCallback(){
        public void onPictureTaken(byte[] data,Camera camera){

            if(data != null){
                
                //String itemImageName = saveBitmap(data);
                //Bitmap bmp = readBitmap(itemImageName);

                Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length,null);
            
                //Log.w("ScanActivity","itemImageName:"+itemImageName);
                Log.w("ScanActivity","mPreviewWidth"+mPreviewWidth);
                Log.w("ScanActivity","mPreviewHeight"+mPreviewHeight);
                
                if(bmp != null){
                    mScanData.thumbnail = bmp;
                    mScanData.barcode = null;
                    mScanData.barcode = null;
                }
                
                //トースト表示
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bmp);
                Toast toast = new Toast(mContext);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(imageView);
                toast.show();
                
            }
            mCamera.startPreview();
            requestPreview();
            requestAutoFocus();
        }
    };
    
    public String saveBitmap(byte[] data){
    	
        //画像を保存(data/data/package_name/files)
        int[] rgb = new int[(mPreviewWidth * mPreviewHeight)];//ARGB8888の画素の配列
        String fileName = null;
        Bitmap bmp=null;
        
        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        
        Log.w("ScanActivity","mPreviewWidth"+mPreviewWidth);
        Log.w("ScanActivity","mPreviewHeight"+mPreviewHeight);

	        try{
	            //ARGB8888でからのビットマップ作成
	            bmp = Bitmap.createBitmap(mPreviewWidth,mPreviewHeight,Bitmap.Config.ARGB_8888);
	            mDecoder.decodeYUV420SP(rgb,data,mPreviewWidth,mPreviewHeight);//変換
	            //変換した画素からビットマップにセット
	            bmp.setPixels(rgb,0,mPreviewWidth,0,0,mPreviewWidth,mPreviewHeight);
	            try{
	                //画像保存処理
	                FileOutputStream out = mContext.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
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
    
    public Bitmap readBitmap(String fileName){
        //画像読み込み(data/data/package_name/files)
        Bitmap bm = null;
        try{
            FileInputStream in = mContext.openFileInput(fileName);
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
