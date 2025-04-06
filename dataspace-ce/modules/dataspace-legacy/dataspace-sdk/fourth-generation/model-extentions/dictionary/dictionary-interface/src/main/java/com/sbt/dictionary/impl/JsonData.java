package com.sbt.dictionary.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {

    private String type;
    private List<Map<String, Object>> objects;

}
