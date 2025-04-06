package com.sbt.dictionary;

import lombok.Getter;

@Getter
public class Columns {

    private final StringBuilder columns = new StringBuilder();
    private final StringBuilder h2Columns = new StringBuilder();
    private final StringBuilder oracleColumns = new StringBuilder();
    private final StringBuilder postgresColumns = new StringBuilder();

}
