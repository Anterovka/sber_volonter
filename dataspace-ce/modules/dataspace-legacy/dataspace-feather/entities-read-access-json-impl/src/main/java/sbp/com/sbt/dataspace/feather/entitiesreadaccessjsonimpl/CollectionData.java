package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.Collection;

/**
 * The data of the collection
 *
 * @param <E> Element type
 */
class CollectionData<E> {

    String base;
    Collection<E> elements;
    Integer count;
}
