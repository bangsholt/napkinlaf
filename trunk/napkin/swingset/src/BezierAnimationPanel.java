/*
 * @(#)BezierAnimationPanel.java	1.14 04/07/26
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)BezierAnimationPanel.java	1.14 04/07/26
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BezierAnimationPanel
 *
 * @version 1.14 07/26/04
 * @author Jim Graham
 * @author Jeff Dinkins (removed dynamic setting changes, made swing friendly)
 */
class BezierAnimationPanel extends JPanel implements Runnable {

    final Runnable timer = new Runnable() {
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            try {
                while (running.get()) {
                    if (getSize().width > 0 && getSize().height > 0) {
                        prePaint();
                        SwingUtilities.invokeAndWait(BezierAnimationPanel.this);
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    };

    Color backgroundColor =  new Color(0,     0, 153);
    Color outerColor      =  new Color(255, 255, 255);
    Color gradientColorA  =  new Color(255,   0, 101);
    Color gradientColorB  =  new Color(255, 255,   0);

    public final int NUMPTS = 6;
    final float animpts[] = new float[NUMPTS * 2];
    final float deltas[] = new float[NUMPTS * 2];
    final float staticpts[] = {
	 50.0f,   0.0f,
	150.0f,   0.0f,
	200.0f,  75.0f,
	150.0f, 150.0f,
	 50.0f, 150.0f,
	  0.0f,  75.0f,
    };
    final float movepts[] = new float[staticpts.length];

    BufferedImage img = null;
    Dimension oldSize = null;

    final AtomicBoolean running = new AtomicBoolean(false);

    final BasicStroke solid = new BasicStroke(9.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 9.0f);
    final int rule = AlphaComposite.SRC_OVER;
    final AlphaComposite opaque = AlphaComposite.SrcOver;
    final AlphaComposite blend = AlphaComposite.getInstance(rule, 0.9f);
    final AlphaComposite set = AlphaComposite.Src;

    /**
     * BezierAnimationPanel Constructor
     */
    public BezierAnimationPanel() {
	addHierarchyListener(
	    new HierarchyListener() {
	       public void hierarchyChanged(HierarchyEvent e) {
		   if(isShowing()) {
		       start();
		   } else {
		       stop();
		   }
	       }
	   }
	);
	setBackground(getBackgroundColor());
        SwingUtilities.invokeLater(this);
    }

    public boolean isOpaque() {
        return true;
    }

    public Color getGradientColorA() {
	return gradientColorA;
    }

    public void setGradientColorA(Color c) {
	if(c != null) {
	    gradientColorA = c;
	}
    }

    public Color getGradientColorB() {
	return gradientColorB;
    }

    public void setGradientColorB(Color c) {
	if(c != null) {
	    gradientColorB = c;
	}
    }

    public Color getOuterColor() {
	return outerColor;
    }

    public void setOuterColor(Color c) {
	if(c != null) {
	    outerColor = c;
	}
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(Color c) {
	if(c != null) {
	    backgroundColor = c;
	    setBackground(c);
	}
    }

    public void start() {
        if (!running.compareAndSet(false, true))
            return;
	Dimension size = getSize();
	for (int i = 0; i < animpts.length; i += 2) {
	    animpts[i + 0] = (float) (Math.random() * size.width);
	    animpts[i + 1] = (float) (Math.random() * size.height);
	    deltas[i + 0] = (float) (Math.random() * 4.0 + 2.0);
	    deltas[i + 1] = (float) (Math.random() * 4.0 + 2.0);
	    if (animpts[i + 0] > size.width / 6.0f) {
		deltas[i + 0] = -deltas[i + 0];
	    }
	    if (animpts[i + 1] > size.height / 6.0f) {
		deltas[i + 1] = -deltas[i + 1];
	    }
	}
        new Thread(timer).start();
        System.out.println("Animation Started.");
    }

    public synchronized void stop() {
        if (!running.compareAndSet(true, false))
            return;
        System.out.println("Animation Stopped.");
    }

    public void animate(float[] pts, float[] deltas, int index, int limit) {
	float newpt = pts[index] + deltas[index];
	if (newpt <= 0) {
	    newpt = -newpt;
	    deltas[index] = (float) (Math.random() * 3.0 + 2.0);
	} else if (newpt >= (float) limit) {
	    newpt = 2.0f * limit - newpt;
	    deltas[index] = - (float) (Math.random() * 3.0 + 2.0);
	}
	pts[index] = newpt;
    }

    public void run() {
        repaint();
    }

    public void prePaint() {
	GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);

        Dimension size = getSize();
        BufferedImage img = this.img;
        if (img == null || !size.equals(oldSize)) {
            img = new BufferedImage(size.width, size.height,
                    BufferedImage.TYPE_INT_RGB);
            oldSize = size;
        }
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_DEFAULT);

        int i;
        int len = animpts.length;
        for (i = 0; i < len; i += 2) {
            animate(animpts, deltas, i + 0, size.width);
            animate(animpts, deltas, i + 1, size.height);
        }

        float prevx = animpts[len - 2];
        float prevy = animpts[len - 1];
        float curx = animpts[0];
        float cury = animpts[1];
        float midx = 0.5f * (curx + prevx);
        float midy = 0.5f * (cury + prevy);
        gp.moveTo(midx, midy);
        float x1, y1, x2, y2;
        for (i = 2; i <= len; i += 2) {
            x1 = 0.5f * (midx + curx);
            y1 = 0.5f * (midy + cury);
            prevx = curx;
            prevy = cury;
            if (i < len) {
                curx = animpts[i + 0];
                cury = animpts[i + 1];
            } else {
                curx = animpts[0];
                cury = animpts[1];
            }
            midx = 0.5f * (curx + prevx);
            midy = 0.5f * (cury + prevy);
            x2 = 0.5f * (prevx + midx);
            y2 = 0.5f * (prevy + midy);
            gp.curveTo(x1, y1, x2, y2, midx, midy);
        }
        gp.closePath();

        g2d.setComposite(set);
        g2d.setBackground(backgroundColor);
        g2d.clearRect(0, 0, size.width, size.height);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(outerColor);
        g2d.setComposite(opaque);
        g2d.setStroke(solid);
        g2d.draw(gp);

        Rectangle bounds = gp.getBounds();
        GradientPaint gradient = new GradientPaint(bounds.x, bounds.y,
                gradientColorA, bounds.x + bounds.width,
                bounds.y + bounds.height, gradientColorB, true);
        g2d.setPaint(gradient);
        g2d.setComposite(blend);
        g2d.fill(gp);
        g2d.dispose();

        this.img = img;
    }

    public void paint(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, 0, 0, null);
    }
}
