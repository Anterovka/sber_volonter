package com.sbt.dictionary.api;

import com.sbt.mg.data.model.XmlModel;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DictionaryData {

    /**
     * @param newPlaces множество files from which it is necessary to import reference data.
     */
    void setDictionaryPlace(Set<File> newPlaces, File oldPlace);

    void setDictionaryStreams(Collection<InputStream> newStreams, Collection<InputStream> currentStreams);
    void setDictionaryStreams(Collection<InputStream> newStreams);

    default void init() {}
    default void check(XmlModel model) {}

    // Entity -> (Field name, value)
    Map<String, List<Map<String, Object>>> getAddData();
    Map<String, List<Map<String, Object>>> getUpdateData();

    /**
     * Obtaining all data read by the implementation for saving the delta
     * @return All read data.
     */
    default Map<String, List<Map<String, Object>>> getAllData() {
        return Collections.emptyMap();
    }
    default Map<String, List<Map<String, Object>>> getOldData() {
        return Collections.emptyMap();
    }

    Set<String> getAllTypes();

    /**
     * for optimization of changelog we clear the previous state of reference data,
     * in order for the data to be rolled out, as from scratch
     */
    default void clearOldData() {}
}
