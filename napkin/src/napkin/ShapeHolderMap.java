
package napkin;

import napkin.ShapeHolder.Factory;

import java.util.WeakHashMap;

class ShapeHolderMap extends WeakHashMap {
    private final Factory factory;

    ShapeHolderMap(Factory factory) {
        this.factory = factory;
    }

    public synchronized Object get(Object key) {
        Object obj = super.get(key);
        if (obj == null) {
            obj = factory.create();
            put(key, obj);
        }
        return obj;
    }
}