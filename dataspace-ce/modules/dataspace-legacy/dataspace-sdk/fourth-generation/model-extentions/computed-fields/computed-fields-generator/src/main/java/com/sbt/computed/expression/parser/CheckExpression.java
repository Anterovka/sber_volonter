package com.sbt.computed.expression.parser;

import com.sbt.computed.expression.exception.CollectionForComputedFieldException;
import com.sbt.computed.expression.exception.ComputedExpressionException;
import com.sbt.computed.expression.exception.DefaultValueForComputedFieldException;
import com.sbt.computed.expression.exception.HistoricalForComputedFieldException;
import com.sbt.computed.expression.exception.MappedByForComputedFieldException;
import com.sbt.computed.expression.exception.ParentForComputedFieldException;
import com.sbt.computed.expression.parser.listener.ErrorListener;
import com.sbt.computed.expression.parser.listener.FullNameColumnListener;
import com.sbt.computed.expression.parser.listener.FullNameColumnListenerForChecker;
import com.sbt.computed.expression.parser.listener.IFullNameColumnListener;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.usermodel.UserXmlPropertySqlExpression;
import com.sbt.parameters.enums.Changeable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.sbt.mg.Helper.getTemplate;

public class CheckExpression {

    public static String PLACEHOLDER = "\\{\\s?[A-Z0-9\\.]+\\s?\\}";


    public enum ESqlExpressionDBMS {

        H2("h2"), ORACLE("oracle"), POSTGRESQL("postgresql");

        private String dbms;

        ESqlExpressionDBMS(String dbms) {
            this.dbms = dbms;
        }
    }


    public static String checkAndTransformCurrentExpression(XmlModelClassProperty property) {
        return replaceSubstring(property.getComputedExpression(), validator(new FullNameColumnListener(property), property.getComputedExpression()));
    }


    public static String expressionForOracle(String expression) {

        ExpressionLexer expressionLexer = new ExpressionLexer(CharStreams.fromString(expression));
        CommonTokenStream tokens = new CommonTokenStream(expressionLexer);
        ExpressionParser parser = new ExpressionParser(tokens);
        ExpressionParser.ComputedExpressionContext tree = parser.computedExpression();

        Function<ParseTree, String> fun = new Function<ParseTree, String>() {
            final StringBuilder stringBuilder = new StringBuilder();

            @Override
            public String apply(ParseTree a) {
                walkByTreeNode(a);
                return stringBuilder.toString();
            }

            public void walkByTreeNode(ParseTree a) {
                if (a.getChildCount() == 0) {
                    if (a.getParent() != null && a.getParent() instanceof ExpressionParser.BooleanLiteralContext) {
                        if (a.getText().equals("TRUE")) {
                            stringBuilder
                                    .append("1")
                                    .append(" ");
                        } else {
                            stringBuilder
                                    .append("0")
                                    .append(" ");
                        }

                    } else {
                        stringBuilder
                                .append(a.getText())
                                .append(" ");
                    }
                    return;
                }
                for (int i = 0; i < a.getChildCount(); i++) {
                    apply(a.getChild(i));
                }
            }
        };
        return fun.apply(tree);
    }


    public static List<PropertyLocation> validator(IFullNameColumnListener iFullNameColumnListener, String expression) {
        if (StringUtils.isEmpty(expression)) {
            return new ArrayList<>();
        }
        ErrorListener parserErrorListener = new ErrorListener();
        ErrorListener lexerErrorListener = new ErrorListener();

        ExpressionLexer expressionLexer = new ExpressionLexer(CharStreams.fromString(expression));
        expressionLexer.addErrorListener(lexerErrorListener);

        CommonTokenStream tokens = new CommonTokenStream(expressionLexer);

        ExpressionParser parser = new ExpressionParser(tokens);
        parser.addErrorListener(parserErrorListener);

        ExpressionParser.ComputedExpressionContext tree = parser.computedExpression();
        //FullNameColumnListener listener = new FullNameColumnListener(property);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(iFullNameColumnListener, tree);

        return iFullNameColumnListener.getListForReplace();
    }

    private static String replaceSubstring(final String str, final List<PropertyLocation> listForReplace) {
        Collections.sort(listForReplace, (a, b) -> b.start - a.start);
        final StringBuilder stringBuilder = new StringBuilder(str);
        listForReplace.forEach(p
                -> stringBuilder.replace(p.start, p.end + 1, p.getColumnName()));
        return stringBuilder.toString();
    }

    public static class PropertyLocation {
        XmlModelClassProperty property;
        XmlEmbeddedProperty embeddedProperty;
        int start;
        int end;
        boolean isEmbeddedClassProperty = false;


        public PropertyLocation(XmlModelClassProperty property, int start, int end) {
            this.start = start;
            this.end = end;
            this.property = property;
        }

        public PropertyLocation(XmlModelClassProperty property, XmlEmbeddedProperty embeddedProperty, int start, int end, boolean isEmbeddedClassProperty) {
            this.start = start;
            this.end = end;
            this.property = property;
            this.embeddedProperty = embeddedProperty;
            this.isEmbeddedClassProperty = isEmbeddedClassProperty;
        }

        public String getColumnName() {
            if (isEmbeddedClassProperty) {
                return this.embeddedProperty.getColumnName();
            } else {
                return this.property.getColumnName();
            }
        }
    }

    public static String computedExpressionAttribute(XmlModelClassProperty property) {
        String expression = property.getComputedExpression();
        if (StringUtils.isEmpty(expression)) {
            return "";

        }
        return "computed=\"" + checkAndTransformCurrentExpression(property) + '"';
    }

    public static class ComputedFieldsGenerator implements ModelGenerate {
        public static final String PROJECT_NAME = "COMPUTED-FIELD";
        private static final String ADD_COLUMN_TEMPLATE = getTemplate("/templates/addComputedColumn.changelog.template");
        private static final String ADD_COLUMN_WITHOUT_ORACLE_TEMPLATE = getTemplate("/templates/addComputedColumn.changelog.withoutOracle.template");
        private static final String COLUMN_NAME_WC = "${columnName}";
        private static final String INDEX_WC = "${index}";
        private static final String TABLE_NAME_WC = "${tableName}";
        private static final String VALUE_COMPUTED_FOR_H2_WC = "${valueComputedForH2}";
        private static final String VALUE_COMPUTED_FOR_ORACLE_WC = "${valueComputedForOracle}";
        private static final String VALUE_COMPUTED_FOR_POSTGRESQL_WC = "${valueComputedForPostgreSQL}";

        private static final String TYPE_WC = "${type}";
        private static final String TYPE_ORACLE_WC = "${typeOracle}";
        private static final String NOTNULL = "${notNULL}";
        private List<XmlModelClassProperty> computedPropertiesList = new ArrayList<>();

        @Override
        public int getPriority() {
            return 60;
        }

        @Override
        public String getProjectName() {
            return PROJECT_NAME;
        }

        @Override
        public void preInit(XmlModel model, PluginParameters pluginParameters) {
            Optional<XmlImport> xmlImport = model.getImports().stream()
                    .filter(imp -> PROJECT_NAME.equals(imp.getType()))
                    .findAny();
            if (!xmlImport.isPresent()) {
                model.getImports().add(new XmlImport(PROJECT_NAME, null));
            }
        }

        @Override
        public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
            model.getClassesAsList()
                    .forEach(clazz -> clazz.getPropertiesAsList()
                            .forEach(property ->
                            {
                                if (property.getComputedExpression() != null
                                        && !property.getComputedExpression().isEmpty()
                                        || !property.getSqlExpressions().isEmpty()) {
                                    if (property.getDefaultValue() != null) {
                                        throw new DefaultValueForComputedFieldException(property);
                                    } else if (property.isHistorical()) {
                                        throw new HistoricalForComputedFieldException(property);
                                    } else if (property.isParent()) {
                                        throw new ParentForComputedFieldException(property);
                                    } else if (property.getMappedBy() != null) {
                                        throw new MappedByForComputedFieldException(property);
                                    } else if (property.getCollectionType() != null) {
                                        throw new CollectionForComputedFieldException(property);
                                    } else if (property.getComputedExpression() != null && !property.getSqlExpressions().isEmpty()) {
                                        throw new ComputedExpressionException(String.format("On the property %s of the class %s, a computed expression using <sql-expr> has been set." +
                                            "general expression in the body <property> ", property.getName(), clazz.getName()), "Remove computed expressions from <sql-expr> or from within the <property> body");
                                    } else if (!property.getSqlExpressions().isEmpty()) {

                                        if (property.getSqlExpressions().stream().filter(p -> p.getDbms() == null).count() > 1) {
                                            throw new ComputedExpressionException(
                                                String.format("On the %s property of the %s class, more than one <sql-expr> element is set without specifying the dbms attribute ", property.getName(), clazz.getName())
                                                , "При задании вычисляемых выражений через <sql-expr> допустимо объявлять только один элемент без указания атрибута dbms.");
                                        }


                                        List<String> dbmsList = property.getSqlExpressions()
                                                .stream()
                                                .filter(p -> p.getDbms() != null)
                                                .flatMap(p -> Arrays.stream(p.getDbms().split(",")))
                                                .collect(Collectors.toList());


                                        if (dbmsList.size() > 0 && dbmsList.stream()
                                                .allMatch(p -> Arrays.stream(ESqlExpressionDBMS.values()).allMatch(e -> !p.equalsIgnoreCase(e.name())))) {
                                            throw new ComputedExpressionException(String.format("On the %s property of the %s class in the dbms attribute of the <sql-expr> element, an incorrect value is set.", property.getName(), clazz.getName())
                                                , "При задании вычисляемых выражений через <sql-expr> для атрибута dbms допустимы значения: h2, PostgreSQL, Oracle");
                                        }

                                        if (dbmsList.size() != dbmsList.stream().distinct().count()) {
                                            throw new ComputedExpressionException(String.format("On the property %s of the class %s in the dbms attribute of the <sql-expr> element, values are duplicated.", property.getName(), clazz.getName())
                                                , "При задании вычисляемых выражений через <sql-expr> для атрибута dbms допустимы значения (повторения не допускаются): h2, PostgreSQL, Oracle.");

                                        }

                                        if (dbmsList.size() < 3
                                                && property.getSqlExpressions()
                                                .stream()
                                                .filter(p -> p.getDbms() == null)
                                                .findFirst()
                                                .isEmpty()) {
                                            throw new ComputedExpressionException(String.format("On the property %s of the class %s, the default element <sql-expr> is not specified.", property.getName(), clazz.getName())
                                                , "При задании вычисляемых выражений через <sql-expr> необходимо указать элемент по умолчанию или указать элементы для всех доступных значений атрибута dbms:  h2, PostgreSQL, Oracle.");
                                        }
                                    } else if (!property.getComputedExpression().isEmpty()) {
                                        CheckExpression.validator(new FullNameColumnListenerForChecker(property), property.getComputedExpression());
                                    }
                                    property.setChangeable(Changeable.READ_ONLY);
                                    computedPropertiesList.add(property);
                                }
                            }));
        }

        public static String addChangeSets(MutableLong index, String tableName, XmlModelClassProperty property, PluginParameters pluginParameters) {
            final StringBuilder liquibase = new StringBuilder();
            index.increment();

            String addComputedColumnTemplate = ADD_COLUMN_TEMPLATE;

            if (!property.getSqlExpressions().isEmpty()) {

            }


            if (pluginParameters.isDisableGenerateOracleLiquibase()) {
                addComputedColumnTemplate = ADD_COLUMN_WITHOUT_ORACLE_TEMPLATE;
            }

            var expressionMap = getExpressionMap(property);
            liquibase.append(addComputedColumnTemplate
                    .replace(INDEX_WC, "computed-" + index.getValue())
                    .replace(TABLE_NAME_WC, tableName)
                    .replace(COLUMN_NAME_WC, property.getName().toUpperCase())
                    .replace(VALUE_COMPUTED_FOR_H2_WC, expressionMap.get(ESqlExpressionDBMS.H2))
                    .replace(VALUE_COMPUTED_FOR_ORACLE_WC, expressionMap.get(ESqlExpressionDBMS.ORACLE))
                    .replace(VALUE_COMPUTED_FOR_POSTGRESQL_WC, expressionMap.get(ESqlExpressionDBMS.POSTGRESQL))
                    .replace(TYPE_WC, property.getTypeInfo().getOracleType() != null ? property.getTypeInfo().getOracleName() : property.getTypeInfo().getDbType())
                    .replace(TYPE_ORACLE_WC, (property.getTypeInfo().getOracleType() != null ? property.getTypeInfo().getOracleName() : property.getTypeInfo().getDbType()).replace("VARCHAR(", "VARCHAR2("))
                    .replace(NOTNULL, property.isMandatory() ? "NOT NULL" : "")
                    .replace("${rollback}", pluginParameters.isOptimizeChangelog() ?
                            "\n\t\t<rollback/>" :
                            String.format("\n\t\t<rollback>\n" +
                                    "             <dropColumn tableName=\"%s\">\n" +
                                    "                <column  name=\"%s\"/>\n" +
                                    "             </dropColumn>\n" +
                                    "        </rollback>", property.getModelClass().getTableName(), property.getName().toUpperCase())));
            return liquibase.toString();
        }

        private static Map<ESqlExpressionDBMS, String> getExpressionMap(XmlModelClassProperty property) {
            if (!property.getSqlExpressions().isEmpty()) {
                return getSqlExpressionsByDBMS(property);
            }
            return Arrays.stream(ESqlExpressionDBMS.values())
                    .map(dbms -> {
                        if (dbms == ESqlExpressionDBMS.ORACLE) {
                            return new AbstractMap.SimpleEntry<ESqlExpressionDBMS, String>(dbms, expressionForOracle(checkAndTransformCurrentExpression(property)));
                        }
                        return new AbstractMap.SimpleEntry<ESqlExpressionDBMS, String>(dbms, checkAndTransformCurrentExpression(property));
                    })
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        }

        /**
         * All checks have been passed above in initModel
         */
        private static Map<ESqlExpressionDBMS, String> getSqlExpressionsByDBMS(XmlModelClassProperty property) {
            Map<ESqlExpressionDBMS, String> mapSql = property.getSqlExpressions()
                .stream()
                .flatMap(p -> {
                    if (p.getDbms() != null) {
                        return Arrays.stream(p.getDbms().split(","))
                            .map(UserXmlPropertySqlExpression::new)
                            .peek(e -> e.setComputedExpression(p.getComputedExpression()));
                    } else {
                        UserXmlPropertySqlExpression userXmlPropertySqlExpression = new UserXmlPropertySqlExpression();
                        userXmlPropertySqlExpression.setComputedExpression(p.getComputedExpression());
                        return Stream.of(userXmlPropertySqlExpression);

                    }
                })
                .collect(Collectors
                    .toMap(p -> {
                            if (p.getDbms() != null) {
                                return ESqlExpressionDBMS.valueOf(p.getDbms().toUpperCase());
                            } else {
                                return null;
                            }
                        }
                        , p -> replacePlaceholder(property, p))
                );

            return Arrays.stream(ESqlExpressionDBMS.values())
                    .collect(Collectors.toMap(
                        v -> v, v -> {
                                if (mapSql.get(v) != null) {
                                    return mapSql.get(v);
                                } else {
                                    return mapSql.get(null);
                                }
                            }

                    ));

        }


        private static String replacePlaceholder(XmlModelClassProperty property, UserXmlPropertySqlExpression
                expression) {
            final XmlModelClass classProperty = property.getModelClass();

            Pattern pattern = Pattern.compile(PLACEHOLDER);
            Matcher matcher = pattern.matcher(expression.getComputedExpression());

            var mapFields = new HashMap<String, String>();
            final String[] finalExpression = {expression.getComputedExpression()};

            while (matcher.find()) {
                mapFields.put(matcher.group().replaceAll("\\s?[\\{\\}]+\\s?", ""), null);
            }

            for (var s : mapFields.entrySet()) {
                if (!s.getKey().contains(".")) {
                    classProperty.getPropertiesAsList()
                            .stream()
                            .filter(p -> p.getName().equalsIgnoreCase(s.getKey()))
                            .findFirst()
                            .ifPresentOrElse(p -> mapFields.put(s.getKey(), p.getColumnName()), () -> {
                                throw new ComputedExpressionException(
                                    String.format("In the class %s there is no property %s that the computed property expression refers to",
                                                classProperty.getName(),
                                                s.getKey(),
                                                property.getName()),
                                    String.format(" Check the correctness of the computed expression in the %s property",
                                                property.getName()));
                            });
                } else if (s.getKey().split("\\.").length == 2) {
                    String[] split = s.getKey().split("\\.");
                    classProperty.getEmbeddedPropertyList()
                            .stream()
                            .filter(p -> p.getName().equalsIgnoreCase(split[0]))
                            .findFirst()
                            .ifPresentOrElse(
                                    p -> {
                                        p.getEmbeddedPropertyList()
                                                .stream()
                                                .filter(e -> e.getName().equalsIgnoreCase(split[1]))
                                                .findFirst()
                                                .ifPresentOrElse(
                                                        p1 -> {
                                                            mapFields.put(s.getKey(), p1.getColumnName());
                                                        },
                                                        () -> {
                                                            throw new ComputedExpressionException(
                                                                String.format("In the class %s in the computed expression on the property %s an invalid class property %s is set",
                                                                            classProperty.getName(),
                                                                            property.getName(),
                                                                            s),
                                                                String.format(" Check the correctness of the computed expression in the %s property",
                                                                            property.getName()));
                                                        }
                                                );
                                    }
                                    , () -> {
                                        throw new ComputedExpressionException(
                                            String.format("In the class %s in the computed expression on the property %s an invalid class property %s is set ",
                                                        classProperty.getName(),
                                                        property.getName(),
                                                        s),
                                            String.format(" Check the correctness of the computed expression in the %s property",
                                                        property.getName()));
                                    });
                } else {
                    throw new ComputedExpressionException(
                        String.format("Invalid property name %s in computed expression on property %s of class %s.",
                                    s, property.getName(), classProperty.getName())
                        , "Check the correctness of class fields in the computed expression.");
                }
            }

            mapFields.entrySet()
                    .stream()
                    .forEach(field -> {
                        finalExpression[0] = finalExpression[0].replaceAll("\\{\\s*" + field.getKey() + "\\s*\\}", field.getValue());
                    });


            return finalExpression[0];
        }
    }
}
