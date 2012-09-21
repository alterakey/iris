package jp.co.monolithworks.il.iris;

import android.database.*;
import android.database.sqlite.*;
import android.content.Context;
import android.app.Application;
import android.widget.*;
import android.util.*;

import java.io.*;
import java.util.*;

public class DB{

    private Context mContext;
    private SQLiteDatabase mDb;

    public DB(Context context){
        mContext = context;
        Log.v("test"," " + mContext);
        DBHelper dh = new DBHelper(mContext);
        mDb = dh.getWritableDatabase();
    }

    public void insert(Map<String,String> data){
        Object[] fields = {data.get("jan_code"),data.get("category_name"),data.get("category_icon"),data.get("bar_code"),data.get("consume_limit")};
        //String query = "INSERT OR IGNORE INTO fridge (jan_code,category_name,category_icon,bar_code,consume_limit) VALUES (?,?,?,?,?)",fields;
        try{
            mDb.execSQL("BEGIN");
            mDb.execSQL("INSERT OR IGNORE INTO fridge (jan_code,category_name,category_icon,bar_code,consume_limit) VALUES (?,?,?,?,?)",fields);
        }catch(SQLiteException e){
            throw e;
        }finally{
            cleanup();
        }
    }

    public List<Map<String,String>> query(){
        String query = "SELECT jan_code,category_name,category_icon,bar_code,consume_limit FROM fridge_table ORDER BY consume_limit";
        Cursor c = null;
        List<Map<String,String>> items = new LinkedList<Map<String,String>>();
        try{
            c = mDb.rawQuery(query,null);
            for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                Map<String,String> item = new HashMap<String,String>();
                item.put("jan_code",c.getString(0));
                item.put("category_name",c.getString(1));
                item.put("category_icon",c.getString(2));
                item.put("bar_code",c.getString(3));
                item.put("consume_limit",c.getString(4));
                items.add(item);
            }
            return items;
        }catch(SQLiteException e){
            throw e;
        }finally {
            if(c != null){
                c.close();
            }
            cleanup();
        }
    }

    private void cleanup() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    private class DBHelper extends SQLiteOpenHelper{
        private final static String DB_NAME = "fridge_db";
        private final static int VERSION = 1;

        public DBHelper(Context context){
            super(context,DB_NAME,null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            String str;
            str = "create table fridge ";
            str += "(id integer primarykey autoincrement";
            str += ",category_name text";
            str += ",category_icon text";
            str += ",bar_code text";
            str += ",consume_limit text)";
            db.execSQL(str);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        }
    }

}