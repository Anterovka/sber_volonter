package sbp.com.sbt.dataspace.feather.common;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Metadata Manager
 */
public final class MetaDataManager {

    Map<Object, Object> keys = new ConcurrentHashMap<>();
    Map<Object, Object> metaDataMap = new ConcurrentHashMap<>();

    /**
     * Get metadata
     *
     * @param metaDataClass The metadata class
     * @param <M>
     * @return
     */
    public <M> M get(Class<M> metaDataClass) {
        return (M) metaDataMap.get(metaDataClass);
    }

    /**
     * Put
     *
     * @param metaDataClass The metadata class
     * @param writeKey      Write key
     * @param metaData      Metadata
     * @param <M>           Type of metadata
     */
    public <M> void put(Class<M> metaDataClass, Object writeKey, M metaData) {
        Object storedWriteKey = keys.computeIfAbsent(metaDataClass, key -> writeKey);
        synchronized (storedWriteKey) {
            if (!Objects.equals(storedWriteKey, writeKey)) {
                throw new MetaDataAccessViolationException();
            }
            metaDataMap.put(metaDataClass, metaData);
        }
    }

    /**
     * Delete
     *
     * @param metaDataClass The metadata class
     * @param writeKey      Write key
     */
    public void remove(Class<?> metaDataClass, Object writeKey) {
        Object storedWriteKey = keys.get(metaDataClass);
        if (storedWriteKey == null) {
            throw new MetaDataAccessViolationException();
        }
        synchronized (storedWriteKey) {
            if (!Objects.equals(storedWriteKey, writeKey)) {
                throw new MetaDataAccessViolationException();
            }
            metaDataMap.remove(metaDataClass);
            keys.remove(metaDataClass);
        }
    }
}
