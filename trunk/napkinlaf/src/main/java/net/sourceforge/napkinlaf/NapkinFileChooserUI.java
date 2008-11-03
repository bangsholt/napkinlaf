package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

//!! It seems as if the BasicFileChooserUI is not yet well formed, so we're just
//!! borrowing the metal chooser for now.

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinFileChooserUI extends MetalFileChooserUI
        implements NapkinPainter {

    private final FileView fileView = new NapkinFileView();

    private class NapkinFileView extends BasicFileView {
        private final Map<String, Icon> pathIconCache =
                new HashMap<String, Icon>();

        @Override
        public Icon getCachedIcon(File f) {
            return pathIconCache.get(f.getPath());
        }

        @Override
        public Icon getIcon(File f) {
            Icon icon = getCachedIcon(f);
            if (icon == null) {
                icon = getDefaultIcon(f);
                cacheIcon(f, icon);
            }
            return icon;
        }

        @Override
        public void cacheIcon(File f, Icon icon) {
            pathIconCache.put(f.getPath(), icon);
        }

        @Override
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
                if (icon == null) {
                    icon = UIManager.getIcon("FileView.fileIcon");
                }
            }
            return icon;
        }
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinFileChooserUI((JFileChooser) c);
    }

    private NapkinFileChooserUI(JFileChooser c) {
        super(c);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    @Override
    public FileView getFileView(JFileChooser fc) {
        return fileView;
    }
}
