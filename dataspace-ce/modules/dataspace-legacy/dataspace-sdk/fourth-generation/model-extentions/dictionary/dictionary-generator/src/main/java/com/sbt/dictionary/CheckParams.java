package com.sbt.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CheckParams {

    private final boolean enableDictionaryDataCheck;

}
