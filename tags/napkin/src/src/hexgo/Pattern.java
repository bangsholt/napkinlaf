// $Header$

package hexgo;

import java.awt.*;

public class Pattern implements HexgoConstants {
    private byte[] lines;
    private byte[] colors;
    private byte num;

    private static byte nextNum = 0;

    static Line line[] = {
        new Line(LEFT, UPPER_LEFT, Line.SHORT_CURVE), // 0
        new Line(UPPER_LEFT, UPPER_RIGHT, Line.SHORT_CURVE), // 1
        new Line(UPPER_RIGHT, RIGHT, Line.SHORT_CURVE), // 2
        new Line(RIGHT, LOWER_RIGHT, Line.SHORT_CURVE), // 3
        new Line(LOWER_RIGHT, LOWER_LEFT, Line.SHORT_CURVE), // 4
        new Line(LOWER_LEFT, LEFT, Line.SHORT_CURVE), // 5
        new Line(LEFT, UPPER_RIGHT, Line.LONG_CURVE), // 6
        new Line(UPPER_LEFT, RIGHT, Line.LONG_CURVE), // 7
        new Line(UPPER_RIGHT, LOWER_RIGHT, Line.LONG_CURVE), // 8
        new Line(RIGHT, LOWER_LEFT, Line.LONG_CURVE), // 9
        new Line(LOWER_RIGHT, LEFT, Line.LONG_CURVE), // 10
        new Line(LOWER_LEFT, UPPER_LEFT, Line.LONG_CURVE), // 11
        new Line(LEFT, RIGHT, Line.STRAIGHT_LINE), // 12
        new Line(UPPER_LEFT, LOWER_RIGHT, Line.STRAIGHT_LINE), // 13
        new Line(UPPER_RIGHT, LOWER_LEFT, Line.STRAIGHT_LINE), // 14
        new Line(LEFT, RIGHT, Line.STRAIGHT_LINE), // 15
        new Line(UPPER_LEFT, LOWER_RIGHT, Line.STRAIGHT_LINE), // 16
        new Line(UPPER_RIGHT, LOWER_LEFT, Line.STRAIGHT_LINE), // 17
    };

    private static Color color[] = {
        Color.white,
        Color.red,
        Color.blue,
    };

    private static Color darker[];

    static {
        darker = new Color[color.length];
        for (int i = 0; i < color.length; i++)
            darker[i] = color[i].darker();
    }

    public Pattern(int l1, int l2, int l3, int c1, int c2, int c3) {
        lines = new byte[]{(byte) l1, (byte) l2, (byte) l3};
        colors = new byte[]{(byte) c1, (byte) c2, (byte) c3};
        num = nextNum++;
    }

    public byte line(int i) {
        return lines[i];
    }

    public Color color(int i) {
        return color[colors[i]];
    }

    public Color darker(int i) {
        return darker[colors[i]];
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("Pattern#" + num + "\n");
        Color[] colors = new Color[6];
        for (int i = 0; i < lines.length; i++) {
            Line line = Pattern.line[line(i)];
            int edge1 = line.edge1();
            int edge2 = line.edge2();
            colors[edge1] = colors[edge2] = color(i);
            buf.append("    " + Line.TYPE_NAMES[line.type()] + " ");
            buf.append(DIRECTION_NAMES[edge1] + "->");
            buf.append(DIRECTION_NAMES[edge2] + "\n");
        }
        buf.append("    ");
        for (int i = 0; i < colors.length; i++) {
            Color color = colors[i];
            buf.append((color == Color.white ? 'W' : (color == Color.red
                    ? 'R' : (color == Color.blue ? 'B' : '?'))));
        }
        return buf.toString();
    }
}
