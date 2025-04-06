package com.sbt.parameters.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DictionaryDto {

    @JsonCreator
    public DictionaryDto(@JsonProperty(value = "type") String type,
                         @JsonProperty(value = "objects") List<Map<String, Object>> objects,
                         @JsonProperty(value = "deletes") Collection<String> deletes) {
        this.type = type;
        this.objects = objects;
        this.deletes = deletes;
    }

    public static DictionaryDto createObjects(String type, List<Map<String, Object>> objects) {
        return new DictionaryDto(type, objects, null);
    }

    public static DictionaryDto createDeletes(String type, Collection<String> deletes) {
        return new DictionaryDto(type, null, deletes);
    }

    private String type;
    private List<Map<String, Object>> objects;
    private Collection<String> deletes;
    @JsonIgnore
    private boolean splittable = true;

}
