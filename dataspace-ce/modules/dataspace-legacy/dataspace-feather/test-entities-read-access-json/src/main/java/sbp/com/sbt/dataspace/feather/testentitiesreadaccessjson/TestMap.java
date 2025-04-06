package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import java.util.HashMap;

/**
 * Display for testing
 *
 * @param <K> Key type
 * @param <V> Type of element
 */
class TestMap<K, V> extends HashMap<K, V> implements Comparable<TestMap<K, V>> {

    static final String ID_FIELD_NAME = "id";

    static int count;

    int index = count++;

    @Override
    public int compareTo(TestMap<K, V> map) {
        return containsKey(ID_FIELD_NAME) ? ((String) get(ID_FIELD_NAME)).compareTo((String) map.get(ID_FIELD_NAME)) : (index - map.index);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TestMap)) {
            return false;
        }
        TestMap<K, V> map = (TestMap<K, V>) object;
        return containsKey(ID_FIELD_NAME) && get(ID_FIELD_NAME).equals(map.get(ID_FIELD_NAME));
    }

    @Override
    public int hashCode() {
        return containsKey(ID_FIELD_NAME) ? get(ID_FIELD_NAME).hashCode() : index;
    }
}
