package com.sbt.model.exception.optimizechangelog;

import com.sbt.mg.exception.GeneralSdkException;

public class NeedToDoChangelogOptimizationException extends GeneralSdkException {
    public NeedToDoChangelogOptimizationException() {
        super("A change in the shutdown setting for generating Liquibase scripts for Oracle has been detected (parameter disableGenerateOracleLiquibase)." +
            "Such change is allowed only in conjunction with changelog optimization (parameter optimizeChangelog=true)" +
            "Enable optimization of the changelog (generator model parameter optimizeChangelog=true) during release with changes" +
            "settings for disabling Liquibase script generation for Oracle (parameter disableGenerateOracleLiquibase), " +
            "либо возвратите предыдущее состояние параметра disableGenerateOracleLiquibase");
    }
}
