package slides;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Slides implements SwingConstants {
    private GraphicsDevice device;
    private Window fullWin;
    private Frame fullFrame;
    private Frame frame;
    private Window curWin;
    private JLabel imageLabel;
    private List images;
    private int curImage;
    private Rectangle screenSize;
    private KeyListener keyActions;
    private MouseListener mouseActions;
    private int curMode;
    private int allMode;
    private AheadThread ahead;

    private static Object holder[] = new Object[30];
    private static int holderPos;

    private static GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();

    private final static int MAXIMIZE = 1;
    private final static int NORMAL = 2;
    private final static int DEFAULT = 3;

    private final static boolean DEBUG_LEADERS = false;

    public static void main(String[] args) throws Exception {
        boolean ask = (args.length > 0 && args[0].equals("-i"));
        String[] paths;
        if (!ask)
            paths = args;
        else {
            paths = new String[args.length - 1];
            System.arraycopy(args, 1, paths, 0, paths.length);
        }
        new Slides(paths, ask).execute();
    }

    private static class ImageInfo implements Comparable {
        final String path;
        String leader;
        String trailer;
        int sequence = -1;
        boolean dimValid;
        int rx, ry;
        SoftReference rawImageIconRef;
        SoftReference scaledImageIconRef;
        float scale;

        ImageInfo(String path) {
            this.path = path;
        }

        void setLeader(String leader) {
            if (leader == null)
                throw new NullPointerException("leader");
            this.leader = leader;
            if (leader == path)
                trailer = path;
            else {
                trailer = path.substring(leader.length());
                sequence = 0;
                for (int i = 0; i < trailer.length(); i++) {
                    char ch = trailer.charAt(i);
                    if (!Character.isDigit(ch))
                        break;
                    else
                        sequence = 10 * sequence + Character.getNumericValue(ch);
                }
            }
        }

        public int hashCode() {
            return leader.hashCode() ^ sequence;
        }

        public boolean equals(Object other) {
            if (other instanceof ImageInfo)
                return path.equals(((ImageInfo) other).path);
            return false;
        }

        public int compareTo(Object other) {
            ImageInfo that = (ImageInfo) other;
            if (this == that)
                return 0;
            int c = leader.compareTo(that.leader);
            if (c != 0)
                return c;
            return sequence - that.sequence;
        }

        synchronized boolean isValid() {
            return (getWidth() > 0 && getHeight() > 0);
        }

        boolean sameSequence(ImageInfo that) {
            return (this.leader == that.leader);
        }

        synchronized ImageIcon rawImageIcon() {
            ImageIcon rawImageIcon = null;
            if (rawImageIconRef != null)
                rawImageIcon = (ImageIcon) rawImageIconRef.get();
            if (rawImageIcon == null) {
                rawImageIcon = new ImageIcon(path, path);
                rx = rawImageIcon.getIconWidth();
                ry = rawImageIcon.getIconHeight();
                dimValid = true;
                rawImageIconRef = new SoftReference(rawImageIcon);
            }
            hold(rawImageIcon);
            return rawImageIcon;
        }

        synchronized int getWidth() {
            if (dimValid)
                return rx;
            else
                return rawImageIcon().getIconWidth();
        }

        synchronized int getHeight() {
            if (dimValid)
                return ry;
            else
                return rawImageIcon().getIconHeight();
        }

        synchronized ImageIcon scaledImageIcon(float scale) {
            if (scale != this.scale) {
                scaledImageIconRef = null;
                this.scale = scale;
            }

            ImageIcon scaledImageIcon = null;
            if (scaledImageIconRef != null)
                scaledImageIcon = (ImageIcon) scaledImageIconRef.get();
            if (scaledImageIcon == null) {
                ImageIcon raw = rawImageIcon();
                if (scale == 1.0f)
                    scaledImageIcon = raw;
                else {
                    Image scaledImage = raw.getImage().getScaledInstance(
                            scale(raw.getIconWidth()),
                            scale(raw.getIconHeight()),
                            ~Image.SCALE_FAST
                    );
                    scaledImageIcon = new ImageIcon(scaledImage, path);
                }
                scaledImageIconRef = new SoftReference(scaledImageIcon);
            }
            if (scale != 1.0f)
                hold(scaledImageIcon);
            return scaledImageIcon;
        }

        private int scale(int val) {
            return Math.round(val * scale);
        }

        public String toString() {
            return "ImageInfo{" + path +
                    (sequence < 0 ? "" : "[" + sequence + "]") + "}";
        }
    }

    private class AheadThread extends Thread {
        private List toRead = new LinkedList();
        private boolean scaling = false;

        public synchronized void run() {
            for (; ;) {
                try {
                    int nextImage;
                    synchronized (this) {
                        scaling = false;
                        while (toRead.isEmpty())
                            wait();
                        nextImage = ((Integer) toRead.remove(0)).intValue();
                        scaling = true;
                    }
                    try {
                        scaledImage(nextImage);
                    } catch (Exception e) {
                        if (e instanceof InterruptedException)
                            throw (InterruptedException) e;
                        continue;	// just an optimization so keep on going
                    }
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

        synchronized void set(int[] indices) {
            toRead.clear();
            if (scaling)
                interrupt();
            for (int i = 0; i < indices.length; i++)
                toRead.add(new Integer(indices[i]));
            notifyAll();
        }
    }

    private static synchronized void hold(Object obj) {
        holder[holderPos++] = obj;
        if (holderPos >= holder.length)
            holderPos = 0;
    }

    Slides(String[] initialPaths, boolean ask) throws Exception {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        ahead = new AheadThread();
        ahead.setDaemon(true);
        ahead.start();

        getImageCollection(initialPaths, ask);

        curMode = allMode = DEFAULT;

        frame = new Frame();

        GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
        fullFrame = new Frame(gc);
        fullFrame.setUndecorated(true);
        fullFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(fullFrame);

        fullWin = new Window(fullFrame);
        fullWin.setBounds(device.getDefaultConfiguration().getBounds());
        fullWin.setBackground(Color.black);

        imageLabel = new JLabel();
        imageLabel.setVerticalAlignment(CENTER);
        imageLabel.setHorizontalAlignment(CENTER);

        keyActions = new KeyListener() {
            public void keyReleased(KeyEvent ev) {
            }

            public void keyTyped(KeyEvent ev) {
            }

            public void keyPressed(KeyEvent ev) {
                int origCurMode = curMode;
                int origAllMode = allMode;

                switch (ev.getKeyChar()) {
		case 'r':
		    removeImage();
		    break;
                case '=':
                case ' ':
                    nextImage();
                    break;
                case '-':
                    prevImage();
                    break;
                case '+':
                    nextSequence();
                    break;
                case '_':
                    prevSequence();
                    break;
                case '<':
                    startSequence();
                    break;
                case '>':
                    endSequence();
                    break;
                case 'W':
                case 'w':
                    swapWin();
                    break;
                case 'm':
                    curMode = MAXIMIZE;
                    break;
                case 'M':
                    curMode = allMode = MAXIMIZE;
                    break;
                case 'n':
                    curMode = NORMAL;
                    break;
                case 'N':
                    curMode = allMode = NORMAL;
                    break;
                case 'q':
                    System.exit(0);
                }
                if (origCurMode != curMode || origAllMode != allMode)
                    showCurrent();
            }
        };
        mouseActions = new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
                if (ev.isMetaDown()) {			// meta swaps windows
                    swapWin();
                } else if (ev.isShiftDown()) {		// shift is backwards
                    if (ev.isControlDown())		    // ctrl is in seq
                        startSequence();
                    else if (ev.isAltDown())		    // alt is sequence
                        prevSequence();
                    else				    // else move one
                        prevImage();
                } else {				// else forwards
                    if (ev.isControlDown())		    // ctrl is in seq
                        endSequence();
                    else if (ev.isAltDown())		    // alt is sequence
                        nextSequence();
                    else				    // else move one
                        nextImage();
                }
            }
        };

        frame.addKeyListener(keyActions);
        fullWin.addKeyListener(keyActions);

        frame.addMouseListener(mouseActions);
        fullWin.addMouseListener(mouseActions);

        setDisplay(frame);

        screenSize = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        System.out.println("screenSize = " + screenSize);

        GraphicsDevice[] gds = ge.getScreenDevices();
        System.out.println("screen devices: " + gds.length);
        for (int i = 0; i < gds.length; i++)
            dump(gds[i], (gds[i] == ge.getDefaultScreenDevice()));
        System.out.println("scaling style = ~Image.SCALE_FAST");
    }

    private void removeImage() {
        ImageInfo info = (ImageInfo) images.get(curImage);
        System.out.print("remove: " + info.path);
        if (!new File(info.path).delete())
            System.out.print(": failed");
        System.out.println();
        images.remove(curImage);
        if (curImage >= images.size())
            curImage--;
        updateImage();
    }

    private void getImageCollection(String[] initialPaths, boolean ask) {
        Map buckets = new HashMap();
        String leader = null;

        if (!ask) {
            for (int i = 0; i < initialPaths.length; i++)
                leader = addIn(new File(initialPaths[i]), buckets, leader);
        } else {
            JFileChooser chooser = new JFileChooser(initialPaths[0]);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);
            chooser.showOpenDialog(chooser);
            File[] chosen = chooser.getSelectedFiles();

            if (chosen == null || chosen.length == 0)
                System.exit(0);

            for (int i = 0; i < chosen.length; i++)
                leader = addIn(chosen[i], buckets, leader);
        }

        List leaders = new ArrayList(buckets.keySet());
        if (DEBUG_LEADERS)
            System.out.println(leaders.size() + " leaders");
        Collections.shuffle(leaders);
        if (DEBUG_LEADERS)
            System.out.println(leaders.size() + " shuffled leaders");
        images = new ArrayList(leaders.size());
        Iterator it = leaders.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Collection members = (Collection) buckets.get(key);
            if (DEBUG_LEADERS) {
                System.out.println(key + ":");
                for (Iterator mems = members.iterator(); mems.hasNext();)
                    System.out.println("    " + mems.next());
            }
            images.addAll(members);
        }
        ((ArrayList) images).trimToSize();
        curImage = 0;
    }

    private String addIn(File file, Map buckets, String lastLeader) {
        String leader;
        if (DEBUG_LEADERS)
            System.out.println("addIn " + file + " [" + lastLeader + "]");
        if (!file.isDirectory()) {
            String path = file.toString();
            ImageInfo info = new ImageInfo(file.toString());
            if (lastLeader != null && path.startsWith(lastLeader) &&
                    path.length() > lastLeader.length() &&
                    Character.isDigit(path.charAt(lastLeader.length()))) {
                if (DEBUG_LEADERS)
                    System.out.println("    " + "matches");
                leader = lastLeader;
                info.setLeader(leader);
                ((Collection) buckets.get(lastLeader)).add(info);
            } else {
                int elemStart = path.lastIndexOf(File.separatorChar);
                if (elemStart < 0)
                    elemStart = 0;
                else
                    elemStart++;
                int lastDigit = path.length() - 1;
                int firstDigit = -1;
                for (; lastDigit >= elemStart; lastDigit--) {
                    if (Character.isDigit(path.charAt(lastDigit)))
                        break;
                }
                if (lastDigit < elemStart)
                    leader = path;
                else {
                    firstDigit = lastDigit;
                    for (; firstDigit >= elemStart; firstDigit--) {
                        if (!Character.isDigit(path.charAt(firstDigit)))
                            break;
                    }
                    firstDigit++;
                    leader = path.substring(0, firstDigit);
                }
                Collection members = (Collection) buckets.get(leader);
                if (DEBUG_LEADERS) {
                    System.out.println("    " + path);
                    int pos = -4;
                    for (; pos < elemStart; pos++) System.out.print(' ');
                    System.out.print("e");
                    pos++;
                    if (pos >= firstDigit) {
                        System.out.println();
                        pos = -4;
                    }
                    for (; pos < firstDigit; pos++) System.out.print(' ');
                    System.out.print("f");
                    pos++;
                    if (pos >= lastDigit) {
                        System.out.println();
                        pos = -4;
                    }
                    for (; pos < lastDigit; pos++) System.out.print(' ');
                    System.out.print("l");
                    System.out.println();
                    System.out.println("    " +
                            (members == null ? "no" : members.size() + "") +
                            " members");
                }
                if (members == null) {
                    members = new TreeSet();
                    buckets.put(leader, members);
                }
                info.setLeader(leader);
                members.add(info);
            }
        } else {
            File[] contents = file.listFiles();
            leader = null;
            for (int i = 0; i < contents.length; i++)
                leader = addIn(contents[i], buckets, leader);
        }
        return leader;
    }

    private synchronized void setDisplay(Window win) {
        if (win == curWin)
            return;
        if (curWin != null) {
            curWin.remove(imageLabel);
            curWin.hide();
        }
        curWin = win;
        win.add(imageLabel, BorderLayout.CENTER);
        if (win == frame) {
            device.setFullScreenWindow(null);
        } else {
            device.setFullScreenWindow(fullWin);
        }
        win.show();
        win.requestFocus();
        showCurrent();
    }

    private static void dump(GraphicsDevice gd, boolean isDefault) {
        if (isDefault)
            System.out.print("* ");
        else
            System.out.print("  ");
        System.out.print(deviceType(gd.getType()) + ": " + gd.getIDstring());
        GraphicsConfiguration[] gcs = gd.getConfigurations();
        System.out.println(", configurations: " + gcs.length);
        for (int i = 0; i < gcs.length; i++)
            dump(gcs[i], gcs[i] == gd.getDefaultConfiguration());
    }

    private static void dump(GraphicsConfiguration gc, boolean isDefault) {
        if (isDefault)
            System.out.print("  * ");
        else
            System.out.print("    ");
        System.out.println(gc.getBounds() + ": " + gc.getColorModel());
    }

    private static String deviceType(int type) {
        switch (type) {
        case GraphicsDevice.TYPE_RASTER_SCREEN:
            return "raster";
        case GraphicsDevice.TYPE_PRINTER:
            return "printr";
        case GraphicsDevice.TYPE_IMAGE_BUFFER:
            return "buffer";
        default:
            throw new IllegalArgumentException("unknown type: " + type);
        }
    }

    private void execute() throws Exception {
        showCurrent();
    }

    private void nextImage() {
        if (++curImage >= images.size())
            curImage = images.size() - 1;
        updateImage();
    }

    private void prevImage() {
        if (--curImage < 0)
            curImage = 0;
        updateImage();
    }

    private void endSequence() {
        curImage = endOfSequence(curImage);
        updateImage();
    }

    private void startSequence() {
        curImage = startOfSequence(curImage);
        updateImage();
    }

    private void nextSequence() {
        curImage = endOfSequence(curImage);
        nextImage();
    }

    private void prevSequence() {
        curImage = startOfSequence(curImage);
        if (curImage > 0) {
            curImage--;
            curImage = startOfSequence(curImage);
        }
        updateImage();
    }

    private int endOfSequence(int pos) {
        ImageInfo start = infoFor(pos);
        while (pos + 1 < images.size() && start.sameSequence(infoFor(pos + 1)))
            pos++;
        return pos;
    }

    private int startOfSequence(int pos) {
        ImageInfo start = infoFor(pos);
        while (pos > 0 && start.sameSequence(infoFor(pos - 1)))
            pos--;
        return pos;
    }

    private void updateImage() {
        curMode = DEFAULT;	// set to overall default
        showCurrent();
    }

    private synchronized void swapWin() {
        setDisplay(curWin != frame ? (Window) frame : fullWin);
        calcAhead();
    }

    private synchronized void showCurrent() {
        imageLabel.setIcon(scaledImage(curImage));
        System.out.println("showing " + images.get(curImage));

        if (curWin != fullWin) {
            curWin.pack();
            curWin.show();
            curWin.toFront();
        }

        calcAhead();
    }

    private ImageInfo infoFor(int pos) {
        return (ImageInfo) images.get(pos);
    }

    private ImageIcon scaledImage(int pos) {
        ImageInfo info = infoFor(pos);
        if (!info.isValid()) {
            removeInvalid();
            if (pos >= images.size())
                pos--;
            if (pos < 0)
                throw new IllegalStateException("No valid images found");
            return scaledImage(pos);
        }

        int dispMode = curMode;
        if (dispMode == DEFAULT) {
            dispMode = allMode;
            if (dispMode == DEFAULT)
                dispMode = (curWin == fullWin ? MAXIMIZE : NORMAL);
        }

        float scale = 1.0f;
        if (dispMode == MAXIMIZE) {
            Rectangle winBounds = curWin.getBounds();
            float xScale = (float) winBounds.width / info.getWidth();
            float yScale = (float) winBounds.height / info.getHeight();
            scale = Math.min(xScale, yScale);
        }

        return info.scaledImageIcon(scale);
    }

    private void calcAhead() {
        if (curImage >= images.size() - 1)
            return;
        ahead.set(new int[]{
            curImage + 1, endOfSequence(curImage) + 1, curImage + 2
        });
    }

    private void removeInvalid() {
        images.remove(curImage);
        if (curImage >= images.size())
            curImage = images.size();
    }
}
