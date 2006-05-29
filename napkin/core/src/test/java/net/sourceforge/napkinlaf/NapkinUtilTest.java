package net.sourceforge.napkinlaf;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import java.awt.*;

public class NapkinUtilTest extends TestCase {
    @SuppressWarnings({"FieldCanBeLocal"})
    private boolean testDone;

    private Throwable problem;

    private abstract class TestingLabel extends JLabel {
        public TestingLabel() {
            super("testCopySettings");
        }

        abstract void runTest(Graphics2D g);

        @Override
        public void paint(Graphics g) {
            try {
                problem = null;
                super.paint(g);
                runTest((Graphics2D) g);
            } catch (Throwable e) {
                problem = e;
            } finally {
                setTestDone();
            }
        }
    }

    public void testCopySettings() throws Throwable {
        runGraphicsTest(new TestingLabel() {
            void runTest(Graphics2D g) {
                Font font = new Font("Serif", Font.BOLD, 12);
                Stroke stroke = new BasicStroke(1.5f);
                Paint paint = new GradientPaint(0, 0, Color.BLUE, 100, 100,
                        Color.CYAN);
                Color bg = Color.WHITE;
                Color fg = Color.GRAY;

                double srcX = -100;
                double srcY = -200;
                Graphics2D src = NapkinUtil.copy(g);
                src.setFont(font);
                src.setStroke(stroke);
                src.setColor(fg);
                src.setBackground(bg);
                src.setPaint(paint);
                src.translate(srcX, srcY);
                src.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                assertEquals("font", font, src.getFont());
                assertEquals("stroke", stroke, src.getStroke());
                assertEquals("paint", paint, src.getPaint());
                assertEquals("fg", fg, src.getColor());
                assertEquals("bg", bg, src.getBackground());
                assertEquals("x", srcX, src.getTransform().getTranslateX());
                assertEquals("y", srcY, src.getTransform().getTranslateY());
                assertEquals("color render",
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                        src.getRenderingHint(
                                RenderingHints.KEY_COLOR_RENDERING));

                Graphics2D dst = NapkinUtil.copy(g);

                double dstX = 1;
                double dstY = 2;
                assertNotEquals("font", font, dst.getFont());
                assertNotEquals("stroke", stroke, dst.getStroke());
                assertNotEquals("paint", paint, dst.getPaint());
                assertNotEquals("fg", fg, dst.getColor());
                assertNotEquals("bg", bg, dst.getBackground());
                assertNotEquals("x", dstX, dst.getTransform().getTranslateX());
                assertNotEquals("y", dstY, dst.getTransform().getTranslateY());
                assertNotEquals("color render",
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                        dst.getRenderingHint(
                                RenderingHints.KEY_COLOR_RENDERING));

                NapkinUtil.copySettings(src, dst);
                dst.translate(dstX, dstY);

                assertEquals("font", font, dst.getFont());
                assertEquals("stroke", stroke, dst.getStroke());
                assertEquals("paint", paint, dst.getPaint());
                assertEquals("fg", fg, dst.getColor());
                assertEquals("bg", bg, dst.getBackground());
                assertEquals("x", dstX, dst.getTransform().getTranslateX());
                assertEquals("y", dstY, dst.getTransform().getTranslateY());
                assertEquals("color render",
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                        dst.getRenderingHint(
                                RenderingHints.KEY_COLOR_RENDERING));
            }
        });
    }

    private void runGraphicsTest(TestingLabel testLabel) throws Throwable {
        JFrame frame = new JFrame("NapkinUtilTest");
        try {
            frame.setLayout(new BorderLayout());
            frame.getContentPane().add(testLabel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
            waitForTest();
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    private synchronized void waitForTest() throws Throwable {
        testDone = false;
        while (!testDone)
            wait();
        if (problem != null)
            throw problem;
    }

    private synchronized void setTestDone() {
        testDone = true;
        notifyAll();
    }

    public static void assertNotEquals(String msg, Object expected,
            Object actual) {
        if (expected.equals(actual))
            fail(msg + ": both are " + expected);
    }

    public static void assertEquals(String msg, Object expected,
            Object actual) {
        Assert.assertEquals(msg, expected, actual);
    }
}