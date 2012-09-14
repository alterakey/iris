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
    private boolean autoFocusFlag = false;
    private boolean barcodeModeFlag = false;
    private boolean isFocusRunning = false;
    private int mPreviewCounts = 0;
    private Context mContext = getContext();

    //シングルトン
    private RectFactory rectFactory = RectFactory.getRectFactory();

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
                    autoFocusFlag = true;
                    //オートフォーカスード
                }else if(supported.equals(Camera.Parameters.FOCUS_MODE_AUTO)){
                    cp.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(cp);
                    autoFocusFlag = true;
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
                    barcodeModeFlag = true;
                }
            }
        }
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
            rectFactory.previewWidth = mPreviewSize.width;
            rectFactory.previewHeight = mPreviewSize.height;
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

        Log.w("surface created","ok");

        try {
            if (mCamera != null) {

                //カメラのプレビュー画面を設定
                mCamera.setPreviewDisplay(holder);

                isCheckAutoFocus();
                Log.w("preview","autoFocusFlag:" + autoFocusFlag);
                isCheckSceneMode();
                Log.w("preview","barcodeModeFlag:" + barcodeModeFlag);
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
        final double ASPECT_TOLERANCE = 0.15;//緩くした
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        int left = rectFactory.finderLeftX;
        int top = rectFactory.finderTopY;
        int width = rectFactory.finderWidth;
        int height = rectFactory.finderHeight;

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

    }

    //オートフォーカスコールバック
    private Camera.AutoFocusCallback mAutoFocusListener = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera){
            //camera.autoFocus(null);//null入れるときもautofocus実行してしまう
            Log.w("onAutoFocus","auto focus callback");
            requestPreview();
            isFocusRunning = false;
        }
    };

    //オートフォーカス起動
    private void requestAutoFocus(){
        //if(mCamera != null || autoFocusFlag != false){
            mCamera.autoFocus(mAutoFocusListener);
            isFocusRunning = true;
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
                //オートフォーカス中は解析しない
                if(isFocusRunning != true){
                    barcodeDecorder(data);
                    //Log.w("preview","decode exec");
                }
             }

            //オートフォーカス中は解析しない
            //if(isFocusRunning != true){
            //barcodeDecorder(data);
            //}
        }
    };

    //プレビュー画像からバーコード解析をする
    public void barcodeDecorder(byte[] data){

        Result result = null;
        //シングルトンから読み込み
        int left = rectFactory.finderLeftX;
        int top = rectFactory.finderTopY;
        int width = rectFactory.finderWidth;
        int height = rectFactory.finderHeight;
        int previewWidth = mPreviewSize.width;
        int previewHeight = mPreviewSize.height;

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

        if(result != null){
            String contents = result.getText();
            if(!(contents.equals(mResultText))){
                mResultText = contents;

                //解析情報からバーコード情報を取り出し
                BarcodeFormat format = result.getBarcodeFormat();

                Log.w("autofocus","フォーマット:"+format);
                Log.w("autofocus:","コンテンツ:"+contents);

                Toast.makeText(mContext,String.format("format:%s , contens:%s",format,contents),Toast.LENGTH_SHORT).show();

                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);

                //画像を保存(data/data/package_name/files)
                int[] rgb = new int[(previewWidth * previewHeight)];//ARGB8888の画素の配列
                String fileName = null;
                try{
                    //ARGB8888でからのビットマップ作成
                    Bitmap bmp = Bitmap.createBitmap(previewWidth,previewHeight,Bitmap.Config.ARGB_8888);
                    decodeYUV420SP(rgb,data,previewWidth,previewHeight);//変換
                    //変換した画素からビットマップにセット
                    bmp.setPixels(rgb,0,previewWidth,0,0,previewWidth,previewHeight);
                    try{
                        //画像保存処理
                        //XXX画像ファイル名をちゃんする(a.jpg)

                        fileName = "iris" + String.valueOf(System.currentTimeMillis()) + ".jpg";

                        FileOutputStream out = mContext.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
                        bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
                        out.close();
                    }catch(Exception e){

                    }


                }catch(Exception e){

                }

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

                }catch(IOException e){

                }

                //トースト表示
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bm);
                Toast toast = new Toast(mContext);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(imageView);
                toast.show();

                //画像削除(data/data/package_name/files)
                mContext.deleteFile(fileName);

            }

        }else{
            //失敗したと判定
            if(mFalsePreviewCounts < TRY_PREVIEW){
                Log.w("autofocus","retry preview");
                mFalsePreviewCounts++;
                Log.w("autofocus","mFalsePreviewCounts:"+mFalsePreviewCounts);
                requestPreview();
            }else{
                //15回連続失敗したので、オートフォーカスからやり直し
                Log.w("autofocus","preview reset");
                Log.w("autofocus","mFalsePreviewCounts:"+mFalsePreviewCounts);
                mFalsePreviewCounts = 0;
                requestAutoFocus();
            }
        }
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
