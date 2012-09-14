package jp.co.monolithworks.il.iris;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.util.*;
import android.net.http.AndroidHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.*;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SearchXmlActivity extends Activity
{
    LinearLayout ll;
    TextView tview;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);


        tview = (TextView)findViewById(R.id.text);

        //parse();

        //pattern();

        //new LoadTask(tv).execute();
        //ll = (LinearLayout)findViewById(R.id.linearlayout);
        try {
            fileread();
         } catch (IOException e) {
         throw new RuntimeException(e);
            }
        //tview.setText(s);
    }

    private InputStream fileopen() throws IOException{
        return getAssets().open("search-fix.txt");
    }

    private void parse() throws IOException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(fileopen(), "MS932");

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("XV", "Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d("XV", "Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d("XV", "End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d("XV", "Text "+xpp.getText());
                }
                eventType = xpp.next();
            }
            Log.d("XV", "done");
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pattern(){
        Pattern p = Pattern.compile("(\\S+)");
        Matcher m = p.matcher("HEllo Hello hello Hello");
        String name = "";
        while(m.find()){
            Log.v("Pattern","通ってますか〜");
            name += m.group(1);
        }
        tview.setText(name);
    }

    private void fileread() throws IOException
    {

        InputStream fr = fileopen();
        try {
            String corpse = new Scanner(fr, "MS932").useDelimiter("\\A").next();//scannerチェック　inputstream one-liner
            Pattern p1 = Pattern.compile("<span class=.st.>(.*?)</span>");
            Pattern p2 = Pattern.compile("<a(.+?)>(.*?)</a>");

            Matcher m1 = p1.matcher(corpse);
            Matcher m2 = p2.matcher(corpse);
            String tag = "";
            //while(m2.find()){
                //tag +="a:attr\n" + m2.group(1) + "\n\nリンクタイトル\n" + m2.group(2) + "\n\n";
                //}
            tag += "\n";
            while(m1.find()){
                tag +="概要\n" + m1.group(1) + "\n\n";
            }
            tview.setText(tag);
            //return str;
        } finally {
            fr.close();
        }
    }
}
