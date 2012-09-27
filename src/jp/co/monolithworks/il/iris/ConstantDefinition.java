package jp.co.monolithworks.il.iris;

import android.os.Environment;

public class ConstantDefinition {
	public static String directory = Environment.getExternalStorageDirectory() + "/Android/data/"
            + FridgeRegister.getContext().getPackageName();
}
