// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 2, 2002
 * Time: 3:40:40 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.awt.*;
import java.awt.geom.*;

/**
 * This class has a set of values precalculated for the current display scaling.
 * The fields are values scaled from a unit hexagon, and made final for further
 * efficiency.  You would create a new <CODE>Scale</CODE> object when the scale
 * changes. <P> The following picture shows the meaning of many of the values.
 * <BR><CENTER> <IMG SRC="TileImage.gif"> </CENTER><BR> The
 * <CODE>straight</CODE> values get you from the center of a tile to the
 * vertices of the straight edges.  The <CODE>angle</CODE> values get you from
 * the center of a tile to the angled vertices.  The <CODE>adj</CODE> marks
 * values to navigate to various adjacent tiles.  The <CODE>grid</CODE> values
 * define the size of the grid of either even or odd numbered rows. <P> Several
 * other values are precalculated simple to speed up the algorithm. Some
 * <CODE>halfFoo</CODE> values are just the value of <CODE>Foo</CODE> halved.
 */
public class Scale implements HexgoConstants {
    // constants for a unit (unscaled) tile
    private final static double STRAIGHT_X = Math.sqrt(3.0) / 2;
    private final static double STRAIGHT_Y = 0.5;
    private final static double ANGLE_X = 0.0;
    private final static double ANGLE_Y = 1.0;

    private final static double ADJ_STRAIGHT = STRAIGHT_X * 2;
    private final static double ADJ_ANGLE_X = STRAIGHT_X;
    private final static double ADJ_ANGLE_Y = STRAIGHT_Y + ANGLE_Y;

    /*
     * These are the coordinates for the vertices of a unit (unscaled) tile.
     * These include seven points so we can wrap around the edge.  It
     * makes algorithms easier to write.
     */
    private final static double[] X_POINTS = {
        -STRAIGHT_X, -ANGLE_X, STRAIGHT_X,
        STRAIGHT_X, ANGLE_X, -STRAIGHT_X,
        -STRAIGHT_X
    };
    private final static double[] Y_POINTS = {
        -STRAIGHT_Y, -ANGLE_Y, -STRAIGHT_Y,
        STRAIGHT_Y, ANGLE_Y, STRAIGHT_Y,
        -STRAIGHT_Y
    };

    /*
     * These are the distances to move to the adjacent tile in each direction.
     */
    private final static double[] X_ADJACENT = {
        -ADJ_STRAIGHT, -ADJ_ANGLE_X, ADJ_ANGLE_X,
        ADJ_STRAIGHT, ADJ_ANGLE_X, -ADJ_ANGLE_X,
    };
    private final static double[] Y_ADJACENT = {
        0, -ADJ_ANGLE_Y, -ADJ_ANGLE_Y,
        0, ADJ_ANGLE_Y, ADJ_ANGLE_Y,
    };

    private final static double GRID_X = X_ADJACENT[RIGHT];
    private final static double GRID_Y = 2 * Y_ADJACENT[LOWER_RIGHT];
    private final static Point.Double EVEN_ORIGIN = new Point.Double(0, 0);
    private final static Point.Double ODD_ORIGIN =
            new Point.Double(X_ADJACENT[UPPER_LEFT], Y_ADJACENT[UPPER_LEFT]);

    /** The scale for this <CODE>Scale</CODE> object. */
    public final double scale;
    /** Half the scale. */
    public final double halfScale;
    /** The stroke for drawing path lines. */
    public final Stroke pathStroke;
    /** The stroke for drawing down the middle of path lines. */
    public final Stroke midPathStroke;
    /** The width of the border stroke. */
    public final float borderStrokeWidth;
    /** The stroke for drawing the tile border. */
    public final Stroke borderStroke;
    /** The path that circumnavigates a tile. */
    public final GeneralPath tile;
    /** The vertices. */
    public final Point.Double[] points;
    /** Getting to the adjacent tile at angled vertices (see Figure 1). */
    public final double adjAngleX;
    /** Getting to the adjacent tile at angled vertices (see Figure 1). */
    public final double adjAngleY;

    /** Distance to straight edge's vertices (see Figure 1). */
    public final double straightX;
    /** Distance to straight edge's vertices (see Figure 1). */
    public final double straightY;

    /** Distance between tiles in same even/odd grid. */
    public final double gridX;
    /** Distance between tiles in same even/odd grid. */
    public final double gridY;
    /** Half grid. */
    public final double halfGridX;
    /** Half grid. */
    public final double halfGridY;
    /** Origin of the grid used for even numbered rows. */
    public final Point.Double evenOrigin;
    /** Origin of the grid used for odd numbered rows. */
    public final Point.Double oddOrigin;

    /**
     * A very small number used to determine if two values are essentially the
     * same.  We treat <CODE>v1 == v2</CODE> iff <CODE>v1 - epsilon <= v2 <= v1
     * + epsilon</CODE>.
     */
    public final double epsilon;

    /**
     * Creates a new <CODE>Scale</CODE> object scaled to the given value.
     *
     * @param scale The scaling factor to use.
     */
    public Scale(double scale) {
        this.scale = scale;
        halfScale = scale / 2;
        epsilon = scale * 0.001;
        pathStroke = new BasicStroke((float) (0.2 * scale));
        midPathStroke = new BasicStroke((float) (0.05 * scale));
        borderStrokeWidth = (float) (0.9 * scale / 20 + 0.65);
        borderStroke = new BasicStroke(borderStrokeWidth);
        straightX = STRAIGHT_X * scale;
        straightY = STRAIGHT_Y * scale;
        adjAngleX = ADJ_ANGLE_X * scale;
        adjAngleY = ADJ_ANGLE_Y * scale;

        gridX = GRID_X * scale;
        gridY = GRID_Y * scale;
        halfGridX = gridX / 2;
        halfGridY = gridY / 2;
        evenOrigin = new Point.Double(EVEN_ORIGIN.getX() * scale,
                EVEN_ORIGIN.getY() * scale);
        oddOrigin = new Point.Double(ODD_ORIGIN.getX() * scale,
                ODD_ORIGIN.getY() * scale);

        double[] xPoints = scale(X_POINTS);
        double[] yPoints = scale(Y_POINTS);
        points = new Point.Double[xPoints.length];
        tile = new GeneralPath();
        for (int i = 0; i < xPoints.length; i++) {
            double xp = xPoints[i];
            double yp = yPoints[i];
            points[i] = new Point.Double(xp, yp);
            if (i == 0)
                tile.moveTo((float) xp, (float) yp);
            else
                tile.lineTo((float) xp, (float) yp);
        }
        tile.closePath();
    }

    /**
     * Returns a new array that has all corresponding values in the input array
     * scaled.
     *
     * @param orig The original array.
     *
     * @return A scaled version of the array.
     */
    private double[] scale(double[] orig) {
        double[] nv = new double[orig.length];
        for (int i = 0; i < nv.length; i++)
            nv[i] = orig[i] * scale;
        return nv;
    }
}
