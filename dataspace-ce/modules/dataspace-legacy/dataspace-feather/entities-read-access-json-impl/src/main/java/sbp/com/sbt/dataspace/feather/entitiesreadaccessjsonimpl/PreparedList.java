package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.ArrayList;
import java.util.function.IntFunction;

/**
 * Prepared list
 *
 * @param <E> Element type
 */
public final class PreparedList<E> {

    // The function is applied for each call to {@link #get(int)}.
    IntFunction<E> elementInitializer;
    ArrayList<E> list = new ArrayList<>();

    /**
     * @param elementInitializer The element initializer
     * @param initialSize        Initial size
     */
    PreparedList(IntFunction<E> elementInitializer, int initialSize) {
        this.elementInitializer = elementInitializer;
        get(initialSize - 1);
    }

    /**
     * Get element
     *
     * @param index Index
     */
    E get(int index) {
        if (index >= list.size()) {
            synchronized (this) {
                if (index >= list.size()) {
                    int capacity = index + 1;
                    capacity = capacity + +(capacity >> 1);
                    list.ensureCapacity(capacity);
                    for (int i = list.size(); i < capacity; ++i) {
                        list.add(elementInitializer.apply(i));
                    }
                }
            }
        }
        return list.get(index);
    }
}
