package sbp.com.sbt.dataspace.graphqlschema;

import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClassEnum;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.EntitiesReadAccessJsonConfiguration;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.EntitiesReadAccessJsonSettings;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.SqlDialect;
import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.testcommon.TestCommonConfiguration;

import java.util.ArrayList;
import java.util.List;

@Import({
        TestCommonConfiguration.class,
        EntitiesReadAccessJsonConfiguration.class,
        GraphQLSchemaConfiguration.class
})
class GraphQLSchemaTestConfiguration {

    @Bean
    GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    @Bean
    @Profile("h2s1")
    EntitiesReadAccessJsonSettings h2EntitiesReadAccessJsonSettings1() {
        return new EntitiesReadAccessJsonSettings()
                .setSqlDialect(SqlDialect.H2);
    }

    @Bean
    @Profile("withUnderscore")
    @Primary
    public GraphQLSchemaSettings graphQLSchemaSettings() {
        return new GraphQLSchemaSettings()
                .setIdWithUnderscore()
                .setAggregateVersionWithUnderscore();
    }

    @Bean
    @Profile("withCalcExprFieldsOnEachType")
    @Primary
    public GraphQLSchemaSettings graphQLSchemaSettings2() {
        return new GraphQLSchemaSettings()
                .setCalcExprFieldsPlacement(CalcExprFieldsPlacement.ON_EACH_TYPE)
                .setGenerateElemsForSelection(true)
                .setGenerateStrExprVariableDefinitionDirective(true);
    }

    @Bean
    @ConditionalOnMissingBean(GraphQLSchemaSettings.class)
    public GraphQLSchemaSettings graphQLSchemaSettingsDefault() {
        return new GraphQLSchemaSettings()
                .setGenerateStrExprVariableDefinitionDirective(true);
    }

    @Bean
    public PdmModel pdmModelMock(ModelDescription modelDescription) {
        List<XmlModelClassEnum> enums = new ArrayList<>();
        for (EnumDescription enumItem : modelDescription.getEnumDescriptions().values()) {
            XmlModelClassEnum modelClassEnum = new XmlModelClassEnum(enumItem.getName(), "label", false);
            ArrayList<XmlEnumValue> xmlEnumValues = new ArrayList<>();
            for (String value : enumItem.getValues()) {
                XmlEnumValue xmlEnumValue = new XmlEnumValue(value, "label", "description");
                xmlEnumValues.add(xmlEnumValue);
            }
            modelClassEnum.setEnumValue(xmlEnumValues);
            enums.add(modelClassEnum);
        }
        PdmModel pdmModel = Mockito.mock(PdmModel.class);
        XmlModel xmlModel = Mockito.mock(XmlModel.class);
        Mockito.when(pdmModel.getModel()).thenReturn(xmlModel);
        Mockito.when(xmlModel.getEnums())
                .thenReturn(enums);
        return pdmModel;
    }
}
