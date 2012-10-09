package jp.co.monolithworks.il.iris;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class BaseActionbarSherlockActivity extends SherlockActivity{
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.actionbar_theme);
    }
}
