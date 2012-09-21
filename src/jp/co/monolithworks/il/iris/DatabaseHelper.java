package jp.co.monolithworks.il.iris;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "fridgeregisterdb";
    private final static int DB_VER = 1;
    private final static String DB_PATH = "/data/data/jp.co.monolithworks.il.iris/databases/";
    
    private SQLiteDatabase mDatabase;
    private Context mContext;
    
    public DatabaseHelper(Context context){
    	super(context, DB_NAME, null, DB_VER);
    	this.mContext = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "";
        sql += "create table fridge_table (";
        sql += "id integer primary key autoincrement";
        sql += ",jan_code text ";
        sql += ",category_name text";
        sql += ",category_icon text";
        sql += ",bar_code text";
        sql += ",consume_limit text";
        sql += ")";
        db.execSQL(sql);
        Toast.makeText(mContext,"database oncreate",Toast.LENGTH_SHORT).show();
        Log.w("resultActivity","database oncreate");
    }
    
    public SQLiteDatabase openDataBase() throws SQLException {
    	String myPath = DB_PATH + DB_NAME;
    	mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	return mDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
    
    @Override  
    public synchronized void close() {  
        if(mDatabase != null)  
            mDatabase.close();  
      
        super.close();  
    }  
    
}
