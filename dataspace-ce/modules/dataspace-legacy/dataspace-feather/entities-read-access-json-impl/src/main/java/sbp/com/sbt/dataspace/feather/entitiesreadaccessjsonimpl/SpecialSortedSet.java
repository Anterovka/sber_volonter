package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A special ordered set
 *
 * @param <E> Element type
 */
class SpecialSortedSet<E> implements Set<E> {

    static final SpecialSortedSet<Object> EMPTY = new SpecialSortedSet<>(null);

    int offset;
    ArrayList<E> sortedElements = new ArrayList<>();

    /**
     * @param offset Offset
     */
    SpecialSortedSet(Integer offset) {
        this.offset = offset == null ? 0 : offset;
    }

    /**
     * Add element
     *
     * @param order   Order
     * @param element Element
     */
    void add(Integer order, E element) {
        if (order == null) {
            sortedElements.add(element);
        } else {
            int capacity = order - offset;
            sortedElements.ensureCapacity(order - offset);
            for (int i = sortedElements.size(); i < capacity; ++i) {
                sortedElements.add(null);
            }
            sortedElements.set(capacity - 1, element);
        }
    }

    @Override
    public int size() {
        return sortedElements.size();
    }

    @Override
    public boolean isEmpty() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return sortedElements.iterator();
    }

    @Override
    public Object[] toArray() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] array0) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean add(E element) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object object) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new java.lang.UnsupportedOperationException();
    }

    /**
     * Get empty sorted set
     *
     * @param <E> Element type
     */
    static <E> SpecialSortedSet<E> getEmpty() {
        return (SpecialSortedSet<E>) EMPTY;
    }
}
