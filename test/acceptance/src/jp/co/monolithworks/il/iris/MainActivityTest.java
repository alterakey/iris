package jp.co.monolithworks.il.iris;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class jp.co.monolithworks.il.iris.MainActivityTest \
 * jp.co.monolithworks.il.iris.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super("jp.co.monolithworks.il.iris", MainActivity.class);
    }

}
