// $Id$

package napkin;

import java.awt.*;

class ComponentWalker {

    private final Visitor visitor;

    interface Visitor {
        boolean visit(Component c, int depth);
    }

    ComponentWalker(Visitor visitor) {
        this.visitor = visitor;
    }

    public void walk(Component top) {
        if (top != null)
            visit(top, 0);
    }

    private void visit(Component c, int depth) {
        if (!visitor.visit(c, depth))
            return;
        int childDepth = depth + 1;
        if (c instanceof Container) {
            Container container = (Container) c;
            int end = container.getComponentCount();
            for (int i = 0; i < end; i++)
                visit(container.getComponent(i), childDepth);
        }
    }

}

