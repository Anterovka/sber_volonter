package sbp.com.sbt.semver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.semver.exceptions.SemverParseException;

public class SemverTest {

    @Test
    void zeroVersionCorePositiveTest() {
        Assertions.assertDoesNotThrow(() -> Semver.of("0.0.0"));
    }

    @Test
    void onlyVersionCorePositiveTest() {
        Assertions.assertDoesNotThrow(() -> Semver.of("0.0.1"));
    }

    @Test
    void leadingZeroMajorNegativeTest() {
        String message = Assertions.assertThrows(SemverParseException.class, () -> Semver.of("00.0.1")).getMessage();
        Assertions.assertEquals(
            "Error when parsing version 00.0.1 according to Semver rules. The rules can be found at https://semver.org/",
            message
        );
    }

    @Test
    void noPatchNumberTest() {
        String message = Assertions.assertThrows(SemverParseException.class, () -> Semver.of("0.1")).getMessage();
        Assertions.assertEquals(
            "Error when parsing version 0.1 according to Semver rules. The rules can be found at https://semver.org/",
            message
        );
    }

    @Test
    void preReleaseVersionEmptyGroupTest() {
        String message = Assertions.assertThrows(SemverParseException.class, () -> Semver.of("0.1.1-a..3")).getMessage();
        Assertions.assertEquals(
            "Error when parsing version 0.1.1-a..3 according to Semver rules. The rules can be found at https://semver.org/",
            message
        );
    }

    @Test
    void preReleaseVersionLeadingZeroTest() {
        String message = Assertions.assertThrows(SemverParseException.class, () -> Semver.of("0.1.1-a.03")).getMessage();
        Assertions.assertEquals(
            "Error when parsing version 0.1.1-a.03 according to Semver rules. The rules can be found at https://semver.org/",
        message
        );
    }

    @Test
    void positiveReleaseVersionTest() {
        Assertions.assertDoesNotThrow(() -> Semver.of("5.9.23-alpha-01.15.D-015+AB25"));
    }

    @Test
    void coreVersionCompareTest() {
        {
            Semver first = Semver.of("1.0.0");
            Semver second = Semver.of("2.0.0");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("2.0.0");
            Semver second = Semver.of("1.0.0");

            Assertions.assertEquals(1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.0");
            Semver second = Semver.of("1.2.0");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.2.0");
            Semver second = Semver.of("1.1.0");

            Assertions.assertEquals(1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1");
            Semver second = Semver.of("1.1.2");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.2");
            Semver second = Semver.of("1.1.1");

            Assertions.assertEquals(1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1");
            Semver second = Semver.of("1.1.1");

            Assertions.assertEquals(0, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha");
            Semver second = Semver.of("1.1.1");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1");
            Semver second = Semver.of("1.1.1-alpha");

            Assertions.assertEquals(1, first.compareTo(second));
        }
    }

    @Test
    void preReleaseVersionCompareTest() {
        {
            Semver first = Semver.of("1.1.1-alpha");
            Semver second = Semver.of("1.1.1-alpha");

            Assertions.assertEquals(0, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha");
            Semver second = Semver.of("1.1.1-alpha.1");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1");
            Semver second = Semver.of("1.1.1-alpha");

            Assertions.assertEquals(1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1");
            Semver second = Semver.of("1.1.1-alpha.2");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1.SNAPSHOT");
            Semver second = Semver.of("1.1.1-alpha.2");

            Assertions.assertEquals(-1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1+D-001");
            Semver second = Semver.of("1.1.1-alpha.1");

            Assertions.assertEquals(0, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1+D-001");
            Semver second = Semver.of("1.1.1-alpha.1+D-002");

            Assertions.assertEquals(0, first.compareTo(second));
        }
        {
            Semver first = Semver.of("1.1.1-alpha.1.35.beta+D-001");
            Semver second = Semver.of("1.1.1-alpha.1.35.Beta+D-002");

            Assertions.assertEquals(1, first.compareTo(second));
        }
        {
            Semver first = Semver.of("0.0.1-alpha.11.35");
            Semver second = Semver.of("0.0.1-alpha.2.35");

            Assertions.assertEquals(1, first.compareTo(second));
        }
    }

    @Test
    void gettersTest() {
        Semver semver = Semver.of("0.0.1-DEV-SNAPSHOT.2+004");
        Assertions.assertEquals(0, semver.getMajorVersion());
        Assertions.assertEquals(0, semver.getMinorVersion());
        Assertions.assertEquals(1, semver.getPatchVersion());

        Assertions.assertEquals("DEV-SNAPSHOT.2", semver.getPreReleaseVersion());

        Assertions.assertEquals("b.12", Semver.of("0.0.1-b.12").getPreReleaseVersion());
    }

    @Test
    void coreAndBuildTest() {
        Semver first = Semver.of("0.0.1+a2");
        Semver second = Semver.of("0.0.1");

        Assertions.assertEquals(0, first.compareTo(second));
    }
}
