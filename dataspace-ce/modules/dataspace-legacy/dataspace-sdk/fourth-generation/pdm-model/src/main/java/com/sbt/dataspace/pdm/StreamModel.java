package com.sbt.dataspace.pdm;

import com.sbt.dataspace.pdm.exception.NoModelStreamException;
import com.sbt.mg.Helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User-defined model, which already exists, is transformed into this class.
 * То есть это модель, которую можно read.
 */
public class StreamModel {

    public static final String MODEL_STREAM_NAME = "MODEL";
    public static final String ROOT_MODEL_STREAM_NAME = "ROOT_MODEL";
    public static final String PDM_STREAM_NAME = "PDM";
    public static final String IMMUTABLE_PDM_STREAM_NAME = "IMMUTABLE_PDM";
    public static final String CHANGELOG_STREAM_NAME = "CHANGELOG";
    public static final String STATUS = "STATUS";
    public static final String DICTIONARY_GENERATOR_NEW = "DICTIONARY_GENERATOR_NEW";
    public static final String DICTIONARY_GENERATOR_CURRENT = "DICTIONARY_GENERATOR_CURRENT";

    private final Map<String, Collection<InputStream>> streams = new HashMap<>();
    private final boolean fromStreams;

    private StreamModel(boolean fromStreams) {
        this.fromStreams = fromStreams;
    }

    public static StreamModel forStreams() {
        return new StreamModel(true);
    }

    public static StreamModel forModelStreamOnly() {
        return new StreamModel(false);
    }

    public boolean isFromStreams() {
        return fromStreams;
    }

    public StreamModel setModel(InputStream modelStream) {
        this.streams.put(MODEL_STREAM_NAME,
                new ArrayList<InputStream>() {{
                    add(modelStream);
                }});
        return this;
    }

    public StreamModel setRootModel(InputStream modelStream) {
        this.streams.put(ROOT_MODEL_STREAM_NAME,
                new ArrayList<InputStream>() {{
                    add(modelStream);
                }});

        return this;
    }

    public StreamModel setModel(String modelString) {
        this.streams.put(MODEL_STREAM_NAME,
                new ArrayList<InputStream>() {{
                    add(new ByteArrayInputStream(modelString.getBytes(StandardCharsets.UTF_8)));
                }});

        return this;
    }

    public StreamModel setRootModel(String modelString) {
        this.streams.put(ROOT_MODEL_STREAM_NAME,
                new ArrayList<InputStream>() {{
                    add(new ByteArrayInputStream(modelString.getBytes(StandardCharsets.UTF_8)));
                }});

        return this;
    }

    public InputStream getRootModel() {
        final Collection<InputStream> streams = this.streams.get(ROOT_MODEL_STREAM_NAME);
        if (streams == null || streams.isEmpty()) {
            throw new NoModelStreamException();
        }
        return streams.iterator().next();
    }


    public Collection<InputStream> getModel() {
        streams.putIfAbsent(MODEL_STREAM_NAME, new ArrayList<>());
        return streams.get(MODEL_STREAM_NAME);
    }

    public StreamModel setStatus(InputStream statusStream) {
        this.streams.put(STATUS, Collections.singletonList(statusStream));
        return this;
    }

    public StreamModel setStatus(String statusString) {
        this.streams.put(STATUS, Collections.singletonList(new ByteArrayInputStream(statusString.getBytes(StandardCharsets.UTF_8))));
        return this;
    }

    public Collection<InputStream> getStatus() {
        return streams.get(STATUS);
    }

    public StreamModel setDictionaryNew(InputStream dictionaryStream) {
        this.streams.put(DICTIONARY_GENERATOR_NEW, Collections.singletonList(dictionaryStream));
        return this;
    }

    public StreamModel setDictionaryCurrent(InputStream dictionaryStream) {
        this.streams.put(DICTIONARY_GENERATOR_CURRENT, Collections.singletonList(dictionaryStream));
        return this;
    }

    public StreamModel setStringDictionaryNew(Collection<String> dictionaries) {
        this.streams.put(
                DICTIONARY_GENERATOR_NEW,
                dictionaries.stream()
                        .map(dictionary -> new ByteArrayInputStream(dictionary.getBytes(StandardCharsets.UTF_8)))
                        .collect(Collectors.toList()));
        return this;
    }

    public StreamModel setStringDictionaryCurrent(Collection<String> dictionaries) {
        this.streams.put(
                DICTIONARY_GENERATOR_CURRENT,
                dictionaries.stream()
                        .map(dictionary -> new ByteArrayInputStream(dictionary.getBytes(StandardCharsets.UTF_8)))
                        .collect(Collectors.toList()));
        return this;
    }

    public StreamModel setStreamDictionaryNew(Collection<InputStream> dictionaries) {
        this.streams.put(
                DICTIONARY_GENERATOR_NEW,
                dictionaries);
        return this;
    }

    public StreamModel setStreamDictionaryCurrent(Collection<InputStream> dictionaries) {
        this.streams.put(
                DICTIONARY_GENERATOR_CURRENT,
                dictionaries);
        return this;
    }

    public Collection<InputStream> getDictionaryNew() {
        return streams.get(DICTIONARY_GENERATOR_NEW);
    }

    public Collection<InputStream> getDictionaryCurrent() {
        return streams.get(DICTIONARY_GENERATOR_CURRENT);
    }

    public StreamModel setPdm(InputStream pdmStream) {
        this.streams.put(PDM_STREAM_NAME, Collections.singleton(pdmStream));
        return this;
    }

    public StreamModel setPdm(String pdm) {
        this.streams.put(PDM_STREAM_NAME, Collections.singleton(new ByteArrayInputStream(pdm.getBytes(StandardCharsets.UTF_8))));
        return this;
    }

    public InputStream getPdm() {
        return this.streams.getOrDefault(PDM_STREAM_NAME, Collections.singleton(null)).iterator().next();
    }

    public StreamModel setImmutablePdm(InputStream pdmStream) {
        this.streams.put(IMMUTABLE_PDM_STREAM_NAME, Collections.singleton(pdmStream));
        return this;
    }

    public StreamModel setImmutablePdm(String pdm) {
        this.streams.put(IMMUTABLE_PDM_STREAM_NAME, Collections.singleton(new ByteArrayInputStream(pdm.getBytes(StandardCharsets.UTF_8))));
        return this;
    }

    public InputStream getImmutablePdm() {
        return this.streams.getOrDefault(IMMUTABLE_PDM_STREAM_NAME, Collections.singleton(null)).iterator().next();
    }

    public StreamModel setChangelog(InputStream changelogStream) {
        this.streams.put(CHANGELOG_STREAM_NAME, Collections.singleton(changelogStream));
        return this;
    }

    public StreamModel setChangelog(String changelog) {
        this.streams.put(CHANGELOG_STREAM_NAME, Collections.singleton(new ByteArrayInputStream(changelog.getBytes(StandardCharsets.UTF_8))));
        return this;
    }

    public InputStream getChangelog() {
        return this.streams.getOrDefault(CHANGELOG_STREAM_NAME, Collections.singleton(null)).iterator().next();
    }

    public String getChangelogAsString() {
        final InputStream changeLogInputStream =
                this.streams.getOrDefault(CHANGELOG_STREAM_NAME, Collections.singleton(null)).iterator().next();
        if (changeLogInputStream == null) {
            return null;
        }
        return Helper.readToString(changeLogInputStream);
    }
}
