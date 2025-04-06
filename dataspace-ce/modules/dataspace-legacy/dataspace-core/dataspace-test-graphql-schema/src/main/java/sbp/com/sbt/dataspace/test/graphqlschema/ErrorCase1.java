package sbp.com.sbt.dataspace.test.graphqlschema;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Case with errors 1<ul>
 * <li>Unused variables</li>
 * <li>Error during literal parsing</li>
 * <li>Validation error</li>
 * </ul>
 */
class ErrorCase1 extends ErrorCase {

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Unused Variables", "unusedVariables"),
            testData("Error during parsing of literal", "parseLiteralError"),
            testData("Error during parsing of literal (2)", "parseLiteralError2"),
            testData("Error during parsing of literal (3)", "parseLiteralError3"),
            testData("Error during parsing of literal (4)", "parseLiteralError4"),
            testData("Error during parsing of literal (5)", "parseLiteralError5"),
            testData("Error during parsing of literal (6)", "parseLiteralError6"),
            testData("Error during parsing of literal (7)", "parseLiteralError7"),
            testData("Error during parsing of literal (8)", "parseLiteralError8"),
            testData("Error during parsing of literal (9)", "parseLiteralError9"),
            testData("Error during parsing of literal (10)", "parseLiteralError10"),
            testData("Validation error", "validationError"),
            testData("Validation error (2)", "validationError2"),
            testData("Validation error (3)", "validationError3"),
            testData("Validation error (4)", "validationError4"),
            testData("Validation error (5)", "validationError5"),
            testData("Validation error (6)", "validationError6"),
            testData("Validation error (7)", "validationError7"),
            testData("Validation error (12)", "validationError12"),
            testData("Bad variable", "badVariable"),
            testData("Bad variable (2)", "badVariable2"),
            testData("Bad variable (3)", "badVariable3"),
            testData("Bad variable (4)", "badVariable4"),
            testData("Bad variable (5)", "badVariable5"),
            testData("Bad variable (6)", "badVariable6"),
            testData("Bad variable (7)", "badVariable7"),
            testData("Bad variable (8)", "badVariable8"),
            testData("Bad variable (9)", "badVariable9"),
            testData("Bad variable (10)", "badVariable10"));
    }
}
