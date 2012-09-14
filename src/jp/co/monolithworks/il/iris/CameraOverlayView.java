package jp.co.monolithworks.il.iris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.Log;

public class CameraOverlayView extends View {

    private int mWidth,mHeight,mPreviewWidth,mPreviewHeight,mFinderLeftX,mFinderTopY,mFinderRightX,mFinderBottomY,mFinderWidth,mFinderHeight;

    public CameraOverlayView(Context context){
        super(context);
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        //ビューのサイズを取得
        //mWidth=w;
        //mHeight=h;
        mPreviewWidth = w;
        mPreviewHeight = h;



        //シングルトン
        RectFactory rectFactory = RectFactory.getRectFactory();
        //プレビュー画面の大きさをシングルトンから取得
        //mPreviewWidth = rectFactory.previewWidth;
        //mPreviewHeight = rectFactory.previewHeight;

        Log.w("cameraoverlay","mPreviewWidth"+mPreviewWidth);
        Log.w("cameraoverlay","mPreviewHeight"+ mPreviewHeight);

        Log.w("cameraoverlay","oldW" + oldw);
        Log.w("cameraoverlay","oldH" + oldh);

        //外枠描画のための計算
        mFinderLeftX = (mPreviewWidth - mPreviewHeight) / 2;
        mFinderTopY = mPreviewHeight / 4;
        mFinderRightX = (mPreviewWidth - mPreviewHeight) / 2 + mPreviewHeight;
        mFinderBottomY = ((mPreviewHeight/4)*3);
        mFinderWidth = mPreviewWidth - mFinderLeftX * 2;
        mFinderHeight = mPreviewHeight - mFinderTopY *2;

        //シングルトンに書き込み
        rectFactory.finderLeftX = mFinderLeftX;
        rectFactory.finderTopY = mFinderTopY;
        rectFactory.finderRightX = mFinderRightX;
        rectFactory.finderBottomY = mFinderBottomY;
        rectFactory.finderWidth = mFinderWidth;
        rectFactory.finderHeight = mFinderHeight;

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //背景色を設定(透明色）
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(100,0,0,0);

        //外枠を描画
        canvas.drawRect(0, 0, mFinderLeftX, mPreviewHeight, paint);
        canvas.drawRect(mFinderRightX, 0, mPreviewWidth, mPreviewHeight, paint);
        canvas.drawRect(mFinderLeftX, 0, mFinderRightX, mFinderTopY, paint);
        canvas.drawRect(mFinderLeftX, mFinderBottomY, mFinderRightX, mPreviewHeight, paint);

        //真ん中の線を描画
        int len = mPreviewHeight / 10;
        paint.setARGB(255,255,0,0);
        paint.setStrokeWidth(2);
        canvas.drawLine(mFinderLeftX + len, mPreviewHeight/2, mFinderRightX - len, mPreviewHeight/2, paint);

        /*
        //注意書きを描画
        paint.setARGB(255,255,255,255);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setTextSize(25);
        //XXX　位置を計算して表示できるようにしよう
        canvas.drawText("横向きにして、枠内にバーコード全体が入るようにしてください",10,mPreviewHeight/8,paint);
        */
    }
}
