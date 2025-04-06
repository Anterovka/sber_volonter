package com.sbt.dataspace.pdm;

import java.io.File;

public class TargetFileHolder {

    private File targetFile;
    private boolean isTargetDirectory;

    public TargetFileHolder(File targetFile) {
        this.targetFile = targetFile;
        this.isTargetDirectory = true;
    }

    public TargetFileHolder(File targetFile, boolean isTargetDirectory) {
        this.targetFile = targetFile;
        this.isTargetDirectory = isTargetDirectory;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public boolean isTargetDirectory() {
        return isTargetDirectory;
    }

    public boolean isReleaseDirectory() {
        return !isTargetDirectory;
    }
}
