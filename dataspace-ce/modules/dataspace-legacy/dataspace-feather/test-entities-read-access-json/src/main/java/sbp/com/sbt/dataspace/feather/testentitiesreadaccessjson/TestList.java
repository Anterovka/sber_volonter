package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 * Test list
 *
 * @param <E> Element type
 */
class TestList<E> extends TreeSet<E> implements List<E> {

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int beginIndex, int endIndex) {
        throw new UnsupportedOperationException();
    }
}
