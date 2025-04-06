package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing access to entities for reading through JSON
 */
public abstract class EntitiesReadAccessJsonTest extends CommonEntitiesReadAccessJsonTest {

    @DisplayName("Test Case 1")
    @TestFactory
    public Stream<DynamicTest> testCase1() {
        return getDynamicTests(TestCase1::new);
    }

    @DisplayName("Test Case 2")
    @TestFactory
    public Stream<DynamicTest> testCase2() {
        return getDynamicTests(TestCase2::new);
    }

    @DisplayName("Test Case 3")
    @TestFactory
    public Stream<DynamicTest> testCase3() {
        return getDynamicTests(TestCase3::new);
    }

    @DisplayName("Test Case 4")
    @TestFactory
    public Stream<DynamicTest> testCase4() {
        return getDynamicTests(TestCase4::new);
    }

    @DisplayName("Test Case 5")
    @TestFactory
    public Stream<DynamicTest> testCase5() {
        return getDynamicTests(TestCase5::new);
    }

    @DisplayName("Test Case 6")
    @TestFactory
    public Stream<DynamicTest> testCase6() {
        return getDynamicTests(TestCase6::new);
    }

    @DisplayName("Test Case 7")
    @TestFactory
    public Stream<DynamicTest> testCase7() {
        return getDynamicTests(TestCase7::new);
    }

    @DisplayName("Test Case 8")
    @TestFactory
    public Stream<DynamicTest> testCase8() {
        return getDynamicTests(TestCase8::new);
    }

    @DisplayName("Test Case 9")
    @TestFactory
    public Stream<DynamicTest> testCase9() {
        return getDynamicTests(TestCase9::new);
    }

    @DisplayName("Test Case 10")
    @TestFactory
    public Stream<DynamicTest> testCase10() {
        return getDynamicTests(TestCase10::new);
    }

    @DisplayName("Test Case 11")
    @TestFactory
    public Stream<DynamicTest> testCase11() {
        return getDynamicTests(TestCase11::new);
    }

    @DisplayName("Test Case 12")
    @TestFactory
    public Stream<DynamicTest> testCase12() {
        return getDynamicTests(TestCase12::new);
    }

    @DisplayName("Test Case 13")
    @TestFactory
    public Stream<DynamicTest> testCase13() {
        return getDynamicTests(TestCase13::new);
    }

    @DisplayName("Test Case 14")
    @TestFactory
    public Stream<DynamicTest> testCase14() {
        return getDynamicTests(TestCase14::new);
    }

    @DisplayName("Test Case 15")
    @TestFactory
    public Stream<DynamicTest> testCase15() {
        return getDynamicTests(TestCase15::new);
    }

    @DisplayName("Test Case 17")
    @TestFactory
    public Stream<DynamicTest> testCase17() {
        return getDynamicTests(TestCase17::new);
    }

    @DisplayName("Test Case 18")
    @TestFactory
    public Stream<DynamicTest> testCase18() {
        return getDynamicTests(TestCase18::new);
    }

    @DisplayName("Test Case 20")
    @TestFactory
    public Stream<DynamicTest> testCase20() {
        return getDynamicTests(TestCase20::new);
    }

    @DisplayName("Test Case 21")
    @TestFactory
    public Stream<DynamicTest> testCase21() {
        return getDynamicTests(TestCase21::new);
    }

    @DisplayName("Test Case 22")
    @TestFactory
    public Stream<DynamicTest> testCase22() {
        return getDynamicTests(TestCase22::new);
    }

    @DisplayName("Test Case 25")
    @TestFactory
    public Stream<DynamicTest> testCase25() {
        return getDynamicTests(TestCase25::new);
    }

    @DisplayName("Test Case 26")
    @TestFactory
    public Stream<DynamicTest> testCase26() {
        return getDynamicTests(TestCase26::new);
    }

    @DisplayName("Test Case 27")
    @TestFactory
    public Stream<DynamicTest> testCase27() {
        return getDynamicTests(TestCase27::new);
    }

    @DisplayName("Test case 28")
    @TestFactory
    public Stream<DynamicTest> testCase28() {
        return getDynamicTests(TestCase28::new);
    }

    @DisplayName("Test Case 29")
    @TestFactory
    public Stream<DynamicTest> testCase29() {
        return getDynamicTests(TestCase29::new);
    }

    @DisplayName("Test Case 30")
    @TestFactory
    public Stream<DynamicTest> testCase30() {
        return getDynamicTests(TestCase30::new);
    }

    @DisplayName("Test Case 31")
    @TestFactory
    public Stream<DynamicTest> testCase31() {
        return getDynamicTests(TestCase31::new);
    }

    @DisplayName("Test Case 32")
    @TestFactory
    public Stream<DynamicTest> testCase32() {
        return getDynamicTests(TestCase32::new);
    }

    @DisplayName("Test Case 33")
    @TestFactory
    public Stream<DynamicTest> testCase33() {
        return getDynamicTests(TestCase33::new);
    }

    @DisplayName("Test Case 36")
    @TestFactory
    public Stream<DynamicTest> testCase36() {
        return getDynamicTests(TestCase36::new);
    }

    @DisplayName("Case with exception 1")
    @TestFactory
    public Stream<DynamicTest> exceptionCase1() {
        return getDynamicTests(ExceptionCase1::new);
    }

    @DisplayName("Bug-case 1")
    @TestFactory
    public Stream<DynamicTest> bugCase1() {
        return getDynamicTests(BugCase1::new);
    }

    @DisplayName("Bug-case 2")
    @TestFactory
    public Stream<DynamicTest> bugCase2() {
        return getDynamicTests(BugCase2::new);
    }

    @DisplayName("Bug-case 4")
    @TestFactory
    public Stream<DynamicTest> bugCase4() {
        return getDynamicTests(BugCase4::new);
    }

    @DisplayName("Bug-case 5")
    @TestFactory
    public Stream<DynamicTest> bugCase5() {
        return getDynamicTests(BugCase5::new);
    }

    @DisplayName("Bug-case 6")
    @TestFactory
    public Stream<DynamicTest> bugCase6() {
        return getDynamicTests(BugCase6::new);
    }

    @DisplayName("Bug-case 8")
    @TestFactory
    public Stream<DynamicTest> bugCase8() {
        return getDynamicTests(BugCase8::new);
    }

    @DisplayName("Bug-case 9")
    @TestFactory
    public Stream<DynamicTest> bugCase9() {
        return getDynamicTests(BugCase9::new);
    }

    @DisplayName("Bug-case 10")
    @TestFactory
    public Stream<DynamicTest> bugCase10() {
        return getDynamicTests(BugCase10::new);
    }

    @DisplayName("Bug-case 11")
    @TestFactory
    public Stream<DynamicTest> bugCase11() {
        return getDynamicTests(BugCase11::new);
    }

    @DisplayName("Bug-case 12")
    @TestFactory
    public Stream<DynamicTest> bugCase12() {
        return getDynamicTests(BugCase12::new);
    }

    @DisplayName("Bug-case 13")
    @TestFactory
    public Stream<DynamicTest> bugCase13() {
        return getDynamicTests(BugCase13::new);
    }

    @DisplayName("Bug-case 14")
    @TestFactory
    public Stream<DynamicTest> bugCase14() {
        return getDynamicTests(BugCase14::new);
    }

    @DisplayName("Bug-case 15")
    @TestFactory
    public Stream<DynamicTest> bugCase15() {
        return getDynamicTests(BugCase15::new);
    }

    @DisplayName("Bug-case 16")
    @TestFactory
    public Stream<DynamicTest> bugCase16() {
        return getDynamicTests(BugCase16::new);
    }

    @DisplayName("Bug-case 17")
    @TestFactory
    public Stream<DynamicTest> bugCase17() {
        return getDynamicTests(BugCase17::new);
    }

    @DisplayName("Bug-case 18")
    @TestFactory
    public Stream<DynamicTest> bugCase18() {
        return getDynamicTests(BugCase18::new);
    }

    @DisplayName("Bug-case 19")
    @TestFactory
    public Stream<DynamicTest> bugCase19() {
        return getDynamicTests(BugCase19::new);
    }

    @DisplayName("Bug-case 20")
    @TestFactory
    public Stream<DynamicTest> bugCase20() {
        return getDynamicTests(BugCase20::new);
    }
}
