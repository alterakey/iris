package jp.co.monolithworks.il.iris;

import android.graphics.Bitmap;

public class ScanData {
	private static final ScanData instance = new ScanData();
	
	public static Bitmap thumbnail;
	public static String barcode;
	public static int scanDate;
	
	public static ScanData getScanData(){
		return instance;
	}
}
