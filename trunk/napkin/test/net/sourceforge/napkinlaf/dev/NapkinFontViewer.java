package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.NapkinLookAndFeel;
import net.sourceforge.napkinlaf.NapkinTheme;
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

public class NapkinFontViewer extends JPanel {
    private Character.Subset curSubset;

    private static final Character.Subset[] SUBSETS;
    private static final NapkinLookAndFeel laf = new NapkinLookAndFeel();

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

    private class Display extends JComponent {
        private Character.Subset last = null;
        private final String[] strings = new String[256];
        private final BitSet chars = new BitSet(strings.length);
        private int numStrings;

        Display() {
            Color color = NapkinTheme.Manager.getCurrentTheme().getPenColor();
            setBorder(BorderFactory.createLineBorder(color));
        }

        @Override
        public Dimension getPreferredSize() {
            System.out.println("NapkinFontViewer$Display.getPreferredSize");
            return new Dimension(400, 400);
        }

        @Override
        public Dimension getMinimumSize() {
            System.out.println("NapkinFontViewer$Display.getMinimumSize");
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g1) {
            Graphics g = NapkinUtil.defaultGraphics(g1, this);
            if (curSubset != last) {
                setStrings();
                last = curSubset;
            }

            Graphics numG = g.create();
            NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
            Font fixedFont = theme.getFixedFont();
            float smallSize = fixedFont.getSize2D() * 0.8f;
            numG.setFont(fixedFont.deriveFont(Font.ITALIC, smallSize));
            numG.setColor(theme.getCheckColor());
            FontMetrics fixed = numG.getFontMetrics();

            int space = (int) Math.round(
                    fixed.getStringBounds(" ", numG).getWidth());

            int perColl = (int) Math.round(Math.ceil(numStrings / 8.0));
            char[] ch = new char[]{(char) chars.nextSetBit(0)};
            for (int i = 0; i < strings.length; i++) {
                String str = strings[i];
                if (str == null)
                    break;

                int x = getX() + (i / perColl) * 75;
                int y = getY() + (i % perColl) * 16;
                numG.drawString(str, x, y);
                double w = fixed.getStringBounds(str, numG).getWidth();
                g.drawChars(ch, 0, 1, x + (int) Math.round(w) + space, y);

                int nextCh = chars.nextSetBit(ch[0] + 1);
                if (nextCh < 0)
                    break;
                ch[0] = (char) nextCh;
            }
        }

        private void setStrings() {
            Arrays.fill(strings, null);
            chars.clear();

            char c = Character.MIN_VALUE;
getChars:
            for (int i = 0; i < strings.length; i++) {
                while (Character.UnicodeBlock.of(c) != curSubset ||
                        !Character.isDefined(c)) {
                    if (c >= Character.MAX_VALUE)
                        break getChars;
                    c++;
                }
                strings[i] = numString(c);
                chars.set(c);
                c++;
            }
            numStrings = chars.cardinality();
        }

        private String numString(char c) {
            StringBuilder sb = new StringBuilder(Integer.toHexString((int) c));
            while (sb.length() < 4)
                sb.insert(0, ' ');
            return sb.toString();
        }
    }

    /**
     * Run this class as a program.
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(laf);

        JFrame frame = new JFrame("Napkin Font Viewer");
        frame.add(new NapkinFontViewer(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}