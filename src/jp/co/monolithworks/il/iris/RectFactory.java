package jp.co.monolithworks.il.iris;

public class RectFactory {

    private static final RectFactory instance = new RectFactory();
    public static int finderLeftX,finderTopY,finderRightX,finderBottomY,finderWidth,finderHeight,previewWidth,previewHeight;

    private RectFactory(){

    }

    public static RectFactory getRectFactory(){
        return instance;
    }
}
