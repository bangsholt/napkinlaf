package net.sourceforge.napkinlaf;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(MergedFontTest.class);
        return suite;
    }
}