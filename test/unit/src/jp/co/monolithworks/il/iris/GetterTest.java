package jp.co.monolithworks.il.iris;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import android.widget.TextView;
import android.widget.EditText;
import java.util.Scanner;
import java.io.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;

public class GetterTest {
    @Test
    public void test000() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet req = new HttpGet("http://www.google.com/");
        InputStream is = client.execute(req).getEntity().getContent();
        String corpse = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        System.out.println(corpse);
    }
}
