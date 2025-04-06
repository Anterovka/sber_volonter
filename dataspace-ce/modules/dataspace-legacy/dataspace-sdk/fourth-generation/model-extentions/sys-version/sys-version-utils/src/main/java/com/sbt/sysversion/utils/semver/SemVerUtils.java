package com.sbt.sysversion.utils.semver;

import sbp.com.sbt.semver.Semver;
import sbp.com.sbt.semver.exceptions.SemverParseException;

import java.util.Objects;

public class SemVerUtils {

    public static final String MIN_VERSION = "MIN-VERSION";
    public static final String SNAPSHOT_VERSION = "DEV-SNAPSHOT";

    private SemVerUtils() {}

    public static int compareVersion(String ver1, String ver2) {

        if (Objects.equals(ver1, ver2)) {
            return 0;
        }

        if (MIN_VERSION.equals(ver1)) {
            return -1;
        }

        if (MIN_VERSION.equals(ver2)) {
            return 1;
        }

        if (SNAPSHOT_VERSION.equals(ver1)) {
            return 1;
        }
        if (SNAPSHOT_VERSION.equals(ver2)) {
            return -1;
        }

        Semver semVer1 = Semver.of(ver1);
        Semver semVer2 = Semver.of(ver2);

        return semVer1.compareTo(semVer2);
    }

    public static boolean isMajorIncrease(String previousVersion, String nextVersion) {
        if (Objects.equals(previousVersion, nextVersion)) {
            return false;
        }

        if (MIN_VERSION.equals(previousVersion)) {
            return true;
        }

        if (MIN_VERSION.equals(nextVersion)) {
            return false;
        }

        if (SNAPSHOT_VERSION.equals(previousVersion)) {
            return false;
        }
        if (SNAPSHOT_VERSION.equals(nextVersion)) {
            return true;
        }

        final int previousMajor = Semver.of(previousVersion).getMajorVersion();
        final int nextMajor = Semver.of(nextVersion).getMajorVersion();
        return nextMajor > previousMajor;
    }

    public static String removeBuild(String version) {

        if (SNAPSHOT_VERSION.equals(version) || MIN_VERSION.equals(version)) {
            return version;
        }

        final Semver semver = Semver.of(version);

        final String preReleaseVersion = semver.getPreReleaseVersion();
        if (!preReleaseVersion.isEmpty()) {
            return semver.getCoreVersion() + "-" + preReleaseVersion;
        }
        return semver.getCoreVersion();
    }

    public static boolean isSemverCompatible(String version) {
        try {
            Semver.of(version);
            return true;
        } catch (SemverParseException ex) {
            return false;
        }
    }
}
