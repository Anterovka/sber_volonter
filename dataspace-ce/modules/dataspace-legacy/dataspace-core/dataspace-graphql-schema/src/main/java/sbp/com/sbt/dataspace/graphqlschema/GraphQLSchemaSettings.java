package sbp.com.sbt.dataspace.graphqlschema;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * GraphQL Schema Settings
 */
public final class GraphQLSchemaSettings {

    boolean idWithUnderscore;
    boolean aggregateVersionWithUnderscore;
    boolean useFasterXmlBase64Decoder;
    boolean useJsonRpcBindMode;
    CalcExprFieldsPlacement calcExprFieldsPlacement = CalcExprFieldsPlacement.ON_SEPARATE_TYPE;
    boolean suppressCalcDateTimeCoercingErrors = false;
    boolean generateElemsForSelection = false;
    boolean generateStrExprVariableDefinitionDirective = false;
    boolean generateStrExprFieldDirective = true;
    boolean generateStrExprField = false;

    /**
     * Should I use an underscore for id?
     */
    public boolean isIdWithUnderscore() {
        return idWithUnderscore;
    }

    /**
     * Set the flag for using an underscore for id
     *
     * @return Current settings
     */
    public GraphQLSchemaSettings setIdWithUnderscore() {
        this.idWithUnderscore = true;
        return this;
    }

    public GraphQLSchemaSettings setIdWithUnderscore(boolean idWithUnderscore) {
        this.idWithUnderscore = idWithUnderscore;
        return this;
    }

    /**
     * Should I use an underscore for the aggregate version?
     */
    public boolean isAggregateVersionWithUnderscore() {
        return aggregateVersionWithUnderscore;
    }

    /**
     * Set the flag for using the underscore for the aggregate version
     *
     * @return Current settings
     */
    public GraphQLSchemaSettings setAggregateVersionWithUnderscore() {
        this.aggregateVersionWithUnderscore = true;
        return this;
    }

    public GraphQLSchemaSettings setAggregateVersionWithUnderscore(boolean aggregateVersionWithUnderscore) {
        this.aggregateVersionWithUnderscore = aggregateVersionWithUnderscore;
        return this;
    }

    public String getIdFieldName() {
        return idWithUnderscore ? GraphQLSchemaHelper.ID_WITH_UNDERSCORE_FIELD_NAME : GraphQLSchemaHelper.ID_FIELD_NAME;
    }

    public String getAggregateVersionFieldName() {
        return aggregateVersionWithUnderscore ? GraphQLSchemaHelper.AGGREGATE_VERSION_WITH_UNDERSCORE_FIELD_NAME : GraphQLSchemaHelper.AGGREGATE_VERSION_FIELD_NAME;
    }

    public boolean isUseFasterXmlBase64Decoder() {
        return useFasterXmlBase64Decoder;
    }

    public void setUseFasterXmlBase64Decoder(boolean useFasterXmlBase64Decoder) {
        this.useFasterXmlBase64Decoder = useFasterXmlBase64Decoder;
    }

    public boolean isUseJsonRpcBindMode() {
        return useJsonRpcBindMode;
    }

    public void setUseJsonRpcBindMode(boolean useJsonRpcBindMode) {
        this.useJsonRpcBindMode = useJsonRpcBindMode;
    }

    public CalcExprFieldsPlacement getCalcExprFieldsPlacement() {
        return calcExprFieldsPlacement;
    }

    public GraphQLSchemaSettings setCalcExprFieldsPlacement(CalcExprFieldsPlacement calcExprFieldsPlacement) {
        this.calcExprFieldsPlacement = calcExprFieldsPlacement;
        return this;
    }

    /**
     * Should an error be thrown if the return value type does not match the field type in calculated date and time fields
     */
    public boolean isSuppressCalcDateTimeCoercingErrors() {
        return suppressCalcDateTimeCoercingErrors;
    }

    public void setSuppressCalcDateTimeCoercingErrors(boolean suppressCalcDateTimeCoercingErrors) {
        this.suppressCalcDateTimeCoercingErrors = suppressCalcDateTimeCoercingErrors;
    }

    public boolean isGenerateElemsForSelection() {
        return generateElemsForSelection;
    }

    public GraphQLSchemaSettings setGenerateElemsForSelection(boolean generateElemsForSelection) {
        this.generateElemsForSelection = generateElemsForSelection;
        return this;
    }

    public boolean isGenerateStrExprVariableDefinitionDirective() {
        return generateStrExprVariableDefinitionDirective;
    }

    public GraphQLSchemaSettings setGenerateStrExprVariableDefinitionDirective(boolean generateStrExprVariableDefinitionDirective) {
        this.generateStrExprVariableDefinitionDirective = generateStrExprVariableDefinitionDirective;
        return this;
    }

    public boolean isGenerateStrExprFieldDirective() {
        return generateStrExprFieldDirective;
    }

    public void setGenerateStrExprFieldDirective(boolean generateStrExprFieldDirective) {
        this.generateStrExprFieldDirective = generateStrExprFieldDirective;
    }

    public boolean isGenerateStrExprField() {
        return generateStrExprField;
    }

    public GraphQLSchemaSettings setGenerateStrExprField(boolean generateStrExprField) {
        this.generateStrExprField = generateStrExprField;
        return this;
    }

    @Override
    public String toString() {
        return getFullDescription("GraphQL Schema Settings",
            param("Whether to use an underscore for id", idWithUnderscore),
            param("Whether to use an underscore for the aggregate version", aggregateVersionWithUnderscore),
            param("Use Base64 decoder from FasterXML library", useFasterXmlBase64Decoder),
            param("Location of fields for computed expressions", calcExprFieldsPlacement),
            param("Generate elements for selection", generateElemsForSelection),
            param("Whether to generate the @strExpr directive for variable definitions", generateStrExprVariableDefinitionDirective),
            param("Whether to generate the @strExpr directive for fields", generateStrExprFieldDirective),
            param("Generate field strExpr", generateStrExprField)
        );
    }
}
