package jp.co.monolithworks.il.iris;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.util.*;
import android.view.*;
import android.net.http.AndroidHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import java.io.*;

import android.os.AsyncTask;

public class SearchActivity extends Activity
{
    EditText mEt;
    TextView mTv;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtop);

        mEt = (EditText)findViewById(R.id.edittext);
        mTv = (TextView)findViewById(R.id.text);
    }

    public static class LoadTask extends AsyncTask<Void, Void, Void> {
        private AndroidHttpClient client;
        private TextView mTextView;
        private Getter mGetter;
        private String mResponse;

        public LoadTask(Getter getter, TextView tv) {
            mTextView = tv;
        }

        @Override
        protected Void doInBackground(Void... args) {
            mResponse = mGetter.get();
            return null;
        }

        @Override
        protected void onPostExecute(Void ret) {
            mTextView.setText(mResponse + "");
            Log.v("search","" + mResponse);
        }
    }

    private void onClick(View v){
        if(v.getId() == R.id.button){

            new LoadTask(new Getter(mEt.getText().toString()), mTv).execute();
        }
    }

    public static class Getter {
        private AndroidHttpClient client;
        private String mQuery;
        private StringBuffer mStringBuffer = new StringBuffer();

        public Getter(String query) {
            mQuery = query;
        }

        public String get() {
            String jancode = mQuery;

            //client = AndroidHttpClient.newInstance("Lynx/2.8.5rel.1");
            client = AndroidHttpClient.newInstance("w3m/0.5.2");
            //DefaultHttpClient client = new DefaultHttpClient();
            HttpGet http = new HttpGet(String.format("http://www.google.co.jp/search?ie=Shift_JIS&hl=ja&source=hp&q=%s&btnG=Google+%%8C%%9F%%8D%%F5&gbv=1",jancode));
            //HttpGet http = new HttpGet("http://www.google.co.jp/search?ie=Shift_JIS&hl=ja&source=hp&q=test&btnG=Google+%8C%9F%8D%F5&gbv=1");
            //HttpGet http = new HttpGet("http://www.google.co.jp/search?ie=UTF-8&hl=ja&source=hp&q=test&btnG=Google+%E6%A4%9C%E7%B4%A2&gbv=1");

            try{
                InputStream is = client.execute(http).getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "MS932"));
                String str;
                while((str = br.readLine()) != null){
                    mStringBuffer.append(str);
                }
            } catch(IOException e) {
                Log.d("MA", "IOException!");
            }
            return mStringBuffer.toString();
        }
    }
}
