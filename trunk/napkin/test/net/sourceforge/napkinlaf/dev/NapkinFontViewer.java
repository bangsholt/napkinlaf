package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.NapkinLookAndFeel;
import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.util.NapkinFont;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

/** This is a component that lets the user select a character subset to display. */
@SuppressWarnings({"WeakerAccess"})
public class NapkinFontViewer extends JPanel {
    private Character.Subset curSubset;

    private static final Character.Subset[] SUBSETS;
    private static final NapkinLookAndFeel LAF = new NapkinLookAndFeel();
    private static final int NUM_LEN = (int)
            Math.round(Math.log10(Character.MAX_CODE_POINT)) + 1;

    static {
        try {
            // There is no method that returns the known subsets
            List<Character.Subset> sublist = new ArrayList<Character.Subset>();
            Field[] fields = Character.UnicodeBlock.class.getFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) &&
                        Character.Subset.class.isAssignableFrom(
                                field.getType())) {
                    sublist.add((Character.Subset) field.get(null));
                }
            }
            SUBSETS = sublist.toArray(new Character.Subset[sublist.size()]);
            assert SUBSETS.length > 0;
            Arrays.sort(SUBSETS, new Comparator<Character.Subset>() {
                public int compare(Character.Subset o1, Character.Subset o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        } catch (IllegalAccessException e) {
            throw new SecurityException(e);
        }
    }

    private class Display extends JComponent {
        private Character.Subset last = null;
        private final String[] strings = new String[256];
        private final BitSet chars = new BitSet(strings.length);
        private int numStrings;

        private NapkinTheme theme;
        private Font fixedFont;

        Display() {
            theme = NapkinTheme.Manager.getCurrentTheme();
            Color color = theme.getPenColor();
            setBorder(BorderFactory.createLineBorder(color));
            fixedFont = theme.getFixedFont();
            float smallSize = fixedFont.getSize2D() * 0.8f;
            fixedFont = fixedFont.deriveFont(Font.ITALIC, smallSize);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @SuppressWarnings({"ObjectEquality"})
        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = NapkinUtil.defaultGraphics(g1, this);
            if (curSubset != last) {
                setStrings();
                last = curSubset;
            }

            Graphics numG = g.create();
            numG.setFont(fixedFont);
            numG.setColor(theme.getCheckColor());
            FontMetrics fixed = numG.getFontMetrics();

            int space = (int) Math.round(
                    fixed.getStringBounds(" ", numG).getWidth());

            int perColl = (int) Math.round(Math.ceil(numStrings / 8.0));
            int[] ch = new int[]{chars.nextSetBit(0)};
            for (int i = 0; i < strings.length; i++) {
                String num = strings[i];
                if (num == null)
                    break;

                int x = getX() + (i / perColl) * 75;
                int y = getY() + (i % perColl) * 16;
                numG.drawString(num, x, y);
                double w = fixed.getStringBounds(num, numG).getWidth();
                String str = new String(ch, 0, 1);
                g.drawString(str, x + (int) Math.round(w) + space, y);

                int nextCh = chars.nextSetBit(ch[0] + 1);
                if (nextCh < 0)
                    break;
                ch[0] = nextCh;
            }
        }

        @SuppressWarnings({"ObjectEquality"})
        private void setStrings() {
            Arrays.fill(strings, null);
            chars.clear();

            int c = Character.MIN_CODE_POINT;
getChars:
            for (int i = 0; i < strings.length; i++) {
                while (Character.UnicodeBlock.of(c) != curSubset ||
                        !displayChar(c)) {
                    if (c >= Character.MAX_CODE_POINT)
                        break getChars;
                    c++;
                }
                strings[i] = numString(c);
                chars.set(c);
                c++;
            }
            numStrings = chars.cardinality();
        }

        private boolean displayChar(int c) {
            return Character.isDefined(c) && !Character.isISOControl(c);
        }

        private String numString(int c) {
            StringBuilder sb = new StringBuilder(Integer.toHexString(c));
            while (sb.length() < NUM_LEN)
                sb.insert(0, ' ');
            return sb.toString();
        }
    }

    /** Creates a new <tt>NapkinFontViewer</tt>. */
    public NapkinFontViewer() {
        setLayout(new BorderLayout());

        final Display display = new Display();
        add(display, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());

        final JComboBox sublistBox = new JComboBox(SUBSETS);
        curSubset = Character.UnicodeBlock.BASIC_LATIN;
        sublistBox.setSelectedItem(curSubset);
        sublistBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                curSubset = (Character.Subset) sublistBox.getSelectedItem();
                display.repaint();
            }
        });
        controls.add(sublistBox);

        add(controls, BorderLayout.NORTH);
    }

    /**
     * Run this class as a program.
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(LAF);

        JFrame frame = new JFrame("Napkin Font Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new NapkinFontViewer(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}