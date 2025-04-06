package com.sbt.parameters.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableInt;

@Setter
@Getter
public class SysVersionFillParams {

    public SysVersionFillParams(String sysVersionId, String modelVersion, String previousSysVersionId, String previousModelVersion) {
        this.sysVersionId = sysVersionId;
        this.modelVersion = modelVersion;
        this.previousSysVersionId = previousSysVersionId;
        this.previousModelVersion = previousModelVersion;
    }

    private String sysVersionId;
    private MutableInt dictPart = new MutableInt(1);
    private String modelVersion;
    private String previousSysVersionId;
    private String previousModelVersion;
}
