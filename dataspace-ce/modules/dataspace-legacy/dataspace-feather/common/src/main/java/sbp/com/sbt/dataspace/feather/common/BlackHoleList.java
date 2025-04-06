package sbp.com.sbt.dataspace.feather.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * List "Black Hole"
 *
 * @param <E> Element type
 */
public final class BlackHoleList<E> implements List<E> {

    static final BlackHoleList<Object> INSTANCE = new BlackHoleList<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return array;
    }

    @Override
    public boolean add(E element) {
        return false;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {
// Никаких действий не требуется
    }

    @Override
    public E get(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public E set(int index, E element) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void add(int index, E element) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public E remove(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int indexOf(Object object) {
        return -1;
    }

    @Override
    public int lastIndexOf(Object object) {
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public List<E> subList(int beginIndex, int endIndex) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return "[]";
    }

    /**
     * Get instance
     *
     * @param <E2> Element type
     */
    public static <E2> BlackHoleList<E2> blackHoleList() {
        return (BlackHoleList<E2>) INSTANCE;
    }
}
