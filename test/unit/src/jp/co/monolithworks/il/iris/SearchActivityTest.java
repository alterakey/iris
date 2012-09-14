package jp.co.monolithworks.il.iris;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import android.widget.TextView;
import android.widget.EditText;
import java.util.Scanner;
import java.io.*;

@RunWith(TestRunner.class)
public class MainActivityTest {
    @Test
    public void test000() {
		MainActivity o = new MainActivity();
     }

    @Test
    public void test001() throws IOException {
        InputStream is = new FileInputStream(new File("test/fixtures", "egg.txt"));
        String corpse = new Scanner(is, "MS932").useDelimiter("\\A").next();
        Robolectric.addPendingHttpResponse(200, corpse);

        //System.out.println(corpse);

        String response = new MainActivity.Getter("4902526-332251").get();

        System.out.println(response);

        assertTrue(true);
    }

}
