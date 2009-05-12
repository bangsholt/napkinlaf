package net.sourceforge.napkinlaf.util;

import java.awt.*;

@SuppressWarnings({"WeakerAccess"})
public class ComponentWalker {

    private final Visitor visitor;

    public interface Visitor {
        @SuppressWarnings({"SameReturnValue"})
        boolean visit(Component c, int depth);
    }

    public ComponentWalker(Visitor visitor) {
        this.visitor = visitor;
    }

    public void walk(Component top) {
        if (top != null) {
            visit(top, 0);
        }
    }

    private void visit(Component c, int depth) {
        if (visitor.visit(c, depth)) {
            int childDepth = depth + 1;
            if (c instanceof Container) {
                Container container = (Container) c;
                int end = container.getComponentCount();
                for (int i = 0; i < end; i++) {
                    visit(container.getComponent(i), childDepth);
                }
            }
        }
    }
}
