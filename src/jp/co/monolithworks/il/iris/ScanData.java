package jp.co.monolithworks.il.iris;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;

public class ScanData {
    private static final ScanData instance = new ScanData();

    public static Bitmap thumbnail;
    public static String barcode;
    public static String scanData;
    public static List<ResultData> lists;
    
    public static ScanData getScanData(){
        return instance;
	}
}
