package napkin;

import java.util.WeakHashMap;

import napkin.DrawnShapeHolder.Factory;

public class DrawnShapeHolderMap extends WeakHashMap {
    private final Factory factory;

    public DrawnShapeHolderMap(Factory factory) {
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