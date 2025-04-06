package sbp.com.sbt.dataspace.feather.stringexpressions.examples;

/**
 * The search condition by id equal to '1'
 */
class FindByIdConditionExample {

    /**
     * Execute
     *
     * @return The search condition for id equal to '1'
     */
    String run() {
        return "root.$id == '1'";
    }
}
