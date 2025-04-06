package sbp.com.sbt.semver;

import sbp.com.sbt.semver.exceptions.SemverParseException;

public class Semver implements Comparable<Semver> {

    /**
     * Pattern from https://semver.org/
     */
    private final static String REGEXP = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";

    private final String version;

    private Semver(String version) {
        this.version = version;
    }

    public static Semver of(String version) {
        if (version == null || !version.matches(REGEXP)) {
            throw new SemverParseException(version);
        }

        return new Semver(version);
    }

    public String getVersion() {
        return version;
    }

    public int getMajorVersion() {
        return Integer.parseInt(getCoreVersion().split("\\.")[0]);
    }

    public int getMinorVersion() {
        return Integer.parseInt(getCoreVersion().split("\\.")[1]);
    }

    public int getPatchVersion() {
        return Integer.parseInt(getCoreVersion().split("\\.")[2]);
    }

    public String getCoreVersion() {
        return version.split("-")[0];
    }

    public String getPreReleaseVersion() {
        final int firstHyphenIndex = version.indexOf("-");
        if (firstHyphenIndex == -1) {
            return "";
        }
        int plusIndex = version.indexOf("+");
        if (plusIndex == -1) {
            plusIndex = version.length();
        }
        // IndexOutOfBound will not occur - regexp will not let version '0.0.1-' pass
        return version.substring(firstHyphenIndex + 1, plusIndex);
    }

    @Override
    public int compareTo(Semver other) {

        String[] thisCoreVersion = getCoreVersion(this.version);
        String[] otherCoreVersion = getCoreVersion(other.version);

        int result = compareCoreVersions(thisCoreVersion, otherCoreVersion);

        if (result != 0) {
            return result;
        }

        String thisPreRelease = getPreReleaseVersion(this.version);
        String otherPreRelease = getPreReleaseVersion(other.version);

        if (thisPreRelease == null && otherPreRelease == null) {
            return result;
        }

        if (thisPreRelease == null || otherPreRelease == null) {
            if (thisPreRelease == null) {
                return 1;
            }
            return -1;
        }

        return comparePreRelease(thisPreRelease, otherPreRelease);
    }

    private int compareCoreVersions(String[] firstCoreVersion, String[] secondCoreVersion) {
        int majorCompareResult = Integer.valueOf(firstCoreVersion[0]).compareTo(Integer.valueOf(secondCoreVersion[0]));
        if (majorCompareResult != 0) {
            return majorCompareResult;
        }

        int minorCompareResult = Integer.valueOf(firstCoreVersion[1]).compareTo(Integer.valueOf(secondCoreVersion[1]));
        if (minorCompareResult != 0) {
            return minorCompareResult;
        }

        return Integer.valueOf(firstCoreVersion[2]).compareTo(Integer.valueOf(secondCoreVersion[2]));
    }

    private String[] getCoreVersion(String version) {
        return dropBuildVersion(version).split("-")[0].split("\\.");
    }

    private String getPreReleaseVersion(String version) {
        int hyphenIndex = version.indexOf("-");
        if (hyphenIndex == -1) {
            return null;
        }

        return dropBuildVersion(version.substring(hyphenIndex + 1));
    }

    private int comparePreRelease(String firstPreRelease, String secondPreRelease) {

        String[] firstPreReleaseIdentifiers = splitToIdentifiers(firstPreRelease);
        String[] secondPreReleaseIdentifiers = splitToIdentifiers(secondPreRelease);

        int minIdentifiersNum = Math.min(firstPreReleaseIdentifiers.length, secondPreReleaseIdentifiers.length);

        for (int i = 0; i < minIdentifiersNum; i++) {
            int compareResult;
            if (isNumeric(firstPreReleaseIdentifiers[i]) && isNumeric(secondPreReleaseIdentifiers[i])) {
                compareResult = Integer.valueOf(firstPreReleaseIdentifiers[i]).compareTo(Integer.valueOf(secondPreReleaseIdentifiers[i]));
            } else {
                compareResult = firstPreReleaseIdentifiers[i].compareTo(secondPreReleaseIdentifiers[i]);
            }
            if (compareResult != 0) {
                return compareResult > 0 ? 1 : -1;
            }
        }

        return Integer.compare(firstPreReleaseIdentifiers.length, secondPreReleaseIdentifiers.length);
    }

    private String dropBuildVersion(String preReleaseVersion) {
        return preReleaseVersion.split("\\+")[0];
    }

    private String[] splitToIdentifiers(String preReleaseVersion) {
        return preReleaseVersion.split("\\.");
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?(0|[1-9]\\d*)");
    }
}
