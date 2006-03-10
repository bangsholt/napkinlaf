// $Id$

package napkin;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.metal.MetalFileChooserUI;

//!! It seems as if the BasicFileChooserUI is not yet well formed, so we're just
//!! borrowing the metal chooser for now.

public class NapkinFileChooserUI extends MetalFileChooserUI
        implements NapkinPainter {

    private final NapkinFileView fileView = new NapkinFileView();

    private class NapkinFileView extends BasicFileView {
        private final Map<String, Icon> pathIconCache =
                new HashMap<String, Icon>();

        public Icon getCachedIcon(File f) {
            return pathIconCache.get(f.getPath());
        }

        public Icon getIcon(File f) {
            Icon icon = getCachedIcon(f);
            if (icon != null)
                return icon;

            icon = getDefaultIcon(f);
            cacheIcon(f, icon);
            return icon;
        }

        public void cacheIcon(File f, Icon icon) {
            pathIconCache.put(f.getPath(), icon);
        }

        public void clearIconCache() {
            pathIconCache.clear();
        }

        public Icon getDefaultIcon(File f) {
            FileSystemView fsv = getFileChooser().getFileSystemView();
            Icon icon;
            if (fsv.isFloppyDrive(f)) {
                icon = UIManager.getIcon("FileView.floppyDriveIcon");
            } else if (fsv.isDrive(f)) {
                icon = UIManager.getIcon("FileView.hardDriveIcon");
            } else if (fsv.isComputerNode(f)) {
                icon = UIManager.getIcon("FileView.computerIcon");
            } else if (f.isDirectory()) {
                icon = UIManager.getIcon("FileView.directoryIcon");
            } else {
                icon = fsv.getSystemIcon(f);
                if (icon == null)
                    icon = UIManager.getIcon("FileView.fileIcon");
            }
            return icon;
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return new NapkinFileChooserUI((JFileChooser) c);
    }

    private NapkinFileChooserUI(JFileChooser c) {
        super(c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

    public FileView getFileView(JFileChooser fc) {
        return fileView;
    }

}