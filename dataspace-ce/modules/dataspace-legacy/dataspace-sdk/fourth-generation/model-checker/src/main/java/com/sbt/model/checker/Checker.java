package com.sbt.model.checker;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.sbt.aggregator.AggregateGenerator;
import com.sbt.base.model.generator.BaseEntityGenerator;
import com.sbt.computed.expression.parser.CheckExpression;
import com.sbt.dataspace.applocks.model.generator.AppLocksModelGenerate;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.dataspace.pdm.exception.ModelVersionException;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.dataspace.security.model.generator.GraphQlSecurityModelGenerate;
import com.sbt.dictionary.DictionaryGenerator;
import com.sbt.dictionary.exceptions.DictionaryClassDeleteException;
import com.sbt.dictionary.exceptions.DictionaryPropertyDeleteException;
import com.sbt.idempotence.model.generator.IdempotenceModelGenerate;
import com.sbt.mg.ElementState;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.NameHelper;
import com.sbt.mg.Pair;
import com.sbt.mg.data.addons.XmlModelClassPropertyEmbeddable;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.MergeKind;
import com.sbt.mg.data.model.PropertyExtractResult;
import com.sbt.mg.data.model.PropertyType;
import com.sbt.mg.data.model.TypeInfo;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelExternalType;
import com.sbt.mg.data.model.XmlModelInterface;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.data.model.XmlQueryDBMS;
import com.sbt.mg.data.model.XmlQueryParam;
import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.mg.data.model.typedef.XmlTypeDef;
import com.sbt.mg.data.model.typedef.XmlTypeDefs;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedColumn;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedSchemaItems;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedTable;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.SdkException;
import com.sbt.mg.exception.checkmodel.CloneablePositionException;
import com.sbt.mg.exception.checkmodel.ColumnNameAlreadyDefinedException;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddedPropertyException;
import com.sbt.mg.exception.checkmodel.NullTypeException;
import com.sbt.mg.exception.checkmodel.TypeDefDuplicationException;
import com.sbt.mg.exception.checkmodel.UnsupportedListReferenceCollectionException;
import com.sbt.mg.exception.checkmodel.UnsupportedSimpleListReferenceCollectionException;
import com.sbt.mg.exception.common.EmbeddedListNotFoundException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.cci.ModelCciLogic;
import com.sbt.model.checker.inner.root.ChangelogChecker;
import com.sbt.model.checker.inner.root.ClassChecker;
import com.sbt.model.checker.inner.root.IdempotenceExcludeSectionRootChecker;
import com.sbt.model.checker.inner.root.NonStringPropsWithMaskRootChecker;
import com.sbt.model.checker.inner.root.PluginPropertiesChecker;
import com.sbt.model.checker.inner.root.PropertyChecker;
import com.sbt.model.checker.inner.root.RootChecker;
import com.sbt.model.checker.inner.root.XmlModelChecker;
import com.sbt.model.diff.BaseModelDiff;
import com.sbt.model.diff.ClassDiff;
import com.sbt.model.diff.EnumDiff;
import com.sbt.model.diff.EventDiff;
import com.sbt.model.diff.IndexDiff;
import com.sbt.model.diff.PropertyDiff;
import com.sbt.model.diff.QueryDiff;
import com.sbt.model.exception.*;
import com.sbt.model.exception.parent.CheckModelException;
import com.sbt.model.foreignkey.ModelForeignKeyLogic;
import com.sbt.model.index.ModelIndexLogic;
import com.sbt.model.index.ModelIndexUtils;
import com.sbt.model.index.exception.TooLongValueException;
import com.sbt.model.utils.Models;
import com.sbt.parameters.dto.SysVersionFillParams;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.ObjectLinks;
import com.sbt.reference.ExternalReferenceGenerator;
import com.sbt.sysversion.utils.semver.SemVerUtils;
import org.apache.commons.lang3.StringUtils;
import sbp.com.sbt.dataspace.extension.status.StatusGenerator;
import sbp.com.sbt.dataspace.utils.ClassPathUtils;
import sbp.com.sbt.semver.Semver;
import sbp.com.sbt.semver.exceptions.SemverParseException;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sbt.dataspace.pdm.ModelGenerateUtils.getModelVersion;
import static com.sbt.dataspace.pdm.StreamModel.MODEL_STREAM_NAME;
import static com.sbt.mg.Helper.isSnapshotVersion;
import static com.sbt.mg.ModelHelper.TYPES_INFO;
import static com.sbt.mg.ModelHelper.XML_MAPPER;
import static com.sbt.mg.ModelHelper.findClosestNotAbstractChilds;
import static com.sbt.mg.ModelHelper.isModelClassType;
import static com.sbt.mg.ModelHelper.isReferenceType;
import static com.sbt.mg.ModelHelper.validateTypeName;
import static com.sbt.mg.data.model.XmlModelClass.DEPRECATED_TAG;
import static com.sbt.mg.jpa.JpaConstants.ENTITY_CLASS_NAME;
import static com.sbt.mg.jpa.JpaConstants.JPA_DISCRIMINATOR_NAME;
import static com.sbt.mg.jpa.JpaConstants.MAX_DATE_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.OBJECT_ID_PREFIX;
import static com.sbt.mg.utils.ClassUtils.isBaseClass;
import static com.sbt.model.checker.CheckerUtils.discriminatorField;
import static com.sbt.model.checker.CheckerUtils.obtainModel;
import static com.sbt.model.diff.MessageHandler.getMessageRemoveClass;
import static com.sbt.model.diff.MessageHandler.getMessageRemoveProperty;
import static com.sbt.model.foreignkey.ForeignKeyHelper.getForeignKeyCondition;
import static com.sbt.model.foreignkey.ForeignKeyHelper.isEnableCreateForeignKeys;
import static com.sbt.model.foreignkey.ForeignKeyHelper.isEnableDropForeignKeys;
import static com.sbt.model.foreignkey.ForeignKeyHelper.prepareDropForeignKey;
import static com.sbt.model.foreignkey.ForeignKeyHelper.prepareGenerationForeignKey;
import static com.sbt.model.foreignkey.ForeignKeyHelper.transferFkPropertiesFromOldModel;
import static com.sbt.model.utils.Models.getTypeInfoForProperty;

public class Checker {

    /**
     * Wrapper over class property that is reference-based (external link).
     * property - external link
     * className - name of the class referenced by the field without the postfix "Reference"
     */
    static class PropertyInClass {
        /**
         * Property - external link
         */
        public final XmlModelClassProperty property;
        /**
         * Name of the class referred to by the link (without the postfix Reference)
         */
        public final String className;

        public PropertyInClass(XmlModelClassProperty property, String className) {
            this.property = property;
            this.className = className;
        }

    }

    private static final Set<String> RESERVED_CLASS_NAMES =
        new HashSet<>(Arrays.asList("RootDictionary", "Aggregate", "BaseAggregateEntity", ENTITY_CLASS_NAME));

    private static final Set<String> RESERVED_PROPERTY_NAMES;

    static {
        RESERVED_PROPERTY_NAMES = new HashSet<>(ModelHelper.SYSTEM_FIELDS);
        RESERVED_PROPERTY_NAMES.addAll(Arrays.asList("object", "aggregateRoot", "toJson", "create", "createCollection", JPA_DISCRIMINATOR_NAME, JpaConstants.ID_NAME));
    }

    private static final Logger LOGGER = Logger.getLogger(Checker.class.getName());

    private static final String DATE_TIME_PATTERN_T = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_TIME_PATTERN_SPACE = "yyyy-MM-dd HH:mm:ss";
    private static final List<DateFormat> DATE_FORMATTERS = new ArrayList<>() {{
        add(new SimpleDateFormat(DATE_TIME_PATTERN_T));
        add(new SimpleDateFormat(DATE_TIME_PATTERN_SPACE));
    }};
    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = new ArrayList<>() {{
        add(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_T));
        add(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_SPACE));
    }};
    private static final List<DateTimeFormatter> OFFSET_FORMATTERS = new ArrayList<>() {{
        add(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        add(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_SPACE).withZone(ZoneOffset.UTC));
    }};

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private final StreamModel streamModel;
    private final XmlModel model;
    private final PdmModel pdmModel;
    private final PdmModel immutablePreviousPdmModel;
    private final String changelog;
    ParameterContext parameterContext;
    boolean disableCompatibilityCheck;
    private final List<String> warnings = new ArrayList<>();

    private final List<XmlModelClass> removedClasses = new ArrayList<>();

    private final List<ModelGenerate> modelGenerates = new ArrayList<>() {{
        add(new AggregateGenerator());
        add(new AppLocksModelGenerate());
        add(new BaseEntityGenerator());
        add(new DictionaryGenerator());
        add(new ExternalReferenceGenerator());
        add(new IdempotenceModelGenerate());
        add(new GraphQlSecurityModelGenerate());
        add(new CheckExpression.ComputedFieldsGenerator());
        add(new StatusGenerator());
    }};

    public Checker(StreamModel streamModel) {
        this(streamModel, null, false);
    }

    /**
     * Translates StreamModel to XmlModel and PdmModel
     */
    public Checker(StreamModel streamModel, ParameterContext parameterContext, boolean disableCompatibilityCheck) {
        this.streamModel = streamModel;
        // pdm.xml can be, and may not be, if this is the first build of the model or if the flag localDeploy is not set
        this.pdmModel = streamModel.getPdm() == null ? null : PdmModel.readModelByInputStream(streamModel.getPdm());
        // model.xml will always be accessible, as it is the starting point for all generation
        this.model = obtainModel(streamModel, parameterContext == null ? ParameterContext.emptyContext() : parameterContext, pdmModel);
        // The same situation applies to the changelog as well.
        this.changelog = streamModel.getChangelogAsString();
        this.immutablePreviousPdmModel = streamModel.getImmutablePdm() == null ? null : PdmModel.readModelByInputStream(streamModel.getImmutablePdm());
        this.parameterContext = parameterContext == null ? ParameterContext.emptyContext() : parameterContext;
        this.disableCompatibilityCheck = disableCompatibilityCheck;
    }

    public List<String> check() {

        this.warnings.clear();

        ModelParameters modelParameters = parameterContext.getModelParameters();
        modelParameters.setWarnings(warnings);
        modelParameters.setCurrentVersion(this.pdmModel == null ? null : pdmModel.getModel().getVersion());
        PluginParameters pluginParameters = parameterContext.getPluginParameters();

        final String rawModelVersion = getModelVersion(modelParameters, model);
        checkModelVersion(rawModelVersion);
        final String modelVersion = SemVerUtils.removeBuild(rawModelVersion);
        this.model.setVersion(modelVersion);
        if (this.immutablePreviousPdmModel != null) {
            modelParameters.setImmutablePreviousPdmModel(this.immutablePreviousPdmModel);
        }
        pluginParameters.setSysVersionFillParams(
            new SysVersionFillParams(
                String.format("%s:%s", model.getModelName().toLowerCase(Locale.ENGLISH), modelVersion),
                modelVersion,
                //todo: not sure if assigning null looks like the right solution; maybe an empty string should be returned
                pluginParameters.isOptimizeChangelog() ? String.format("%s:%s", model.getModelName().toLowerCase(Locale.ENGLISH), modelParameters.getPreviousModelVersion()) : null,
                pluginParameters.isOptimizeChangelog() ? modelParameters.getPreviousModelVersion() : null)
        );

        BackCompatibilityHandler.handlePdmModel(this.pdmModel);
        // Add links to the model and pdm to modelParameters
        modelParameters.setModel(this.model);
        if (this.pdmModel != null) {
            modelParameters.setPdmModel(this.pdmModel);
        }

        modelGenerates.forEach(modelGenerate -> modelGenerate.addServiceClassesToModel(this.model, pluginParameters));

        // Checking the correctness of alias writing and replacing aliases with real types in class and interface properties
        checkAndReplaceAliasesTypes();

        final List<RootChecker> checkers = new ArrayList<>();
        checkers.add(new XmlModelChecker(this.pdmModel == null ? null : this.pdmModel.getModel(), this.model));
        checkers.add(new NonStringPropsWithMaskRootChecker(this.model));
        checkers.add(new IdempotenceExcludeSectionRootChecker(this.model));
        checkers.add(new ClassChecker(this.model));
        checkers.add(new PropertyChecker(this.model, parameterContext));
        checkers.add(new PluginPropertiesChecker(this.pdmModel == null ? null : pdmModel.getMetaInformation(), pluginParameters));

        checkers.forEach(RootChecker::check);

        setManualNameFlagForIndexes();

        checkVersionedEntities(pluginParameters);

        if (pdmModel != null) {
            restorePkIndexNameOnCollectionProperties();
        }

        // for importing third generation models.The probability of use is extremely low
        addModelViaImports();

        checkDeprecatedTagNewElements();

        // Moving from pdm.xml section XmlUnusedSchemaItems
        addXmlUnusedSchemaItemsToNewModel();
        // Checking remote elements from model (detailed description in the method header)
        // pluginParameters.isDeprecatedDeletedItems() - switch of version of algorithm of deletion of elements of model (for more details see AbstractPlugin.deprecatedDeletedItems)
        if (!pluginParameters.isDeprecateDeletedItems() && !pluginParameters.isAllowDeleteNonDeprecatedItems()) {
            checkDeletedElementsFromModel(parameterContext.getModelParameters());
        }

        addMarkIsDeprecatedToProperties();

        // We find the deleted enums, mark them as deprecated, and copy them to the new model
        addRemovedEnumsToNewModel();
        // setting the flag of enum on class and interface properties
        markEnumProperties();
        // Adds information about the deleted classes to the model and adds them to the diff in the "deleted" section.
        // Adds removed classes to the Checker.removedClasses
        if (pluginParameters.isDeprecateDeletedItems()) {
            addRemovedClassesToNewModel(modelParameters);
            markRemovedEvents();
        }
        // Moves deleted requests to a new model
        addRemovedQueriesToNewModel(parameterContext.getModelParameters());
        // Restoring backlinks to parent classes of deleted items.
        addRemovedMappedByPropertyToNewModel(parameterContext.getModelParameters());


        checkNoHistoryAttributesOnNewModel();
        checkHistoryTablesNotOverridden();
        // Restore collection external links to classes from the previous model.
        // The deleted are restored only if we use the old deletion mechanism
        // (regarding the new deletion mechanism, see the description for the parameter AbstractPlugin.deprecateDeletedItems)
        addCollectionReferencesFromPreviousToNewModel(pluginParameters.isDeprecateDeletedItems());
        if (pluginParameters.isDeprecateDeletedItems()) {
            // Copying deleted fields of embeddable classes
            addRemovedPropertiesToEmbeddableClasses();
            // Copy deleted embedded class fields
            addRemovedEmbeddedProperties();
        }
        // Checking that there are no property and reference to deleted classes
        checkDeprecatedFlag();
        // Validation of names: models, classes, class properties, class external references, property types, and external references
        // enum and their values
        checkCorrectNaming();
        // check that object labels do not exceed 254 characters
        checkLabelLengthOverHead();

        // Checking that embeddable classes do not extend any other, and that no one extends embeddable classes
        checkEmbeddedDoesNotHaveExtends();
        //Checking that there are no abstract embeddable classes (as they cannot be extended)
        checkEmbeddedDoesNotAbstract();

        // We set the classAccess attribute to UPDATE for all classes.
        //No system classes are currently present in the model.
        makeAllClassesUpdatable();

        // Set the base class flag
        ModelHelper.initBaseMarkClass(this.model);
        // Check if the lockable attribute is set to true only for base classes and not marked as true for reference books
        checkLocks();
        // We check that the externalLink flag is not set manually on the properties
        checkNoManualExternalLink();

        // Checking the mappedBy properties
        checkMappedByProperties();

        // ******************
        // launching generators
        executeExtensions();

        // rerun setting the base class flag after generator processing (just in case)
        ModelHelper.initBaseMarkClass(this.model);

        // Checks that the name of the enum does not overlap with the name of the class, interface, alias or primitive type
        checkEnums();
        // setting the flag of enum on class and interface properties
        markEnumProperties();
        // Checks for user requests
        checkQueries();
        // Checking that the class strategy (JOINED, SINGLE_TABLE) is set only for base classes, except historicality classes
        checkOnlyBaseMarkInheritanceStrategy();
        // Copying class strategies from base to descendants
        ModelHelper.initStrategy(model);
        // If the field has an embeddable type, then it sets the corresponding flag on the field; otherwise, it checks that the flag is not set.
        model.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList()
                .forEach(this::checkAndInitCorrectEmbeddedProperties));
        // Checking that the length of the string parameter does not exceed 4000
        checkStringLengthOverhead();
        // Check if the length of unicodestring does not exceed 2000
        checkUnicodeStringLengthOverhead();
        // Checking that the BigDecimal precision does not exceed 38
        checkBigDecimalLengthOverhead();
        // Check for String, unicodeString, and bigDecimal that the length is greater than 0
        checkMinimumLength();
        // Checking that non-embeddable classes/interfaces do not have collection embedding properties
        checkEmbeddedWithoutCollection();
        // Check that embedded fields cannot be required (mandatory), except for the identifier
        model.getClassesAsList().forEach(this::checkNoMandatoryOnEmbeddedProperty);
        // Checking default values
        model.getClassesAsList().forEach(this::checkDefaultValues);
        // Check that the accuracy of the BigDecimal field does not exceed the length and is non-negative
        // Check that the accuracy is specified only for BigDecimal fields
        checkBigDecimalScale();
        // Check that in fields where length does not make sense, there is no length.
        checkFieldLength();
        // Checking that the property type is not an abstract class
        model.getClassesAsList().forEach(this::checkNoAbstractClassAsLink);

        // checking the fulfillment of conditions for applying the SingleTable strategy
        model.getClassesAsList().forEach(this::checkSingleTableRequirements);

        // Adding the type field to base non-final classes
        addTypeField();
        // Setting TypeInfo for interface attributes
        // Also, the type attribute field of the interface is modified.
        model.getInterfacesAsList().forEach(this::processInterfaceClass);

        // It is necessary to transfer the names from pdm before generating table names
        setTableNameFromPdm();
        setColumnNameFromPdm();

//      The generation of the table name and primary key for non - abstract and non -embedded classes.The name is placed in the class and model
//      Setting flags(ObjectLinks)O2M, M2O, O2O on properties
//      Setting the physical type property (typeInfo)
//      Fill in indexName with embedded attributes
//      Fill in the indexed field on reference fields
//      Setting the physical name of the column in the field and model
//      Physical table name setting for collection fields
//      Physical name setting of index fields collection
//      Setting the physical name of the Order field in the collection
//      Check that reference fields do not use a collection list
//      The check that clob and blob are not used with indexes
//      Fill in the index name on the field and model, if the field is indexed
        model.getClassesAsList().forEach(this::processModelClass);
        model.getClassesAsList().forEach(this::validPrimitiveDefaultValue);
        model.getClassesAsList().forEach(this::checkNoManyToManyLink);
        // Initialization of EmbeddedList on classes
        model.getClassesAsList().forEach(modelClass -> Checker.initEmbeddedList(modelClass, parameterContext.getPluginParameters().getMaxDBObjectNameLength()));
        model.getClassesAsList().forEach(this::setupLengthOffsetDateTime);
        model.getClassesAsList().forEach(this::checkIdCategoryIsNotNull);

        externalTypesActions();

        // Initialization of  incomeProperties
        setIncomeProperties(model);

        new ModelIndexLogic(model, pluginParameters, pdmModel).initIndexes(warnings);
        new ModelCciLogic(model, pluginParameters.getMaxCciKeyLength()).check();
        new ModelForeignKeyLogic(model, pluginParameters, pdmModel).initForeignKeys();

        model.getClassesAsList().forEach(this::checkIntegrityCheckReferenceTag);
        checkCloneable();
        model.getClassesAsList().forEach(this::setPartitionKeyByPartitionKeyRegex);

        checkDiffs(parameterContext, pdmModel, disableCompatibilityCheck);

        new ChangelogChecker(this.model, this.changelog, parameterContext).check();
        removeSysLastHistVersionFromPdmBaseClass();
        checkEqualPropertyName(parameterContext);
        checkDuplicatePhysicNames(parameterContext);
        // Checking that there are no two or more history classes referring to one base class
        checkSingularHistoryClassReference(parameterContext.getModelParameters().getModel());

        writeAllowLastChangeDateComparePluginParameterToPdm(
            modelParameters.getPdmModel(),
            pluginParameters.isAllowLastChangeDateCompare()
        );
        writeAllowUseParentUniqueIndexPluginParameterToPdm(
            modelParameters.getPdmModel(),
            pluginParameters.isAllowUseParentUniqueIndex()
        );

        return warnings;
    }

    /**
     * Check that in the new model, attributes that should not be present in it are not filled in the framework of sitistoricity
     */
    private void checkNoHistoryAttributesOnNewModel() {
        List<String> classesWithWongAttributes = model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getHistoryForClass() != null || modelClass.getHistoryClass() != null)
            .map(XmlModelClass::getName)
            .toList();

        if (!classesWithWongAttributes.isEmpty()) {
            throw new CheckModelException(
                "The following class attributes cannot be used on the model: ["
                    + XmlModelClass.HISTORY_FOR_CLASS_TAG + ", " + XmlModelClass.HISTORY_CLASS_TAG
                    + "]. Noted usage on classes: " + classesWithWongAttributes,
                "Remove requested attributes from the model"
            );
        }
    }

    /**
     * Prevent cases when in the old model there is a history class, say SomeClassHistory,
     * And in the new model, a custom class with the same name is created.
     */
    private void checkHistoryTablesNotOverridden() {
        PdmModel pdmModel = parameterContext.getModelParameters().getPdmModel();
        XmlModel prevModel = pdmModel.getModel();
        if (prevModel != null) {
            Set<String> overwrittenHistoryClassesNames = prevModel.getClassesAsList().stream()
                // choose classes of history from the old model
                .filter(XmlModelClass::isHistoryClass)
                // We choose single-named classes from the new model
                .map(UserXmlModelClass::getName)
                .filter(name -> model.getClassNullable(name) != null)
                .collect(Collectors.toSet());

            if (!overwrittenHistoryClassesNames.isEmpty()) {
                throw new CheckModelException(
                    "You cannot redefine history classes: the following classes were redefined" + overwrittenHistoryClassesNames,
                    "Select another name for these classes."
                );
            }
        }
    }

    private void addMarkIsDeprecatedToProperties() {

        if (this.pdmModel == null) {
            return;
        }

        this.model.getClassesAsList().stream()
            .filter(XmlModelClass::isDeprecated)
            .forEach(xmlModelClass -> {
                    xmlModelClass.getPropertiesAsList().stream()
                        .filter(xmlModelClassProperty -> !xmlModelClassProperty.isDeprecated())
                        .forEach(XmlModelClassProperty::setDeprecated);
                    xmlModelClass.getReferencesAsList().stream()
                        .filter(xmlModelClassReference -> !xmlModelClassReference.isDeprecated())
                        .forEach(xmlModelClassReference -> xmlModelClassReference.setDeprecated(true));
                }
            );

    }

    private void setManualNameFlagForIndexes() {
        model.getClassesAsList().stream()
            .flatMap(newClass -> newClass.getIndices().stream())
            .forEach(newIndex -> {
                if (!StringUtils.isEmpty(newIndex.getIndexName())) {
                    newIndex.setManualName(true);
                }
            });
    }

    private void checkVersionedEntities(PluginParameters pluginParameters) {
        if (!pluginParameters.isEnableVersionedEntities() && this.model.isVersionedEntities()) {
            throw new EnableVersionedEntitiesException();
        }
    }

    /**
     * Checking remote elements from the model:
     * The generation is performed according to a new algorithm (deprecateDeletedItems = false); both classes and methods are analyzed:
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
     * (whether the user has previously marked it as deprecated by setting isDeprecated=true explicitly);
     * - it is checked whether the installation was deprecated = true in a major release different from the current one.
     * All elements of the scheme that have not passed the check are entered in the appropriate collections, which are then used
     * for displaying errors to the user
     * All deleted elements that have passed the check are entered in the corresponding section items.
     * <unusedSchemaItems> (XmlUnusedSchemaItems) в методе {@link #addDeletedItemsToXmlUnusedSchemaItems(XmlModel, XmlModel, ModelParameters, PluginParameters)}
     *
     * @param modelParameters
     */
    private void checkDeletedElementsFromModel(ModelParameters modelParameters) {
        String newModelVersion = getModelVersion(modelParameters, model);

        if (this.pdmModel == null) {
            return;
        }

        List<XmlModelClass> removedNotDeprecatedClasses = new ArrayList<>();
        List<XmlModelClassProperty> removedNotDeprecatedProperties = new ArrayList<>();

        List<XmlModelClass> removedAndDeprecatedMajorReleaseVersionIsSameClasses = new ArrayList<>();
        List<XmlModelClassProperty> removedAndDeprecatedMajorReleaseVersionIsSameProperties = new ArrayList<>();

        XmlModel lastVersionModel = pdmModel.getModel();
        lastVersionModel.getClassesAsList().stream()
            .filter(it -> !it.isEvent())
            .forEach(oldClass -> {

                if (this.model.containsClass(oldClass.getName())
                    && (oldClass.getClassAccess() == Changeable.UPDATE)) {

                    XmlModelClass newModelClass = this.model.getClass(oldClass.getName());

                    oldClass.getPropertiesAsList().stream()
                        .filter(oldProperty -> oldProperty.isUserProperty() || oldProperty.isExternalLink())
                        .filter(oldProperty -> !oldProperty.getName().equals(OBJECT_ID_PREFIX))
                        .filter(oldProperty -> !newModelClass.containsProperty(oldProperty.getName()))
                        .filter(oldProperty -> newModelClass.getReferencesAsList().stream()
                            .noneMatch(reference -> reference.getName().equals(oldProperty.getName())))
                        .forEach(oldProperty -> {

                            // Here we check if "deprecated" was explicitly set to true.
                            // if not, then we collect all such classes in a list to later issue an error
                            if (!oldProperty.isDeprecated()) {
                                removedNotDeprecatedProperties.add(oldProperty);
                            } else {
                                // if it was set, then we check that the major release version is not equal to the one in which the deprecated = true flag was set:
                                // if equal, then fall with an error
                                if (isSameMajorVersionOrSnapshot(newModelVersion, oldProperty.getVersionDeprecated())) {
                                    removedAndDeprecatedMajorReleaseVersionIsSameProperties.add(oldProperty);
                                }

                            }
                        });

                } else if (!this.model.containsClass(oldClass.getName())
                    && (oldClass.getClassAccess() == Changeable.UPDATE)) {
                    // We choose only user-defined classes that are missing in the new model
                    //here we check if deprecated = true was explicitly set.
                    // if not, then we collect all such classes in a list to later issue an error
                    if (!oldClass.isDeprecated()) {
                        removedNotDeprecatedClasses.add(oldClass);
                    } else {
                        // if it was set, then we check that the major release version is not equal to the one in which the deprecated = true flag was set:
                        // if equal, then fall with an error
                        if (isSameMajorVersionOrSnapshot(newModelVersion, oldClass.getVersionDeprecated())) {
                            removedAndDeprecatedMajorReleaseVersionIsSameClasses.add(oldClass);
                        }

                    }

                }

            });

        if (!removedNotDeprecatedClasses.isEmpty() || !removedNotDeprecatedProperties.isEmpty()) {
            // if the new deletion logic and the list of deleted items do not explicitly pre-set deprecated = true classes or properties are empty, then we fail with an error
            throw new RemovedNotDeprecatedItemsOfModelException(removedNotDeprecatedClasses, removedNotDeprecatedProperties);
        }

        if (!removedAndDeprecatedMajorReleaseVersionIsSameClasses.isEmpty() || !removedAndDeprecatedMajorReleaseVersionIsSameProperties.isEmpty()) {
            // if the new deletion logic is applied and some of the deleted items were marked as deprecated=true not in the larger major release, then we fail with an error
            throw new RemovedAndDeprecatedItemsInSameMajorReleaseException(removedAndDeprecatedMajorReleaseVersionIsSameClasses, removedAndDeprecatedMajorReleaseVersionIsSameProperties);
        }

    }

    private boolean isSameMajorVersionOrSnapshot(String newModelVersion, String deprecatedVersion) {

        if (isSnapshotVersion(newModelVersion)) {
            return true;
        }

        int newMajorModelVersion = Semver.of(newModelVersion).getMajorVersion();
        int deprecatedClassMajorVersion = getDeprecateMajorVersion(deprecatedVersion);

        return newMajorModelVersion == deprecatedClassMajorVersion;

    }

    /**
     * Backward compatibility processing. Previously, the fields ver-deprecated might not have existed or the version might not have been semver.
     *
     * @param version
     * @return major version if model version match semver rules. Zero if not or null.
     */
    private int getDeprecateMajorVersion(String version) {
        try {
            return Semver.of(version).getMajorVersion();
        } catch (SemverParseException exception) {
            return 0;
        }
    }

    private void addXmlUnusedSchemaItemsToNewModel() {
        if (this.pdmModel != null) {
            XmlModel lastVersionModel = pdmModel.getModel();
            XmlUnusedSchemaItems unusedSchemaItems = lastVersionModel.getUnusedSchemaItems();
            if (Objects.nonNull(unusedSchemaItems)) {
                this.model.setUnusedSchemaItems(unusedSchemaItems);
            }
        }
    }

    private void writeAllowLastChangeDateComparePluginParameterToPdm(PdmModel pdmModel,
                                                                     boolean allowLastChangeDateCompare) {

        pdmMetaInformationAccessor(
            pdmModel,
            xmlMetaInformation -> xmlMetaInformation
                .setAllowLastChangeDateCompare(
                    String.valueOf(allowLastChangeDateCompare)
                )
        );

    }

    private void writeAllowUseParentUniqueIndexPluginParameterToPdm(PdmModel pdmModel,
                                                                    boolean allowUseParentUniqueIndex) {

        pdmMetaInformationAccessor(
            pdmModel,
            xmlMetaInformation -> xmlMetaInformation
                .setAllowUseParentUniqueIndex(
                    String.valueOf(allowUseParentUniqueIndex)
                )
        );

    }

    public static void pdmMetaInformationAccessor(PdmModel pdmModel,
                                                  Consumer<XmlMetaInformation> consumer) {

        if (Objects.isNull(pdmModel.getMetaInformation())) {
            pdmModel.setMetaInformation(new XmlMetaInformation());
        }

        consumer.accept(pdmModel.getMetaInformation());

    }

    /**
     * Remove unnecessary field from BaseClass class (sysLastHistVersion)
     * It was earlier
     * <p>
     * Ты предоставил пустое сообщение для примера, поэтому я не могу продемонстрировать замену текста на английском языке. Пожалуйста, предоставь текст с русскими словами, который нужно заменить на английские.
     */
    private void removeSysLastHistVersionFromPdmBaseClass() {
        if (pdmModel != null) {
            XmlModel model = pdmModel.getModel();
            XmlModelClass baseClass = model.getClass(JpaConstants.ENTITY_CLASS_NAME);
            baseClass.removeProperty(JpaConstants.LAST_HIST_VERSION_PROPERTY, true);
        }
    }

    /**
     * Checks the correctness of model version formation
     */
    private void checkModelVersion(String version) {
        ModelHelper.validateModelVersionName(version, new ModelVersionException());
    }

    /**
     * Checks that the class strategy (JOIND, SINGLE_TABLE) is set only for base classes
     * Exception classes of historicity
     */
    private void checkOnlyBaseMarkInheritanceStrategy() {
        model.getClassesAsList().stream()
            .filter(modelClass -> !modelClass.isHistoryClass())
            .forEach(modelClass -> {
                if (!modelClass.isBaseClassMark() && modelClass.getStrategy() != null) {
                    throw new ChildInheritanceStrategyException(modelClass.getName());
                }
            });
    }

    /**
     * For importing a third-generation model
     * (minimal probability of use)
     */
    private void addModelViaImports() {
        List<XmlImport> imports = model.getImports().stream()
            .filter(xmlImport -> MODEL_STREAM_NAME.equals(xmlImport.getType()))
            .toList();

        imports.forEach(xmlImport -> Helper.wrap(() -> {
            XmlModel xmlModel = ClassPathUtils.readResource(xmlImport.getFile(), XML_MAPPER, XmlModel.class);
            model.importXmlModel(xmlModel);
        }));

        model.getImports().removeAll(imports);
    }

    private void checkDeprecatedTagNewElements() {
        final List<XmlModelClass> deprecatedClasses = new ArrayList<>();
        final List<XmlModelClassProperty> deprecatedProperties = new ArrayList<>();
        final List<XmlModelClassReference> deprecatedReferences = new ArrayList<>();
        if (this.pdmModel == null) {
            model.getClassesAsList().forEach(modelClass -> {
                if (modelClass.isDeprecated()) {
                    deprecatedClasses.add(modelClass);
                }
                deprecatedProperties.addAll(
                    modelClass.getPropertiesAsList().stream()
                        .filter(XmlModelClassProperty::isDeprecated)
                        .toList()
                );
                deprecatedReferences.addAll(
                    modelClass.getReferencesAsList().stream()
                        .filter(XmlModelClassReference::isDeprecated)
                        .toList()
                );
            });
        } else {
            // add new classes
            deprecatedClasses.addAll(
                model.getClassesAsList().stream()
                    .filter(xmlModelClass -> this.pdmModel.getModel().getClassNullable(xmlModelClass.getName()) == null)
                    .filter(XmlModelClass::isDeprecated)
                    .toList()
            );
            // add new property
            model.getClassesAsList().stream()
                .filter(xmlModelClass -> this.pdmModel.getModel().getClassNullable(xmlModelClass.getName()) != null)
                .forEach(modelClass -> {
                        deprecatedProperties.addAll(modelClass.getPropertiesAsList().stream()
                            .filter(property -> {
                                final XmlModelClass prevClass = this.pdmModel.getModel().getClass(modelClass.getName());
                                return prevClass.getPropertyNullable(property.getName()) == null &&
                                    property.isDeprecated();
                            })
                            .toList());

                        deprecatedReferences.addAll(modelClass.getReferencesAsList().stream()
                            .filter(reference -> {
                                final XmlModelClass prevClass = this.pdmModel.getModel().getClass(modelClass.getName());
// In pdm reference, there are already properties.
                                return prevClass.getPropertyNullable(reference.getName()) == null &&
                                    reference.isDeprecated();
                            })
                            .toList());
                    }

                );

        }
        if (!deprecatedClasses.isEmpty() || !deprecatedProperties.isEmpty() || !deprecatedReferences.isEmpty()) {
            throw new NewElementDeprecatedException(deprecatedClasses, deprecatedProperties, deprecatedReferences);
        }
    }

    /**
     * Finds deleted enums, marks them as deprecated, and copies to the new model
     */
    private void addRemovedEnumsToNewModel() {
        if (this.pdmModel != null) {
            XmlModel lastVersionModel = pdmModel.getModel();
            final List<XmlModelClassEnum> deletedEnums = lastVersionModel.getUserEnums().stream()
                .filter(pdmEnum -> !this.model.containsEnum(pdmEnum.getName()))
                .peek(it -> it.setDeprecated(true))
                .toList();
//TODO possibly, we need to copy rather than modify pdm data
            this.model.addEnums(deletedEnums);
        }
    }

    /**
     * Adds information about deleted classes to the model and adds them to the diff in the "deleted" section.
     * Adds removed classes to Checker.removedClasses
     */
    private void addRemovedClassesToNewModel(ModelParameters modelParameters) {
        this.removedClasses.clear();
        if (this.pdmModel != null) {
            XmlModel lastVersionModel = pdmModel.getModel();
            lastVersionModel.getClassesAsList().stream()
                // We choose only user-defined classes that are missing in the new model
                .filter(oldClass -> !this.model.containsClass(oldClass.getName())
                    && (oldClass.getClassAccess() == Changeable.UPDATE))
                .filter(it -> !it.isEvent())
                .forEach(oldClass -> {
                    // we get the class as if it were described by the consumer, not pdm
                    XmlModelClass copyClass = oldClass.extractFromPdmUserClass();
                    //TODO it would be great to mark the next three operations in the previous method
                    //Replaces the type of long string attributes with Text
                    changeOverheadStringToText(copyClass);
                    if (copyClass.isDictionary()) {
                        copyClass.setId(null);
                    }
                    if (!oldClass.isDeprecated()) {
                        copyClass.addChangedProperty(DEPRECATED_TAG, oldClass.isDeprecated());
                        // adds information about the deleted class to the diff
                        modelParameters.addDiffObject(ElementState.DEPRECATED, copyClass);
                    } else {
                        copyClass.setVersionDeprecated(oldClass.getVersionDeprecated());
                    }
                    copyClass.setDeprecated(true);
                    model.addClass(copyClass);
                    this.removedClasses.add(copyClass);
                });
        }
    }

    private void markRemovedEvents() {
        if (this.pdmModel != null) {
            XmlModel lastVersionModel = pdmModel.getModel();
            lastVersionModel.getEvents().stream()
                .filter(it -> !this.model.containsEvent(it.getName()))
                .forEach(it -> it.setDeprecated(true));
        }
    }

    private void addRemovedQueriesToNewModel(ModelParameters modelParameters) {
        if (this.pdmModel != null) {
            XmlModel lastVersionModel = pdmModel.getModel();
            lastVersionModel.getQueriesAsList().stream()
                // We choose only user requests that are missing in the new model
                .filter(oldQuery -> !this.model.containsQuery(oldQuery.getName()))
                .forEach(oldQuery -> {
                    XmlQuery copiedQuery = oldQuery.copy();
                    if (!copiedQuery.isDeprecated()) {
                        copiedQuery.addChangedProperty(DEPRECATED_TAG, copiedQuery.isDeprecated());
                        // adds diff information about the removed class
                        modelParameters.addDiffObject(ElementState.DEPRECATED, copiedQuery);
                    } else {
                        copiedQuery.setVersionDeprecated(oldQuery.getVersionDeprecated());
                    }
                    copiedQuery.setDeprecated(true);
                    model.addQuery(copiedQuery);
                });
        }
    }

    /**
     * Changes the attribute type of the class from String to Text if the length of such an attribute exceeds the maximum (4000)
     */
    private void changeOverheadStringToText(XmlModelClass modelClass) {
        modelClass.getPropertiesAsList().forEach(property -> {
            if ("String".equals(property.getType()) &&
                Objects.nonNull(property.getLength()) &&
                property.getLength() > JpaConstants.MAX_STRING_LENGTH) {
                property.setType("Text");
            }
        });
    }

    /**
     * For parents (parent classes) of remote classes, the presence of a backlink is checked.
     * If there is no backlink, it is restored, as the classes are not physically deleted.
     */
    private void addRemovedMappedByPropertyToNewModel(ModelParameters modelParameters) {
// bypass remote classes
        removedClasses.forEach(removedClass -> {
            restoreWithParentProperties(removedClass, modelParameters);
            restoreNoParentProperties(removedClass);
        });
    }

    private void restoreWithParentProperties(XmlModelClass removedClass, ModelParameters modelParameters) {
        // bypassing the parent property of classes, excluding references to itself
        List<XmlModelClassProperty> parentsProperties = removedClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isParent)
            .filter(it -> isParentNotSameClass(removedClass, it)) // Removing parents that are the same class.
            .toList();

        //Since the class was validated,it may have 1 parent to another class or not have one at all.
        if (parentsProperties.size() == 1) {
            XmlModelClassProperty parentProperty = parentsProperties.get(0);

            // May be deleted immediately 2 classes with parent and mappedBy. Then it will also be restored with mappedBy. There is no need to add a property.
            XmlModelClass parentClassModel = model.getClass(parentProperty.getType());

            // If there is no back reference in the parent class of the new model
            if (parentClassModel.getPropertiesAsList().stream()
                .noneMatch(property -> Objects.equals(property.getType(), removedClass.getName()) &&
                    Objects.equals(property.getMappedBy(), parentProperty.getName()))) {

                // in pdm there can't be a missing class that was referenced in pdm (reference in deleted class)
                XmlModelClass parentClassPdm = pdmModel.getModel().getClass(parentProperty.getType());

                List<XmlModelClassProperty> mappedByProperty = parentClassPdm.getPropertiesAsList().stream()
                    .filter(property -> Objects.equals(property.getType(), removedClass.getName()) &&
                        Objects.equals(property.getMappedBy(), parentProperty.getName()))
                    .toList();

                // should find one property
                mappedByProperty.forEach(oldMappedByProperty -> {
                    XmlModelClassProperty property = oldMappedByProperty.extractFromPdmProperty().getProperty();
                    property.setDeprecated(true);
                    parentClassModel.addProperty(property);
                });
            }
        }
    }

    private void restoreNoParentProperties(XmlModelClass removedClass) {
            // If restoring the class in which there was a field with mappedBy to another class,and this property is deleted,
            // Otherwise, we will fall with a not found property.
            removedClass.getPropertiesAsList().stream()
            .filter(it -> it.getMappedBy() != null)
            .forEach(propertyWithMappedBy -> {
                XmlModelClass mappedByClass = this.model.getClass(propertyWithMappedBy.getType());

                XmlModelClassProperty pointedProperty =
                    mappedByClass.getPropertyNullable(propertyWithMappedBy.getMappedBy());

                if (pointedProperty == null) {
                    XmlModelClass oldMappedByClass = pdmModel.getModel().getClass(mappedByClass.getName());
                    XmlModelClassProperty oldPointedProperty
                        = oldMappedByClass.getProperty(propertyWithMappedBy.getMappedBy());

                    XmlModelClassProperty property = oldPointedProperty.extractFromPdmProperty().getProperty();
                    property.setDeprecated();
                    mappedByClass.addProperty(property);
                }

            });
    }

    private boolean isParentNotSameClass(XmlModelClass removedClass, XmlModelClassProperty parentProperty) {
        return !Objects.equals(parentProperty.getType(), removedClass.getName());
    }

    /**
     * Adding collection external links to classes from the previous model
     */
    private void addCollectionReferencesFromPreviousToNewModel(boolean isDeprecateDeletedItems) {
// Find reference-s in the old model
        if (this.pdmModel != null) {
            XmlModel prevModel = pdmModel.getModel();

            List<PropertyInClass> references = new LinkedList<>();
            prevModel.getClassesAsList().forEach(modelClass -> references.addAll(getClassReferences(modelClass)));

            // Those that are not in the new one should be added to the corresponding class (provided it exists)
            references.stream()
                .filter(propertyInClass -> {
                    String className = propertyInClass.property.getModelClass().getName();
                    XmlModelClass newClass = this.model.getClassNullable(className);
                    return Objects.nonNull(newClass);
                })
                .forEach((propertyInClass) -> {
                    String className = propertyInClass.property.getModelClass().getName();
                    XmlModelClass newClass = this.model.getClass(className);
                    XmlModelClassReference newReference = newClass.getReferenceNullable(propertyInClass.property.getName());
                    if (newReference == null) {
                        // Move the remote element from the old model only with the old deletion mechanism
                        // ( For the new deletion mechanism, see the description of the parameter AbstractPlugin.deprecateDeletedItems)
                        if (isDeprecateDeletedItems) {
                            XmlModelClassReference ref = propertyInClass.property.extractFromPdmProperty().getReference();
                            ref.setDeprecated(true);
                            ref.setExistsInPdm(true);
                            newClass.addReference(ref);
                        }
                    } else {
                        newReference.setExistsInPdm(true);
                        newReference.setReferenceGenerateStrategy(propertyInClass.property.getReferenceGenerateStrategy());
                    }
                });
        }
    }

    /**
     * Restores deleted fields from embeddable classes with the deprecated attribute on the new model
     */
    private void addRemovedPropertiesToEmbeddableClasses() {
        if (this.pdmModel != null) {
            XmlModel prevModel = pdmModel.getModel();

            prevModel.getClassesAsList().stream()
                // select only embeddable classes from the old model
                .filter(XmlModelClass::isEmbeddable)
                // select classes that are present in the new model
                .filter(it -> this.model.containsClass(it.getName()))
                // translating embeddable classes into properties
                .forEach(embClass -> {
                    XmlModelClass embClassInNew = this.model.getClass(embClass.getName());
                    embClass.getPropertiesAsList().forEach(embProp -> {
                        XmlModelClassProperty embPropInNew = embClassInNew.getPropertyNullable(embProp.getName());
                        //if the property is missing in the new model, we copy it to the new model
                        // and marked as deprecated
                        if (embPropInNew == null) {
                            XmlModelClassProperty newEmbProp = embProp.extractFromPdmProperty().getProperty();
                            newEmbProp.setDeprecated(true);
                            embClassInNew.addProperty(newEmbProp);
                            //we don't add it to diff, because the property will be added to diff if needed in the method
                            // PropertyDiff.checkDiff
                        }
                    });
            });
        }
    }

    /**
     * Restores deleted embedded properties on classes
     */
    private void addRemovedEmbeddedProperties() {
        if (this.pdmModel != null) {
            XmlModel prevModel = pdmModel.getModel();

            prevModel.getClassesAsList().stream()
                // select classes that are present in the new model
                .filter(it -> this.model.containsClass(it.getName()))
                // Processing class properties, finding properties that are embedded
                .forEach(classInOld -> {
                    XmlModel oldModel = classInOld.getModel();
                    XmlModelClass classInNew = this.model.getClass(classInOld.getName());

                    classInOld.getPropertiesAsList().forEach(embProp -> {
                        XmlModelClass embeddableClassInOld = oldModel.getClassNullable(embProp.getType());
                        // If the property type is not an embeddable class or the property is system, then we skip such a field
                        if (embeddableClassInOld == null || !embeddableClassInOld.isEmbeddable()
                            || embProp.getChangeable() == Changeable.SYSTEM) {
                            return;
                        }
                        //Checks if there are no embedded fields in the new model
                        if (classInNew.getPropertyNullable(embProp.getName()) == null
                            && classInNew.getReferenceNullable(embProp.getName()) == null) {
                            //Restore the deleted property on the class in the new model
                            PropertyExtractResult propertyExtractResult = embProp.extractFromPdmProperty();
                            if (propertyExtractResult.isSystem()) {
                                // here goes an embedded objectId
                                return;
                            }
                            if (propertyExtractResult.getReference() != null) {
                                XmlModelClassReference reference = propertyExtractResult.getReference();
                                reference.setDeprecated(true);
                                classInNew.addReference(reference);
                            } else {
                                XmlModelClassProperty property = propertyExtractResult.getProperty();
                                property.setDeprecated(true);
                                classInNew.addProperty(property);
                            }
                        }
                    });
                });
        }
    }

    /**
     * Returns a wrapper over a reference property (PropertyInClass) and the name of the class in which the property is declared
     * Only collection reference fields are selected
     *
     * @param modelClass - model class
     */
    //handles the class from pdm, so all attributes are already set
    private List<PropertyInClass> getClassReferences(XmlModelClass modelClass) {
        return modelClass.getPropertiesAsList().stream()
            .filter(property -> {
                String type = property.getType();
                XmlModelClass xmlModelClass = modelClass.getModel().getClassNullable(type);
                if (xmlModelClass == null) {
                    // Since the class is from pdm, such a possibility exists only if the property type is primitive.
                    return false;
                }
                return xmlModelClass.isExternalReference();
            })
            .map(property -> {
                String className = getOriginalClassNameByReferenceClass(modelClass.getModel().getClass(property.getType()));
                return new PropertyInClass(
                    property,
                    className);
            })
            .toList();
    }

    /**
     * Returns the original class name from the reference class.
     * For example, for the ProductReference class it will return a Product
     *
     * @param modelClass - class representing the link
     * @return Original class name
     */
    private String getOriginalClassNameByReferenceClass(XmlModelClass modelClass) {
        XmlModelClassProperty reference = modelClass.getProperty(ExternalReferenceGenerator.REFERENCE);
        return ModelHelper.transformReferenceClassToOriginClass(reference.getType());
    }

    /**
     * Validation of names:
     * - models
     * - classes
     * - properties of classes
     * - external class references
     * - types of properties and external links
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
     */
    private void checkCorrectNaming() {
        String modelName = model.getModelName();
        // checking the format of the model name
        ModelHelper.validateModelName(modelName, new ModelNameRuleException(modelName));
        // the model name is translated to lower case
        model.setModelName(modelName.toLowerCase(Locale.ENGLISH));

        // bypassing classes
        PluginParameters pluginParameters = parameterContext.getPluginParameters();
        model.getClassesAsList().stream()
            .filter(it -> !it.isServiceClass())
            .forEach(modelClass -> {
                //Validation of class name
                    validateEntityName(modelClass.getName(),
                    pluginParameters,
                    EntityNameRuleException.of(modelClass, pluginParameters.getMaxClassNameLength()),
                    EntityNameAsLocalReservedWordException.of(modelClass),
                    EntityNameAsJavaReservedWordException.of(modelClass));

                    // correctness of the value of modelClass.getExtendedClass() is not checked, since the class name will be verified,
                    // and the existence of the class will be checked further in allExtendedClassExistsInModel

                    // Bypasses class properties
                    modelClass.getPropertiesAsList().forEach(property ->
                    validateEntityFieldName(property.getName(),
                        pluginParameters.getMaxPropertyNameLength(),
                        () -> PropertyNameRuleException.of(property, pluginParameters.getMaxPropertyNameLength()),
                        () -> PropertyNameAsJavaReservedWordException.of(property),
                        () -> PropertyNameAsLocalReservedWordException.of(property),
                        () -> PropertyTypeRuleException.of(property)
                    )
                );

                // bypassing external links of the class
                modelClass.getReferencesAsList().forEach(ref -> {
                        validateEntityFieldName(ref.getName(),
                            pluginParameters.getMaxPropertyNameLength(),
                            () -> ReferenceNameRuleException.of(ref, pluginParameters.getMaxPropertyNameLength()),
                            () -> ReferenceNameAsJavaReservedWordException.of(ref),
                            () -> ReferenceNameAsLocalReservedWordException.of(ref),
                            () -> ReferenceTypeRuleException.of(ref)
                        );
                        ModelHelper.validateReferenceType(ref.getType(), () -> ReferenceTypeRuleException.of(ref));
                    }
                );
            });

        model.getInterfacesAsList().forEach(iface -> {
            validateEntityName(iface.getName(),
                pluginParameters,
                EntityNameRuleException.of(iface, pluginParameters.getMaxClassNameLength()),
                EntityNameAsLocalReservedWordException.of(iface),
                EntityNameAsJavaReservedWordException.of(iface));

            iface.getPropertiesAsList().forEach(property ->
                validateEntityFieldName(property.getName(),
                    pluginParameters.getMaxPropertyNameLength(),
                    () -> PropertyNameRuleException.of(property, pluginParameters.getMaxPropertyNameLength()),
                    () -> PropertyNameAsJavaReservedWordException.of(property),
                    () -> PropertyNameAsLocalReservedWordException.of(property),
                    () -> PropertyTypeRuleException.of(property)
                )
            );
        });

        // bypass of enum. Checking the format of the enum name and the names of its values
        model.getEnums().forEach(classEnum -> {
            validateEntityName(classEnum.getName(),
                pluginParameters,
                EntityNameRuleException.of(classEnum, pluginParameters.getMaxClassNameLength()),
                EntityNameAsLocalReservedWordException.of(classEnum),
                EntityNameAsJavaReservedWordException.of(classEnum));

            classEnum.getEnumValues().forEach(xmlEnumValue ->
                ModelHelper.validateEnumValueName(
                    xmlEnumValue.getName(),
                    pluginParameters.getMaxPropertyNameLength(),
                    new EnumValueRuleException(
                        xmlEnumValue.getName(),
                        classEnum.getName(),
                        pluginParameters.getMaxPropertyNameLength()
                    )
                )
            );
        });

        model.getQueriesAsList().forEach(query -> {
            validateEntityName(query.getName(),
                pluginParameters,
                EntityNameRuleException.of(query, pluginParameters.getMaxClassNameLength()),
                EntityNameAsLocalReservedWordException.of(query),
                EntityNameAsJavaReservedWordException.of(query));

            // Bypassing incoming request parameters
            query.getParams().forEach(xmlQueryParam ->
                validateEntityFieldName(xmlQueryParam.getName(),
                    pluginParameters.getMaxPropertyNameLength(),
                    () -> UserQueryTagRuleException.of(xmlQueryParam, pluginParameters.getMaxPropertyNameLength()),
                    () -> UserQueryTagNameAsJavaReservedWordException.of(xmlQueryParam),
                    () -> UserQueryTagNameAsLocalReservedWordException.of(xmlQueryParam),
                    () -> null)
            );

            // Bypassing outgoing request parameters
            query.getProperties().forEach(xmlQueryProperty ->
                validateEntityFieldName(xmlQueryProperty.getName(),
                    pluginParameters.getMaxPropertyNameLength(),
                    () -> UserQueryTagRuleException.of(xmlQueryProperty, pluginParameters.getMaxPropertyNameLength()),
                    () -> UserQueryTagNameAsJavaReservedWordException.of(xmlQueryProperty),
                    () -> UserQueryTagNameAsLocalReservedWordException.of(xmlQueryProperty),
                    () -> null)
            );
        });

        model.getExternalTypes().forEach(externalType -> {
            validateTypeName(externalType.getType(), () -> new ExternalTypeNameRuleException(externalType.getType()));
        });
    }

    /**
     * Checking the correctness of the class name
     */
    private static void validateEntityName(String name,
                                           PluginParameters pluginParameters,
                                           EntityNameRuleException formatNameException,
                                           EntityNameAsLocalReservedWordException reservedWordException,
                                           EntityNameAsJavaReservedWordException reservedJavaWordException) {

        // checking the format of the class name
        ModelHelper.validateClassName(name, pluginParameters.getMaxClassNameLength(), formatNameException);
        // The class name should not match the base type's name
        if (Objects.nonNull(TYPES_INFO.get(name.toLowerCase(Locale.ENGLISH)))) {
            throw formatNameException;
        }
        // The class name should not match reserved names.
        if (RESERVED_CLASS_NAMES.contains(name)) {
            throw reservedWordException;
        }

        if (SourceVersion.isKeyword(name.toLowerCase(Locale.ENGLISH))) {
            throw reservedJavaWordException;
        }
    }

    /**
     * Checking the correctness of the property name
     */
    // TODO to transfer exceptions as lambdas, or to customize exceptions in order not to create them every time
    // for this, it will be necessary to combine classes and interfaces into a common interface,
    // properties and external links(in the context of classes and interfaces) are also shared by a common
    private static void validateEntityFieldName(String propertyName,
                                                int maxPropertyNameLength,
                                                Supplier<FiledNameRuleException> nameFormatException,
                                                Supplier<FieldNameAsJavaReservedWordException> javaReservedWordException,
                                                Supplier<FieldNameAsLocalReservedWordException> localReservedWordException,
                                                Supplier<FiledTypeRuleException> typeNameException) {
        // validation of property name
        ModelHelper.validatePropertyName(propertyName, maxPropertyNameLength, nameFormatException);
        // check that the property name does not match the reserved word "java"
        if (SourceVersion.isKeyword(propertyName.toLowerCase(Locale.ENGLISH))) {
            throw javaReservedWordException.get();
        }
        // The property name should not match any of the reserved property names
        if (RESERVED_PROPERTY_NAMES.contains(propertyName)) {
            throw localReservedWordException.get();
        }
        // checking the format of the property name type
        // TODO непонятно зачем. Нужна лишь проверка, что тип известен
        // Helper.validateTypeName(propertyName, typeNameException);
    }

    /**
     * Checking that the label of the following objects does not exceed a given length (254):
     * - class
     * - enum
     * - properties
     * The links are not provided in the text you shared.
     * - status name (not label)
     */
    private void checkLabelLengthOverHead() {
        final Integer maxLength = TYPES_INFO.get("string").getFirstNumber();

        checkClassLabelLength(maxLength);
        checkEnumLabelLength(maxLength);
        checkPropertiesLabelLength(maxLength);
        checkReferencesLabelLength(maxLength);
    }

    private void checkPropertiesLabelLength(Integer maxLength) {
        Map<String, String> propertyLabelMap = new HashMap<>();

        model.getClassesAsList().forEach(clazz ->
            propertyLabelMap.putAll(clazz.getPropertiesAsList().stream()
                .filter(it -> it.getLabel().length() > maxLength)
                .collect(Collectors.toMap(it -> it.getName() + " in class " + clazz.getName(),
                    XmlModelClassProperty::getLabel))));

        if (!propertyLabelMap.isEmpty()) {
            throw new TooLongValueException(
                "label",
                "properties " + String.join(", ", propertyLabelMap.keySet()),
                String.join(",", propertyLabelMap.values()),
                maxLength);
        }
    }

    private void checkReferencesLabelLength(Integer maxLength) {
        Map<String, String> referenceLabelMap = new HashMap<>();

        model.getClassesAsList().forEach(clazz ->
            referenceLabelMap.putAll(clazz.getReferencesAsList().stream()
                .filter(it -> Optional.ofNullable(it.getLabel()).orElse("").length() > maxLength)
                .collect(Collectors.toMap(it -> it.getName() + " in class " + clazz.getName(),
                    XmlModelClassReference::getLabel))));

        if (!referenceLabelMap.isEmpty()) {
            throw new TooLongValueException(
                "label",
                "references" + String.join(", ", referenceLabelMap.keySet()),
                String.join(",", referenceLabelMap.values()),
                maxLength);
        }
    }

    private void checkEnumLabelLength(Integer maxLength) {
        final Map<String, String> enumLabelMap = model.getEnums().stream()
            .filter(it -> Optional.ofNullable(it.getLabel()).orElse("").length() > maxLength)
            .collect(Collectors.toMap(XmlModelClassEnum::getName, XmlModelClassEnum::getLabel));
        if (!enumLabelMap.isEmpty()) {
            throw new TooLongValueException(
                "label",
                "enumerations" + String.join(", ", enumLabelMap.keySet()),
                String.join(",", enumLabelMap.values()),
                maxLength);
        }
    }

    private void checkClassLabelLength(Integer maxLength) {
        final Map<String, String> classLabelMap = model.getClassesAsList().stream()
            .filter(it -> it.getLabel().length() > maxLength)
            .collect(Collectors.toMap(XmlModelClass::getName, XmlModelClass::getLabel));
        if (!classLabelMap.isEmpty()) {
            throw new TooLongValueException(
                "label",
                "classes " + String.join(", ", classLabelMap.keySet()),
                String.join(",", classLabelMap.values()),
                maxLength);
        }
    }

    /**
     * Checks that embeddable classes do not extend any others, and that no one extends embeddable classes
     */
    private void checkEmbeddedDoesNotHaveExtends() {
        Collection<XmlModelClass> extendsClasses = model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getExtendedClassName() != null)
            .filter(modelClass -> modelClass.isEmbeddable() ||
                model.getClass(modelClass.getExtendedClassName()).isEmbeddable())
            .toList();

        if (!extendsClasses.isEmpty()) {
            throw new EmbeddableExtendsException(extendsClasses);
        }
    }

    private void checkEmbeddedDoesNotAbstract() {
        List<XmlModelClass> embeddableAbstractClasses = model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.isEmbeddable() && modelClass.isAbstract())
            .toList();

        if (!embeddableAbstractClasses.isEmpty()) {
            throw new EmbeddableAbstractException(embeddableAbstractClasses);
        }
    }

    /**
     * Checks that the locable = true flag is set only for base classes and is not set to true for reference books
     */
    private void checkLocks() {
        checkLockableOnlyBaseEntities();
        checkDictionaryNotLockable();
    }

    /**
     * Checks that the flag locable = true is set only for base classes
     */
    private void checkLockableOnlyBaseEntities() {
        Set<String> badClasses = model.getClassesAsList().stream()
            .filter(cls -> !cls.isDictionary())
            .filter(cls -> cls.getLockable() && !cls.isBaseClassMark())
            .map(XmlModelClass::getName)
            .collect(Collectors.toSet());

        if (!badClasses.isEmpty()) {
            throw new LockMarkupException(badClasses);
        }
    }

    /**
     * Checks that no directories are set with the property locable = true
     */
    private void checkDictionaryNotLockable() {
        List<String> lockableDictionaryClasses = model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.isDictionary() && modelClass.getLockable())
            .map(XmlModelClass::getName)
            .toList();
        if (!lockableDictionaryClasses.isEmpty()) {
            throw new DictionaryLockableException(lockableDictionaryClasses);
        }
    }

    /**
     * Checks that the externalLink flag is not set manually on the properties
     */
    private void checkNoManualExternalLink() {
        model.getClassesAsList()
            .forEach(clazz -> {
                List<XmlModelClassProperty> externalProperties = clazz.getPropertiesAsList().stream()
                    .filter(XmlModelClassProperty::isExternalLink).toList();
                if (!externalProperties.isEmpty()) {
                    throw new ManualExternalLinkDefineException(externalProperties);
                }
            });
    }

    /**
     * Checks the correctness of filling in type aliases and replaces aliases with the corresponding types in class and interface properties.
     * The following checks apply:
     * - the name is filled
     * The text does not contain any Russian words or phrases to be translated into English.
     * - intersection with class names
     * The text does not contain any Russian words or phrases to be translated into English. Therefore, no replacements are needed.
     * The text "пересечение с именами зарегистрированных примитивных типов" in Russian is translated to "intersection with the names of registered primitive types" in English.
     * <p>
     * Resulting text: * - intersection with the names of registered primitive types
     * - alias is based on the well-known primitive type
     */
    private void checkAndReplaceAliasesTypes() {
        XmlTypeDefs xmlTypeDefs = model.getXmlAlias();

        if (xmlTypeDefs == null) {
            return;
        }

        Set<String> diffNames = new HashSet<>();

        xmlTypeDefs.getTypeDefs().forEach(xmlTypeDef -> {
            String aliasName = xmlTypeDef.getName();
            // checking the name
            if (StringUtils.isBlank(aliasName)) {
                throw new TypeDefEmptyNameException();
            }

            // checking for duplicates (by name)
            if (!diffNames.add(aliasName)) {
                throw new TypeDefDuplicationException(aliasName);
            }

            // intersection with class names
            if (model.getClassNullable(aliasName) != null) {
                throw new TypeDefNameEqualToClassNameException(aliasName);
            }

            // intersection with interface names
            if (model.getInterfaceNullable(aliasName) != null) {
                throw new TypeDefNameEqualToInterfaceNameException(aliasName);
            }

            // The name must not conflict with registered types
            if (TYPES_INFO.containsKey(aliasName.toLowerCase(Locale.ENGLISH))) {
                throw new TypeDefEqualToPrimitiveException(aliasName);
            }

            TypeInfo typeInfo = TYPES_INFO.get(xmlTypeDef.getType().toLowerCase(Locale.ENGLISH));

            // alais should be based on a known type
            if (typeInfo == null) {
                throw new TypeDefTypeNotPrimitiveException(aliasName);
            }

            // Replacing aliases in property types with corresponding types
            model.getClassesAsList().forEach(modelClass ->
                modelClass.getPropertiesAsList().stream()
                    .filter(property -> aliasName.equals(property.getType()))
                    .forEach(property -> {
                            property.setType(xmlTypeDef.getType());
                            property.setLength(property.getLength() != null ? property.getLength() : xmlTypeDef.getLength());
                            property.setScale(property.getScale() != null ? property.getScale() : xmlTypeDef.getScale());
                        }
                    )
            );

            // replacing aliases in interfaces with corresponding types
            // The length and precision are not indicated, as these parameters are not controlled for the interface.
            model.getInterfacesAsList().forEach(modelInterface ->
                modelInterface.getPropertiesAsList().stream()
                    .filter(property -> aliasName.equals(property.getType()))
                    .forEach(property -> property.setType(xmlTypeDef.getType()))
            );
        });
    }

    /**
     * The method checks that there are no properties and references that are not deprecated but refer to deprecated classes.
     * If the type is deprecated, then either the property must be deprecated or the class it belongs to must be deprecated
     */
    private void checkDeprecatedFlag() {
        List<XmlModelClassProperty> notDeprecatedProps = model.getClassesAsList().stream()
            .flatMap(xmlClass -> xmlClass.getPropertiesAsList().stream())
            .filter(prop -> {
                XmlModelClass propTypeClass = model.getClassNullable(prop.getType());
                if (propTypeClass == null || !propTypeClass.isDeprecated()) {
                    return false;
                }
                return !prop.isDeprecated() && !prop.getModelClass().isDeprecated();
            })
            .toList();

        List<XmlModelClassReference> notDeprecatedRefs = model.getClassesAsList().stream()
            .flatMap(xmlClass -> xmlClass.getReferencesAsList().stream())
            .filter(ref -> {
                XmlModelClass refTypeClass = model.getClassNullable(ref.getType());
                if (refTypeClass == null || !refTypeClass.isDeprecated()) {
                    return false;
                }
                return !ref.isDeprecated() && !ref.getModelClass().isDeprecated();
            })
            .toList();

        List<XmlProperty> totalList = new ArrayList<>(notDeprecatedProps);
        totalList.addAll(notDeprecatedRefs);
        if (!totalList.isEmpty()) {
            throw new NotDeprecatedPropertyWithDeprecatedTypeException(totalList);
        }
    }

    /**
     * Sets all classes the attribute classAccess = UPDATE
     */
    private void makeAllClassesUpdatable() {
        model.getClassesAsList().stream()
            .filter(it -> !it.isServiceClass())
            .forEach(modelClass -> {
                if (modelClass.getClassAccess() == null) {
                    modelClass.setClassAccess(Changeable.UPDATE);
                }
            });
    }

    /**
     * Checking mappedBy properties:
     * - it is not declared two mappedBy properties with the same class on the class
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
     * The text "соотвествующем" is replaced with "corresponding". Here's the modified text:
     * <p>
     * - there is a direct link on the corresponding class (for mappedBy)
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacements are needed.
     * - no direct link refers to another class
     * The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
     * - check that when using o2o links, there is a unique index (except for parent and dictionary).
     * is created a unique index under the hood, in the dictionary uniqueness is controlled at the application level)
     */
    private void checkMappedByProperties() {

        checkNoMappedByOnCollectionAndSimpleProperty();

        model.getClassesAsList().forEach(modelClass -> {
            List<XmlModelClassProperty> properties = modelClass.getPropertiesAsList()
                .stream()
                .filter(property -> property.getMappedBy() != null)
                .toList();

            // check that no more than one collection field with the same type (class) is declared in the class
            //TODO it is unclear why it is impossible to declare several collection mappedBy fields tied to the same class,,
            // but different its fields (direct links)?
            List<String> mappedBySame = properties
                .stream()
                .filter(property -> property.getCollectionType() != null)
                .collect(Collectors.groupingBy(XmlModelClassProperty::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

            if (!mappedBySame.isEmpty()) {
                throw new SeveralMappedBySamePropertyException(modelClass.getName());
            }

            // check that in the mappedBy field there is no non-existent type in the model indicated
            // TODO possibly unnecessary
            List<XmlModelClassProperty> nonModelProperties = properties
                .stream()
                .filter(property -> !modelClass.getModel().containsClass(property.getType()))
                .toList();

            if (!nonModelProperties.isEmpty()) {
                throw new MappedByToUndefinedClassException(modelClass.getName(), nonModelProperties);
            }

            properties.forEach(property -> {
                XmlModelClassProperty linkedProperty = modelClass.getModel().getClass(property.getType())
                    .getPropertyNullable(property.getMappedBy());

                // check if the link field exists on the specified class
                if (linkedProperty == null) {
                    throw new MappedByLinkNotExistsInClassException(property);
                }

                // link is itself a backlink
                if (linkedProperty.getMappedBy() != null) {
                    throw new MappedByLinkPropertyHasAlsoMappedByException(property);
                }

                // If a direct reference refers to a class that has not declared the mappedBy field
                if (!Objects.equals(modelClass.getName(), linkedProperty.getType())) {
                    throw new MappedByNotOnSameTypeException(property);
                }

                // link is a collection
                if (linkedProperty.getCollectionType() != null) {
                    throw new MappedByOnCollectionPropertyException(property);
                }

                // if the back reference is not collective, then we check that the direct link has a unique index, or it is parent
                // If the link parent and backlink are not collective, then under the hood a unique index will be created.
                // The underlying index is not created for a regular field, and an exception is thrown instead.
                // It's not pretty, but I didn't redo it, I left it for the future
                // For reference indices are also created under the hood
                //TODO unify behavior (either lose backward compatibility - not critical, or create non-intuitive index)
                //TODO add check for links after all indexes are created
                if (property.getCollectionType() == null) {
                    if (!linkedProperty.getModelClass().isDictionary() && !linkedProperty.isParent()
                        && !isPropertyHaveUniqueIndex(linkedProperty)) {
                        throw new OneToOneWithoutParentException(property);
                    }
                }
            });
        });
    }

    void checkNoMappedByOnCollectionAndSimpleProperty() {
        this.model.getClassesAsList().stream()
            .filter(it -> !it.isEmbeddable() && !it.isAbstract())
            .forEach(modelClass -> {
                final List<XmlModelClassProperty> mappedByProperties =
                    modelClass.getPropertiesWithIncome().stream()
                        .filter(it -> StringUtils.isNotEmpty(it.getMappedBy()))
                        .toList();

                mappedByProperties.stream()
                    .filter(it -> Objects.isNull(it.getCollectionType()))
                    .forEach(simpleProperty -> mappedByProperties.stream()
                        .filter(it -> !Objects.isNull(it.getCollectionType()))
                        .forEach(collectionProperty -> {
                            if (Objects.equals(simpleProperty.getType(), collectionProperty.getType()) &&
                                Objects.equals(simpleProperty.getMappedBy(), collectionProperty.getMappedBy())) {
                                throw new MappedByOnCollectionAndSimplePropertyException(
                                    collectionProperty,
                                    simpleProperty
                                );
                            }
                        }));
            });
    }

    /**
     * Launches generators.
     * Generators are tied to import types.
     * If there is a corresponding import in the model, a generator is launched that somehow modifies the model
     */
    private void executeExtensions() {
        if (streamModel.isFromStreams()) {
                // for tests
                executeExtensionsForStreams(
                    this.parameterContext.getPluginParameters(),
                    streamModel,
                    this.parameterContext.getModelParameters()
                );
        } else {
            // for combat mode
            executeExtensions(
                this.parameterContext.getPluginParameters(),
                this.parameterContext.getPluginParameters().getModel(),
                this.parameterContext.getModelParameters()
            );
        }
    }

    private void executeExtensionsForStreams(PluginParameters params,
                                             StreamModel streamModel,
                                             ModelParameters modelParameters) {
        modelGenerates.forEach(modelGenerate -> modelGenerate.preInit(modelParameters.getModel(), params));

        List<Pair<ModelGenerate, XmlImport>> generatePairs = checkAndOrderModelGenerates(modelParameters.getModel());

        generatePairs.forEach(xmlImportPair -> {
            ModelGenerate modelGenerate = xmlImportPair.getFirst();
            modelParameters.addExecutingModelGenerate(modelGenerate);

            modelGenerate.initModel(modelParameters.getModel(), streamModel, modelParameters);
        });

        generatePairs.forEach(xmlImportPair -> {
            ModelGenerate modelGenerate = xmlImportPair.getFirst();

            modelGenerate.postInitModel(modelParameters.getModel(), modelParameters);
        });
    }

    /**
     * Starts generators.
     * Generators are tied to import types.
     * If there is a corresponding import in the model, a generator is launched that somehow modifies the model
     */
    private void executeExtensions(PluginParameters params,
                                   File modelDirectory,
                                   ModelParameters modelParameters) {
        // Launch the preInit stage of generators. For example, it is used to supplement the XmlImport model with default data.
        modelGenerates.forEach(modelGenerate -> modelGenerate.preInit(modelParameters.getModel(), params));

        // We find suitable generators for imports (other than IMPORT) and sort them by generator priority.
        //Why is it that only the first suitable generator is taken, it is unclear what happens to the rest
        List<Pair<ModelGenerate, XmlImport>> generatePairs = checkAndOrderModelGenerates(modelParameters.getModel());

        generatePairs.forEach(xmlImportPair -> {
            ModelGenerate modelGenerate = xmlImportPair.getFirst();
            modelParameters.addExecutingModelGenerate(modelGenerate);

            String xmlImportFile = xmlImportPair.getSecond().getFile();
            // direct launch of the generator
            modelGenerate.initModel(modelParameters.getModel(),
                Helper.getFile(modelDirectory, xmlImportFile == null ? "" : xmlImportFile), modelParameters);
        });

        // execution of the postInit stage of generators. It is executed after all generators have been processed
        generatePairs.forEach(xmlImportPair -> {
            ModelGenerate modelGenerate = xmlImportPair.getFirst();

            String xmlImportFile = xmlImportPair.getSecond().getFile();
            modelGenerate.postInitModel(modelParameters.getModel(),
                Helper.getFile(modelDirectory, xmlImportFile == null ? "" : xmlImportFile), modelParameters);
        });
    }

    /**
     * For each import in the model, it finds an appropriate generator (one).
     * Prioritizes generators and their corresponding imports in the order of generator priority.
     * If the generator for import is not found, it throws an exception of UnsupportedImportException
     */
    private List<Pair<ModelGenerate, XmlImport>> checkAndOrderModelGenerates(XmlModel baseModel) {
        return baseModel.getImports().stream()
            // find imports with a type different from IMPORT
            .filter(xmlImport -> !xmlImport.getType().equals("IMPORT"))
            .map(xmlImport -> {
                Optional<ModelGenerate> first = modelGenerates.stream()
                    // We find the appropriate type generator
                    // It turns out that there shouldn't be multiple generators for one import type, but it's not checked anywhere.
                    .filter(modelGenerate -> modelGenerate.getProjectName().equals(xmlImport.getType()))
                    .findFirst();

                // If the generator is not found, we report on an incorrect import type.
                if (first.isEmpty()) {
                    throw new UnsupportedImportException(xmlImport, modelGenerates);
                }

                // Returns the generator and its corresponding import
                return new Pair<>(first.get(), xmlImport);
            }).sorted(Comparator.comparingInt(xmlImportPair -> xmlImportPair.getFirst().getPriority()))
            .toList();
    }

    /**
     * Checking that the name of the enum does not conflict with the name of a class, interface, alias, or primitive type
     */
    private void checkEnums() {
        checkEmptyEnums();
        checkEnumNames();
        checkEnumValues();
        checkEnumExtensions();
    }

    private void checkEmptyEnums() {
        final List<String> emptyEnums = model.getEnums().stream()
            .filter(xmlModelClassEnum -> xmlModelClassEnum.getEnumValues().isEmpty())
            .map(XmlModelClassEnum::getName)
            .toList();
        if (!emptyEnums.isEmpty()) {
            throw new EmptyEnumException(emptyEnums);
        }
    }

    /**
     * Checking that the name of the enum does not conflict with the name of a class, interface, alias, query, or primitive type
     */
    private void checkEnumNames() {
        model.getEnums().forEach(classEnum -> {
            String enumName = classEnum.getName();
            if (model.containsClass(enumName)) {
                throw new EnumNameAsClassNameException(enumName);
            }

            if (model.containsInterface(enumName)) {
                throw new EnumNameAsInterfaceNameException(enumName);
            }

            if (model.containsAlias(enumName)) {
                throw new EnumNameAsAliasNameException(enumName);
            }

            if (TYPES_INFO.containsKey(enumName)) {
                throw new EnumNameAsPrimitiveException(enumName);
            }
        });
    }

    private void checkEnumValues() {
        model.getEnums().forEach(classEnum -> {
            Set<String> collect = classEnum.getEnumValues().stream()
                .map(XmlEnumValue::getName)
                .collect(Collectors.toSet());
            if (classEnum.getEnumValues().size() != collect.size()) {
                throw new EnumValueDuplicateException(classEnum.getName());
            }
        });
    }

    private void checkEnumExtensions() {
        model.getEnums().forEach(modelEnum ->
            modelEnum.getEnumValues().forEach(enumValue -> {
                enumValue.getExtensions().forEach(extension -> {
                    if (StringUtils.isBlank(extension.getName())) {
                        throw new EnumExtensionEmptyNameException(modelEnum.getName(), enumValue.getName());
                    }
                });
                final Set<String> uniqueExtensionNames = enumValue.getExtensions().stream()
                    .map(extension -> extension.getName().toUpperCase(Locale.ENGLISH))
                    .collect(Collectors.toSet());
                if (uniqueExtensionNames.size() != enumValue.getExtensions().size()) {
                    throw new DuplicateExtensionNameException(modelEnum.getName(), enumValue.getName());
                }
            })
        );
    }

    /**
     * Setting an enum flag on class or interface properties
     */
    private void markEnumProperties() {
        model.getInterfacesAsList().forEach(this::setupEnumProperty);
        model.getClassesAsList().forEach(this::setupEnumProperty);
    }

    /**
     * Setting the flag of enum on interface properties
     */
    private void setupEnumProperty(XmlModelInterface modelInterface) {
        modelInterface.getPropertiesAsList().stream()
            .filter(property -> model.containsEnum(property.getType()))
            .forEach(property -> property.setEnum(true));
    }

    /**
     * Setting the flag of the enum on class properties
     */
    private void setupEnumProperty(XmlModelClass modelClass) {
        modelClass.getPropertiesAsList().stream()
            .filter(property -> model.containsEnum(property.getType()))
            .forEach(property -> property.setEnum(true));
    }

    /**
     * If the field has an embeddable type, then it sets the corresponding flag on the field; otherwise, it checks that the flag is not set.
     */
    private void checkAndInitCorrectEmbeddedProperties(XmlModelClassProperty modelClassProperty) {
        XmlModelClass modelClass = modelClassProperty.getModelClass().getModel()
            .getClassNullable(modelClassProperty.getType());

        if (modelClass == null) {
            return;
        }

        if (modelClass.isEmbeddable()) {
            modelClassProperty.setEmbedded(true);
        } else if (modelClassProperty.isEmbedded()) {
            throw new EmbeddedPropertyException(modelClassProperty.getName());
        }
    }

    /**
     * If the length of the string field exceeds 4k, then the exception StringLengthOverheadException is thrown
     */
    private void checkStringLengthOverhead() {
        this.model.getClassesAsList().forEach(modelClass -> {
            List<XmlModelClassProperty> overheadString = modelClass.getPropertiesAsList().stream()
                .filter(property -> "string".equalsIgnoreCase(property.getType()))
                .filter(property -> property.getLength() != null && property.getLength() > JpaConstants.MAX_STRING_LENGTH)
                .toList();

            if (!overheadString.isEmpty()) {
                throw new StringLengthOverheadException(modelClass, overheadString);
            }
        });

        if (this.model.getXmlAlias() != null) {
            List<XmlTypeDef> overheadTypeDefs = this.model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> "string".equalsIgnoreCase(it.getType()))
                .filter(it -> it.getLength() != null && it.getLength() > JpaConstants.MAX_STRING_LENGTH)
                .toList();

            if (!overheadTypeDefs.isEmpty()) {
                throw new TypeDefStringLengthOverheadException(overheadTypeDefs);
            }
        }
    }

    /**
     * Checking that the length of unicodestring does not exceed 2000
     */
    private void checkUnicodeStringLengthOverhead() {
        this.model.getClassesAsList().forEach(it -> {
            List<XmlModelClassProperty> overheadUnicodeString = it.getPropertiesAsList().stream()
                .filter(property -> "unicodestring".equalsIgnoreCase(property.getType()))
                .filter(property -> property.getLength() != null && property.getLength() > JpaConstants.MAX_UNICODE_STRING_LENGTH)
                .toList();

            if (!overheadUnicodeString.isEmpty()) {
                throw new UnicodeStringLengthOverheadException(it, overheadUnicodeString);
            }
        });

        if (this.model.getXmlAlias() != null) {
            List<XmlTypeDef> overheadTypeDefs = this.model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> "unicodestring".equalsIgnoreCase(it.getType()))
                .filter(it -> it.getLength() != null && it.getLength() > JpaConstants.MAX_UNICODE_STRING_LENGTH)
                .toList();

            if (!overheadTypeDefs.isEmpty()) {
                throw new TypeDefUnicodeStringLengthOverheadException(overheadTypeDefs);
            }
        }
    }

    /**
     * Checking that bigdecimal precision does not exceed 38
     */
    private void checkBigDecimalLengthOverhead() {
        this.model.getClassesAsList().forEach(it -> {
            List<XmlModelClassProperty> overheadBigDecimal = it.getPropertiesAsList().stream()
                .filter(property -> "bigdecimal".equalsIgnoreCase(property.getType()))
                .filter(property -> property.getLength() != null && property.getLength() > JpaConstants.MAX_BIG_DECIMAL_LENGTH)
                .toList();

            if (!overheadBigDecimal.isEmpty()) {
                throw new BigDecimalLengthOverheadException(it, overheadBigDecimal);
            }
        });

        if (this.model.getXmlAlias() != null) {
            List<XmlTypeDef> overheadTypeDefs = model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> "bigdecimal".equalsIgnoreCase(it.getType()))
                .filter(it -> it.getLength() != null && it.getLength() > JpaConstants.MAX_BIG_DECIMAL_LENGTH)
                .toList();

            if (!overheadTypeDefs.isEmpty()) {
                throw new TypeDefBigDecimalLengthOverheadException(overheadTypeDefs);
            }
        }
    }

    /**
     * Check for String, unicodeString, and bigdecimal that length is greater than 0
     */
    private void checkMinimumLength() {
        {
            BiPredicate<XmlModelClassProperty, String> predicateType = (property, type) ->
                type.equalsIgnoreCase(property.getType());

            BiPredicate<XmlModelClassProperty, Integer> predicateLength = (property, length) ->
                property.getLength() != null && property.getLength() < length;

            Predicate<XmlModelClassProperty> chString = property -> predicateType.test(property, "string") &&
                predicateLength.test(property, JpaConstants.MIN_LENGTH);

            Predicate<XmlModelClassProperty> chUnicodeString = property -> predicateType.test(property, "unicodestring") &&
                predicateLength.test(property, JpaConstants.MIN_LENGTH);

            Predicate<XmlModelClassProperty> chBigDecimal = property -> predicateType.test(property, "bigdecimal") &&
                predicateLength.test(property, JpaConstants.MIN_LENGTH);

            this.model.getClassesAsList().forEach(it -> {
                List<XmlModelClassProperty> belowMinimumLength = it.getPropertiesAsList().stream()
                    .filter(property -> chString.test(property) || chUnicodeString.test(property) || chBigDecimal.test(property))
                    .toList();

                if (!belowMinimumLength.isEmpty()) {
                    throw new BelowMinimumPropertyLengthException(it, belowMinimumLength);
                }
            });
        }
        {
            BiPredicate<XmlTypeDef, String> predicateType = (typeDef, type) ->
                type.equalsIgnoreCase(typeDef.getType());

            BiPredicate<XmlTypeDef, Integer> predicateLength = (typeDef, length) ->
                typeDef.getLength() != null && typeDef.getLength() < length;

            Predicate<XmlTypeDef> chString = typeDef -> predicateType.test(typeDef, "string") &&
                predicateLength.test(typeDef, JpaConstants.MIN_LENGTH);

            Predicate<XmlTypeDef> chUnicodeString = typeDef -> predicateType.test(typeDef, "unicodestring") &&
                predicateLength.test(typeDef, JpaConstants.MIN_LENGTH);

            Predicate<XmlTypeDef> chBigDecimal = typeDef -> predicateType.test(typeDef, "bigdecimal") &&
                predicateLength.test(typeDef, JpaConstants.MIN_LENGTH);

            if (this.model.getXmlAlias() != null) {
                List<XmlTypeDef> belowMinimumLength = this.model.getXmlAlias().getTypeDefs().stream()
                    .filter(it -> chString.test(it) || chUnicodeString.test(it) || chBigDecimal.test(it))
                    .toList();

                if (!belowMinimumLength.isEmpty()) {
                    throw new TypeDefBelowMinimumPropertyLengthException(belowMinimumLength);
                }
            }
        }
    }

    /**
     * Checking that non-embeddable classes do not have collection embeddable properties
     */
    private void checkEmbeddedWithoutCollection() {
        this.model.getClassesAsList().forEach(modelClass -> {
                final List<String> embeddedCollectionProperties = modelClass.getPropertiesAsList().stream()
                    .filter(property -> property.getCollectionType() != null)
                    .filter(XmlModelClassProperty::isEmbedded)
                    .map(XmlModelClassProperty::getName)
                    .toList();
                if (!embeddedCollectionProperties.isEmpty()) {
                    throw new EmbeddedCollectionInEntityException(modelClass.getName(), embeddedCollectionProperties);
                }
            }

        );
        this.model.getInterfacesAsList().forEach(modelInterface -> {
                final List<String> embeddedCollectionProperties = modelInterface.getPropertiesAsList().stream()
                    .filter(property -> property.getCollectionType() != null)
                    .filter(property -> {
                        final XmlModelClass typeClass = model.getClassNullable(property.getType());
                        return typeClass != null && typeClass.isEmbeddable();
                    })
                    .map(XmlModelInterfaceProperty::getName)
                    .toList();
                if (!embeddedCollectionProperties.isEmpty()) {
                    throw new EmbeddedCollectionInEntityException(modelInterface.getName(), embeddedCollectionProperties);
                }
            }
        );
    }

    /**
     * Check that embedded fields cannot be required (mandatory), except for the identifier
     */
    private void checkNoMandatoryOnEmbeddedProperty(XmlModelClass modelClass) {
        List<XmlModelClassProperty> mandatoryEmbeddedProperties = modelClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isEmbedded)
            .filter(XmlModelClassProperty::isMandatory)
            .filter(property -> !property.isId())
            .filter(property -> !property.isExternalLink())
            .toList();

        if (!mandatoryEmbeddedProperties.isEmpty()) {
            throw new EmbeddedMandatoryException(modelClass, mandatoryEmbeddedProperties);
        }
    }

    /**
     * Checking default values
     * If the field is an enum, then check that such a value exists in the enum
     * If the field is not an enum, then check that the type is primitive and not collective
     */
    private void checkDefaultValues(XmlModelClass modelClass) {
        modelClass.getPropertiesAsList().stream()
            .filter(property -> property.getDefaultValue() != null)
            .forEach(property -> {
                String propertyType = property.getType();
                if (property.isEnum()) {
                    XmlModelClassEnum modelClassEnum = modelClass.getModel().getEnums().stream()
                        .filter(classEnum -> classEnum.getName().equals(propertyType))
                        .findFirst().orElseThrow(
                            () -> new IllegalStateException(
                                String.format("The model does not contain an enum class with the name %s.", propertyType)
                            )
                        );

                    Optional<XmlEnumValue> defaultEnumValue = modelClassEnum.getEnumValues().stream()
                        .filter(enumValue -> enumValue.getName().equals(property.getDefaultValue()))
                        .findFirst();

                    if (defaultEnumValue.isEmpty()) {
                        throw new DefaultEnumValueNotFoundException(property);
                    }

                    return;
                }

                TypeInfo typeInfo = ModelHelper.TYPES_INFO.get(propertyType.toLowerCase(Locale.ENGLISH));

                if (typeInfo == null || property.getCollectionType() != null) {
                    throw new DefaultValueNotOnSimplePropertyException(property);
                }
            });
    }

    /**
     * Checking that the accuracy of the BigDecimal field does not exceed its length and is non-negative
     * Check that the accuracy is specified only for BigDecimal fields
     */
    private void checkBigDecimalScale() {
        checkScaleInBounds();
        checkScaleOnlyOnBigDecimal();
    }

    private void checkScaleInBounds() {

        Predicate<String> isBigDecimal = type -> {
            TypeInfo typeInfo = TYPES_INFO.get(type.toLowerCase(Locale.ENGLISH));
            return typeInfo != null && "BigDecimal".equalsIgnoreCase(typeInfo.getJavaName());
        };

        model.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().stream()
                .filter(it -> isBigDecimal.test(it.getType()))
                .forEach(it -> {
                    Integer scale = it.getScale() == null ? TYPES_INFO.get("bigdecimal").getSecondNumber() : it.getScale();
                    Integer length = it.getLength() == null ? TYPES_INFO.get("bigdecimal").getFirstNumber() : it.getLength();
                    if (scale > length || scale < 0) {
                        throw new ScaleNotInBoundsException(it, scale, length);
                    }
                })
        );

        if (this.model.getXmlAlias() != null) {
            model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> isBigDecimal.test(it.getType()))
                .forEach(it -> {
                    Integer scale = it.getScale() == null ? TYPES_INFO.get("bigdecimal").getSecondNumber() : it.getScale();
                    Integer length = it.getLength() == null ? TYPES_INFO.get("bigdecimal").getFirstNumber() : it.getLength();
                    if (scale > length || scale < 0) {
                        throw new TypeDefScaleNotInBoundsException(it);
                    }
                });
        }
    }

    private void checkScaleOnlyOnBigDecimal() {
        List<XmlModelClassProperty> wrongScaledProperties = new ArrayList<>();
        this.model.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().stream()
                .filter(property -> {
                    TypeInfo typeInfo = TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH));
                    return !BigDecimal.class.getSimpleName().equals(typeInfo == null ? null : typeInfo.getJavaName()) &&
                        property.getScale() != null;
                })
                .forEach(wrongScaledProperties::add)
        );
        if (!wrongScaledProperties.isEmpty()) {
            throw new WrongScaledPropertyException(wrongScaledProperties);
        }

        if (this.model.getXmlAlias() != null) {
            List<XmlTypeDef> wrongScaledTypeDefs = this.model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> {
                    TypeInfo typeInfo = TYPES_INFO.get(it.getType().toLowerCase(Locale.ENGLISH));
                    return !BigDecimal.class.getSimpleName().equals(typeInfo == null ? null : typeInfo.getJavaName()) &&
                        it.getScale() != null;
                })
                .toList();

            if (!wrongScaledTypeDefs.isEmpty()) {
                throw new WrongScaledTypeDefException(wrongScaledTypeDefs);
            }
        }
    }

    private void checkFieldLength() {
        checkDateFieldsLength();
        checkUnnecessaryFieldsLength();
        //The text on external links does not define the tag length.Enough verification of model classes.
            checkUnnecessaryModelTypeLength();
    }

    private void checkDateFieldsLength() {

        BiPredicate<Integer, XmlModelClassProperty> propLengthExists = (minlength, it) -> it.getLength() != null &&
            (it.getLength() > MAX_DATE_LENGTH || it.getLength() < minlength);

        BiPredicate<String, Class<?>> isClass = (type, clazz) -> clazz.getSimpleName().equals(type);

        this.model.getClassesAsList().forEach(modelClass -> {
                List<XmlModelClassProperty> wrongProperties = new ArrayList<>();
                modelClass.getPropertiesAsList().stream()
                    .filter(it -> {
                        String typeJavaName = getTypeJavaName(it.getType());
                        return (propLengthExists.test(1, it) &&
                            (isClass.test(typeJavaName, Date.class) || isClass.test(typeJavaName, LocalDate.class)))
                            || (propLengthExists.test(0, it) &&
                            (isClass.test(typeJavaName, OffsetDateTime.class) || isClass.test(typeJavaName, LocalDateTime.class)));
                    })
                    .forEach(wrongProperties::add);

                if (!wrongProperties.isEmpty()) {
                    throw new DateFieldLengthException(modelClass.getName(), wrongProperties);
                }
            }
        );

        BiPredicate<Integer, XmlTypeDef> typeDefLengthExists = (minlength, it) -> it.getLength() != null &&
            (it.getLength() > MAX_DATE_LENGTH || it.getLength() < minlength);

        if (this.model.getXmlAlias() != null) {
            List<XmlTypeDef> wrongTypeDefs = this.model.getXmlAlias().getTypeDefs().stream()
                .filter(it -> {
                    String typeJavaName = getTypeJavaName(it.getType());
                    return (typeDefLengthExists.test(1, it) &&
                        (isClass.test(typeJavaName, Date.class) || isClass.test(typeJavaName, LocalDate.class)))
                        || (typeDefLengthExists.test(0, it) &&
                        (isClass.test(typeJavaName, OffsetDateTime.class) || isClass.test(typeJavaName, LocalDateTime.class)));
                })
                .toList();

            if (!wrongTypeDefs.isEmpty()) {
                throw new TypeDefDateFieldException(wrongTypeDefs);
            }
        }
    }

    private String getTypeJavaName(String userType) {
        TypeInfo typeInfo = TYPES_INFO.get(userType.toLowerCase(Locale.ENGLISH));
        return typeInfo == null ? null : typeInfo.getJavaName();
    }

    private void checkUnnecessaryFieldsLength() {
        Stream.of("byte[]", "text", "integer", "long", "byte", "short", "boolean", "character", "float", "double")
            .forEach(type -> {
                final TypeInfo typeInfo = TYPES_INFO.get(type);
                checkFieldUnnecessaryLengthForTypeDef(typeInfo);
                checkFieldUnnecessaryLengthForType(typeInfo);
            });

    }

    private void checkUnnecessaryModelTypeLength() {
        List<XmlModelClassProperty> wrongProperties = new ArrayList<>();
        model.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().stream()
                .filter(property -> (isModelClassType(model, property.getType()) && property.getLength() != null) ||
                    (isReferenceType(model, property.getType()) && property.getLength() != null))
                .forEach(wrongProperties::add)
        );

        if (!wrongProperties.isEmpty()) {
            throw new UnnecessaryFieldLengthException(wrongProperties);
        }
    }

    private void checkFieldUnnecessaryLengthForType(TypeInfo typeInfo) {
        this.model.getClassesAsList().forEach(modelClass -> {
            List<XmlModelClassProperty> wrongProperties = new ArrayList<>();
            modelClass.getPropertiesAsList().stream()
                .filter(it -> {
                    TypeInfo propertyTypeInfo = TYPES_INFO.get(it.getType().toLowerCase(Locale.ENGLISH));
                    return Objects.nonNull(it.getLength()) &&
                        Objects.equals(typeInfo, propertyTypeInfo);
                })
                .forEach(wrongProperties::add);

            if (!wrongProperties.isEmpty()) {
                throw new UnnecessaryFieldLengthException(wrongProperties);
            }
        });
    }

    private void checkFieldUnnecessaryLengthForTypeDef(TypeInfo typeInfo) {
        List<XmlTypeDef> wrongTypeDefs = new ArrayList<>();
        if (Objects.isNull(this.model.getXmlAlias())) {
            return;
        }
        this.model.getXmlAlias().getTypeDefs().stream()
            .filter(typeDef -> {
                TypeInfo typeDefTypeInfo = TYPES_INFO.get(typeDef.getType().toLowerCase(Locale.ENGLISH));
                return Objects.equals(typeInfo, typeDefTypeInfo) &&
                    Objects.nonNull(typeDef.getLength());
            })
            .forEach(wrongTypeDefs::add);
        if (!wrongTypeDefs.isEmpty()) {
            throw new UnnecessaryTypeDefLengthException(wrongTypeDefs);
        }
    }

    /**
     * Checking that the property type is not an abstract class
     */
    private void checkNoAbstractClassAsLink(XmlModelClass modelClass) {
        XmlModel model = modelClass.getModel();

        List<XmlModelClassProperty> abstractLinks = modelClass.getPropertiesAsList()
            .stream().filter(property -> {
                XmlModelClass aClass = model.getClassNullable(property.getType());

                return aClass != null && aClass.isAbstract();
            }).toList();


        if (!abstractLinks.isEmpty()) {
            throw new AbstractLinkException(modelClass.getName(), abstractLinks);
        }
    }

    /**
     * If the class is embeddable and not basic, and other non-basic classes refer to it, and it has required fields, then a warning
     * There may be an error if there will be a parallel class without such an embeddable field.
     * If the class is not embeddable and it has required fields - we just warn that the singleTable strategy,
     * also check that in the scope of parallel inheritance no identical parameter names are used, except for status fields
     */
    private void checkSingleTableRequirements(XmlModelClass modelClass) {
        // if the class is built-in
        if (modelClass.isEmbeddable()) {
            // if the first class is not abstract or it's BaseEntity, then exit
            if (modelClass.isBaseClassMark()
                || isBaseClass(modelClass.getName())) {
                return;
            }

            // if there are no required fields, then exit
            if (modelClass.getPropertiesAsList().stream().noneMatch(XmlModelClassProperty::isMandatory)) {
                return;
            }

            // If there are non-basic SingleTable classes that have embedded properties containing required fields, then these classes will be
            // then we generate a WARN message, as there may be parallel children without such required fields
                List<XmlModelClass> modelClasses = modelClass.getModel().getClassesAsList()
                .stream().filter(modelClass1 ->
                    modelClass1.getPropertiesAsList().stream()
                        .anyMatch(property -> property.getType().equals(modelClass.getName())))
                .filter(modelClass1 -> modelClass1.getStrategy() == ClassStrategy.SINGLE_TABLE)
                .filter(modelClass1 -> !modelClass1.isBaseClassMark())
                .toList();

            if (!modelClasses.isEmpty()) {
                LOGGER.warning("In the class " + modelClass.getName() + " there are required properties, while this embeddable class "
                    + "is defined as a property in classes:"
                    + modelClasses.stream().map(XmlModelClass::getName).collect(Collectors.joining(", "))
                    + " with inheritance strategy " + ClassStrategy.SINGLE_TABLE);
            }
        } else if (modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE) {

            List<XmlModelClassProperty> mandatoryProperties = modelClass.getPropertiesAsList()
                .stream().filter(XmlModelClassProperty::isMandatory)
                .toList();

            if (!mandatoryProperties.isEmpty()) {
                LOGGER.warning("Required properties are defined"
                    + mandatoryProperties.stream().map(XmlModelClassProperty::getName).collect(Collectors.joining(", "))
                    + " in the class " + modelClass.getName() + " whose inheritance strategy is " + ClassStrategy.SINGLE_TABLE);
            }

            // Checking that in parallel inheritance not used properties with the same name (with SingleTable strategy)
            // There is no sense to check for each class from the same inheritance chain, so we launch it only for the base one.
            if (modelClass.isBaseClassMark()) {
                checkDuplicateFieldsInInheritance(modelClass);
            }
        }
    }

    private void checkIdCategoryIsNotNull(XmlModelClass xmlModelClass) {

        final XmlId xmlId = xmlModelClass.getId();

        if (Objects.nonNull(xmlId) && Objects.isNull(xmlId.getIdCategory())) {
            throw new IdCategoryNullException(xmlModelClass.getName());
        }

    }

    private void checkDuplicateFieldsInInheritance(XmlModelClass modelClass) {
        if (modelClass.isHistoryClass()) {
            // Disable verification for historization classes, as fields may be duplicated in them вынужденно.
            // since in the historical strategy Joind is translated into SingleTable
            return;
        }

        Set<XmlModelClass> allInheritedClasses = ModelHelper.getAllChildClasses(modelClass, true);

        Map<String, List<XmlModelClassProperty>> groupedProperties = new HashMap<>();
        allInheritedClasses.stream()
            .flatMap(clazz -> clazz.getPropertiesAsList().stream())
            .flatMap(prop -> {
                return !prop.isEmbedded()
                    ? Stream.of(prop)
                    : ModelHelper.propertyToPropertyEmbeddable(prop).stream();
            })
            .forEach(property -> {
                String nameToCompare = property.getName().toLowerCase();
                groupedProperties.putIfAbsent(nameToCompare, new ArrayList<>());
                groupedProperties.get(nameToCompare).add(property);
            });

        Map<String, Integer> duplicatedProperties = groupedProperties.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, en -> en.getValue().size()));

        if (!duplicatedProperties.isEmpty()) {
            throw new DuplicateSingleTablePropertyNameException(modelClass, duplicatedProperties.keySet());
        }
    }

    // TODO not consider embedded fields and indexes on them, but is it needed?
    private boolean isPropertyHaveUniqueIndex(XmlModelClassProperty property) {
        if (property.isUnique()) {
            return true;
        }
        XmlModelClass modelClass = property.getModelClass();

        Optional<XmlIndex> uniqueIndex = modelClass.getIndices().stream()
            .filter(it -> it.getProperties().size() == 1 &&
                (it.getProperties().get(0).getName().equals(property.getName()) &&
                    it.isUnique()))
            .findFirst();

        return uniqueIndex.isPresent();
    }

    private boolean isPropertyNeedIndex(XmlModelClassProperty property) {
        if (property.isIndexed()) {
            return false;
        }

        List<XmlIndex> absorbIndices = ModelIndexUtils.findAbsorbIndices(property);

        return absorbIndices.isEmpty();
    }

    /**
     * Adds the type field to base non-final classes.
     * If the class is final, then the field is not added
     */
    private void addTypeField() {
        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.isBaseClassMark() && !modelClass.isFinalClass() && !modelClass.isEvent())
            .forEach(xmlModelClass -> {
                String defaultValue = xmlModelClass.getName();
                boolean useRenamedFields = this.parameterContext.getPluginParameters().isUseRenamedFields();
                xmlModelClass.addProperty(discriminatorField(defaultValue, useRenamedFields));
            });
    }

    private void externalTypesActions() {
        externalLinksEnricher();
        checkExternalTypesNames();
    }

    /**
     * Enriches properties with {@link XmlModelExternalType}
     */
    private void externalLinksEnricher() {
        Collection<XmlModelExternalType> externalTypes = model.getExternalTypes();
        if (externalTypes.isEmpty()) {
            return;
        }
        checkDuplicateExternalType(externalTypes);
        externalTypes.forEach(it -> {
            checkMergeKind(it);
            it.setReferenceType(findSoftReferenceTypeByReferenceType(it.getType()));
        });
    }

    private void checkExternalTypesNames() {
        final List<String> intersectedTypes = model.getExternalTypes().stream()
            .map(XmlModelExternalType::getType)
            .filter(type -> this.model.getClassesAsList()
                .stream()
                .anyMatch(modelClass -> Objects.equals(modelClass.getName(), type))
            )
            .toList();
        if (!intersectedTypes.isEmpty()) {
            throw new ExternalTypeIntersectionException(intersectedTypes);
        }
    }

    private void checkDuplicateExternalType(Collection<XmlModelExternalType> externalTypes) {
        List<String> listOfTypes = externalTypes.stream().map(XmlModelExternalType::getType).toList();
        Set<String> duplicateTypes = findDuplicateString(listOfTypes);
        if (!duplicateTypes.isEmpty()) {
            throw DuplicateException.duplicateExternalType("type", duplicateTypes.toString());
        }
        List<String> listOfMergeKind = externalTypes.stream().map(XmlModelExternalType::getMergeKind).toList();
        Set<String> duplicateMergeKind = findDuplicateString(listOfMergeKind);
        if (!duplicateMergeKind.isEmpty()) {
            throw DuplicateException.duplicateExternalType("merge-kind", duplicateMergeKind.toString());
        }
    }

    private Set<String> findDuplicateString(List<String> list) {
        return list.stream()
            .filter(i -> Collections.frequency(list, i) > 1)
            .collect(Collectors.toSet());
    }

    private String findSoftReferenceTypeByReferenceType(String type) {
        XmlModelClass modelClass = model.getClassesAsList().stream()
            .filter(it -> type.equals(it.getOriginalType()))
            .findFirst()
            .orElseThrow(() -> new SoftReferenceNotFoundException(type));
        return modelClass.getName();
    }

    private void checkMergeKind(XmlModelExternalType externalType) {
        Arrays.stream(MergeKind.values())
            .filter(it -> it.name().equalsIgnoreCase(externalType.getMergeKind()))
            .findFirst()
            .orElseThrow(() -> new UnknownMergeKindException(externalType));
    }


    /**
     * Setting up TypeInfo for interface attributes
     * The type attribute field of the interface is also modified.
     */
    private void processInterfaceClass(XmlModelInterface modelInterface) {
        modelInterface.getPropertiesAsList().forEach(this::processInterfaceClassProperty);
    }

    /**
     * Setting up TypeInfo for interface attributes
     * The type attribute field of the interface is also modified.
     */
    private void processInterfaceClassProperty(XmlModelInterfaceProperty interfaceProperty) {
        TypeInfo typeInfo = getTypeInfoForProperty(interfaceProperty);
        interfaceProperty.setTypeInfo(typeInfo);
    }

    /**
     * Moves physical class names from pdm to model.xml.
     * The physical name is not carried over if the new class is abstract or embeddable, or if there is no physical name in the old class.
     * (for example, if the old class was abstract or embeddable)
     */
    private void setTableNameFromPdm() {
        if (pdmModel == null || pdmModel.getModel() == null) {
            return;
        }

        pdmModel.getModel().getClassesAsList().stream()
            .filter(oldClass -> oldClass.getTableName() != null)
            .forEach(oldClass -> {
                XmlModelClass newClass = model.getClassNullable(oldClass.getName());
                if (newClass != null) {
                    // we check that the class really needs a physical name
                    if (newClass.isAbstract()
                        || newClass.isEmbeddable()
                        || NameHelper.notCopyTableName(newClass, oldClass)) {
                        return;
                    }
                    newClass.setTableName(oldClass.getTableName());
                } else {
                    if (oldClass.getStrategy() != ClassStrategy.SINGLE_TABLE || oldClass.isBaseClassMark()) {
                        model.addTableName(oldClass.getTableName());
                    }
                }
            });

        // Translate collection field names
        pdmModel.getModel().getClassesAsList()
            .forEach(oldClass -> {
                XmlModelClass newClass = model.getClassNullable(oldClass.getName());
                if (newClass != null) {
                    oldClass.getPropertiesAsList().stream()
                        .filter(property -> property.getCollectionTableName() != null)
                        .forEach(oldProp -> {
                            XmlModelClassProperty newProp = newClass.getPropertyNullable(oldProp.getName());
                            if (newProp != null) {
                                if (newProp.getCollectionType() == null
                                    || newProp.getMappedBy() != null) {
                                    return;
                                }
                                newProp.setCollectionTableName(oldProp.getCollectionTableName());
                            } else {
                                model.addTableName(oldProp.getCollectionTableName());
                            }
                        });
                } else {
                    oldClass.getPropertiesAsList().stream()
                        .map(XmlModelClassProperty::getCollectionTableName)
                        .filter(Objects::nonNull)
                        .forEach(model::addTableName);
                }
            });
    }

    private void setColumnNameFromPdm() {
        if (pdmModel == null || pdmModel.getModel() == null) {
            return;
        }

        pdmModel.getModel().getClassesAsList()
            .forEach(oldClass -> {
                XmlModelClass newClass = model.getClassNullable(oldClass.getName());
                if (newClass != null) {
                    oldClass.getPropertiesAsList()
                        .forEach(oldProp -> {
                            XmlModelClassProperty newProp = newClass.getPropertyNullable(oldProp.getName());
                            if (newProp != null) {
                                if (NameHelper.notCopyColumnName(newProp, oldProp)) {
                                    return;
                                }
                                if (!newProp.isEmbedded()) {
                                    newProp.setColumnName(oldProp.getColumnName());
                                    addColumnNameToClass(newClass, newProp);
                                } else {
                                    setEmbeddedColumnNameFromPdm(newProp, oldProp);
                                }
                            } else {
                                (oldProp.isEmbedded()
                                    ? ModelHelper.propertyToPropertyEmbeddable(oldProp)
                                    : Collections.singletonList(oldProp))
                                    .stream()
                                    .filter(prop -> prop.getColumnName() != null)
                                    .forEach(prop -> addColumnNameToClass(newClass, prop));
                            }
                        });
                } else {
                    // If the table is ST and we are not in an interim build, then it is necessary to save the physical field names.
                    // removed classes on base for ST
                    if (oldClass.getStrategy() == ClassStrategy.SINGLE_TABLE
                        && !parameterContext.getPluginParameters().isIntermediaryBuild()
                        && !parameterContext.getPluginParameters().isDropDeletedItemsImmediately()
                    ) {
                        XmlModelClass oldClassBase = ModelHelper.getBaseClass(oldClass);
                        XmlModelClass newClassBase = model.getClassNullable(oldClassBase.getName());
                        if (newClassBase != null) {
                            ModelHelper.propertyToPropertyEmbeddable(oldClass.getPropertiesAsList())
                                .stream()
                                .filter(property -> property.getColumnName() != null)
                                .forEach(oldProp -> addColumnNameToClass(newClassBase, oldProp));
                        }
                    }
                }
            });
    }

    private static void setEmbeddedColumnNameFromPdm(XmlModelClassProperty newProperty,
                                                     XmlModelClassProperty oldProperty) {

        if (oldProperty == null || !oldProperty.isEmbedded()) {
            return;
        }
        List<XmlModelClassPropertyEmbeddable> oldEmbeddableProperties
            = ModelHelper.propertyToPropertyEmbeddable(oldProperty);
        List<XmlModelClassPropertyEmbeddable> newEmbeddableProperties
            = ModelHelper.propertyToPropertyEmbeddable(newProperty);

        Map<String, XmlModelClassPropertyEmbeddable> oldPropMap = oldEmbeddableProperties.stream()
            .collect(Collectors.toMap(XmlModelClassPropertyEmbeddable::getName, it -> it));

        newEmbeddableProperties.forEach(newProp -> {
            XmlModelClassPropertyEmbeddable oldProp = oldPropMap.get(newProp.getName());
            if (oldProp != null) {
                newProp.setColumnName(oldProp.getColumnName());
                addColumnNameToClass(newProperty.getModelClass(), newProp);
            }
        });
    }

    /**
     * Generation of table name and primary key for non-abstract and non-embedded classes. The name is placed in the class and model
     * Setting flags (ObjectLinks) O2M, M2O, O2O on properties
     * Setting the physical type property (typeInfo)
     * Fill in indexName with embedded attributes
     * Fill in the indexed attribute on reference fields
     * Setting the physical column name in the field and model
     * Setting the physical name of the collection field table
     * Setting the physical name of the index for collective fields
     * Setting the physical name of the Order field in the collection
     * Check that no collection list is applied to reference fields
     * Check that clob and blob are not used with indexes
     * Fill in the index name on the field and model, if the field is indexed
     */
    private void processModelClass(XmlModelClass modelClass) {
// Checking the existence of extensible classes is performed in allExtendedClassExistsInModel
        int maxDBObjectNameLength = parameterContext.getPluginParameters().getMaxDBObjectNameLength();

        //If the class is not abstract and not built - in
        if (!modelClass.isAbstract() && !modelClass.isEmbeddable()) {
            // generation and setting the table name in the class and model
            if (modelClass.getTableName() == null) {
                NameHelper.setTableName(modelClass, pdmModel != null ? pdmModel.getModel() : null, maxDBObjectNameLength);
            }
        }

        // Setting flags (ObjectLinks) O2M, M2O, O2O
        setPropertiesObjectLinks(modelClass);

        modelClass.getPropertiesAsList().forEach(this::processModelClassProperty);
    }

    /**
     * Setting the physical type property (typeInfo)
     * Fill in indexName with embedded attributes
     * Fill in the indexed attribute on reference fields
     * Setting the physical column name in the field and model
     * Setting the physical name of the collection field table
     * Setting the physical name of the index for collective fields
     * Setting the physical name of the Order field in the collection
     * Check that no collection list is applied to reference fields
     * Check that clob and blob are not used with indexes
     * Fill in the index name on the field and model if the field is indexed.
     */
    private void processModelClassProperty(XmlModelClassProperty modelClassProperty) {
        // установка физического типа свойства
        Models.fillCategoryAndTypeInfo(modelClassProperty);

        // skipping the mappedBy property
        if (modelClassProperty.getMappedBy() != null || modelClassProperty.isEmbedded()) {
            return;
        }

        XmlModelClass propertyModelClass = modelClassProperty.getModelClass();

        // If the field is a direct link, then we mark that it needs an index
        if (modelClassProperty.getObjectLinks() == ObjectLinks.M2O) {
            if (isPropertyNeedIndex(modelClassProperty)) {
                modelClassProperty.setIndex(true);
            }
        }

        //The situation occurs when the link is unique or the back reference is not collective(there may be no index).
        //Therefore, we always set the index in any case
        if (modelClassProperty.getObjectLinks() == ObjectLinks.O2O) {
            if (isPropertyNeedIndex(modelClassProperty)) {
                modelClassProperty.setIndex(true);
            }
            // TODO possibly, one can simply set the flag right away without checking.
            // Currently, it is unclear what will happen if there is a composite unique index and you set the uniqueness flag.
            if (!isPropertyHaveUniqueIndex(modelClassProperty)) {
                modelClassProperty.setUnique(Boolean.TRUE);
            }
        }

        PluginParameters pluginParameters = parameterContext.getPluginParameters();
        int maxDBObjectNameLength = pluginParameters.getMaxDBObjectNameLength();
        if (StringUtils.isEmpty(modelClassProperty.getColumnName())) {
            // Store physical column names information into the class
            String columnName = NameHelper.generateColumnName(
                modelClassProperty,
                maxDBObjectNameLength,
                pluginParameters.isUseRenamedFields()
            );
            modelClassProperty.setColumnName(columnName);
            addColumnNameToClass(propertyModelClass, modelClassProperty);
        }

        if (modelClassProperty.getCollectionType() != null) {
            //collection property
            if (!modelClassProperty.getTypeInfo().isCollect()) {
                throw new UnsupportedSimpleListReferenceCollectionException(modelClassProperty);
            }

            // установка физического имени таблицы коллекционного поля
            // adding table name to model
            if (modelClassProperty.getCollectionTableName() == null) {
                modelClassProperty.setCollectionTableName(
                    NameHelper.getTableNameForCollectionProperty(
                        modelClassProperty,
                        maxDBObjectNameLength,
                        pluginParameters.getMinCroppedClassNameLength(),
                        pluginParameters.getMinCroppedPropertyNameLength()
                    )
                );
            }

            // setting the key physical field for the collection
            // TODO there is a suspicion of cases when an incorrect name will be installed
            modelClassProperty.setKeyColumnName(
                NameHelper.getPrimitiveCollectionPkColumnName(propertyModelClass, maxDBObjectNameLength)
            );
        }
        if (modelClassProperty.getCollectionType() == CollectionType.LIST) {
            if (modelClassProperty.getCategory() == PropertyType.REFERENCE) {
                throw new UnsupportedListReferenceCollectionException(propertyModelClass.getName(),
                    modelClassProperty.getName());
            }
            modelClassProperty.setOrderColumnName(
                NameHelper.getPrimitiveCollectionOrderColumnName(modelClassProperty, maxDBObjectNameLength)
            );
        }

        if (getForeignKeyCondition(modelClassProperty)) {
            if (isEnableCreateForeignKeys(pluginParameters)) {
                prepareGenerationForeignKey(modelClassProperty, pluginParameters.isEnableDeleteCascade());
            } else if (isEnableDropForeignKeys(pluginParameters)) {
                prepareDropForeignKey(modelClassProperty);
            } else {
                transferFkPropertiesFromOldModel(modelClassProperty, pdmModel, pluginParameters.isEnableDeleteCascade());
            }
        }

    }

    /**
     * Adds physical column name to the class.
     * If the name is already present in the class, an exception of type ColumnNameAlreadyDefinedException is thrown
     */
    private static void addColumnNameToClass(XmlModelClass modelClass, XmlModelClassProperty property) {
        String columnName = property.getColumnName();
        Set<String> columnNames = modelClass.getColumnNamesReal();
        if (!columnNames.add(columnName)) {
            throw new ColumnNameAlreadyDefinedException(modelClass.getName(), columnName);
        }
    }

    /**
     * Setting flags for ObjectLinks O2M, M2O, O2O
     * The collection of backlinks is allowed, even if the direct link is unique.
     * For parent links, backward references are definitely required (not in this method, but checked in AggregateGenerator).
     */
    // TODO reviewing and commenting on the code is necessary
    private void setPropertiesObjectLinks(XmlModelClass modelClass) {
        modelClass.getPropertiesAsList().forEach(property -> {
            XmlModelClass propTypeClass = modelClass.getModel().getClassNullable(property.getType());
            // Skipping properties that are not links
            if (propTypeClass == null || propTypeClass.isEmbeddable()) {
                return;
            }

            // if the link is not collective
            if (property.getCollectionType() == null) {
                // search for a direct link to the property type if the current one is reverse
                // correctness of links by types checked in checkMappedByProperties
                Optional<XmlModelClassProperty> forwardMapping
                    = Optional.ofNullable(property.getMappedByProperty());

                // search for reverse link to property type, if current is direct
                // correctness of links by types checked in checkMappedByProperties
                Optional<XmlModelClassProperty> backMapping = propTypeClass.getPropertiesAsList()
                    .stream()
                    .filter(linkProperty -> Objects.equals(property.getName(), linkProperty.getMappedBy())
                        // on a class, there can be multiple bi-directional references with the same mappedBy name but different types
                        && modelClass.getName().equals(linkProperty.getType())).findAny();

                // if there is a direct link (which means the current reverse one)
                if (forwardMapping.isPresent()) {
                    // above we checked that the backlink is not a collection link, which means the connection is 1-1
                    property.setObjectLinks(ObjectLinks.O2O);
                } else if (backMapping.isPresent() && backMapping.get().getCollectionType() == null
                    || isPropertyHaveUniqueIndex(property)) {
                    // If there is an inverse reference (which means the current one is direct)and the inverse reference is not collective,
                    // or the current field is marked as unique, then 1-1
                        property.setObjectLinks(ObjectLinks.O2O);
                } else {
                    // otherwise N-1
                    property.setObjectLinks(ObjectLinks.M2O);
                }
            } else {
                // if the link is collectible

                // determining if the link is reverse
                String mappedBy = property.getMappedBy();
                if (mappedBy != null) {
                    // Reverse and collect link, so 1 - N
                    property.setObjectLinks(ObjectLinks.O2M);
                } else {
                    // link direct and collective, so M-M,
                    // but such references are forbidden, an exception will be thrown later
                    property.setObjectLinks(ObjectLinks.M2M);
                }
            }
        });
    }

    private void validPrimitiveDefaultValue(XmlModelClass xmlModelClass) {
        xmlModelClass.getPropertiesAsList().forEach(property ->
        {
            String propertyType = property.getType().toLowerCase(Locale.ENGLISH);
            if (Objects.isNull(TYPES_INFO.get(propertyType))) {
                return;
            }
            String defaultValue = property.getDefaultValue();
            if (defaultValue == null) {
                return;
            }

            Object defValObj = null;
            try {
                switch (propertyType) {
                    case "character":
                        if (defaultValue.length() == 1) {
                            defValObj = defaultValue;
                            break;
                        } else {
                            throw new ParseException("The number of characters in the default-value for the Character type must be one.", 0);
                        }
                    case "unicodestring":
                    case "string":
                        if (defaultValue.length() <= property.getLength()) {
                            defValObj = defaultValue;
                            break;
                        } else {
                            throw new ParseException("The number of characters in the default value is greater than the field size.", 0);
                        }
                    case "text":
                        break;
                    case "boolean":
                        if ("true".equals(defaultValue) || "false".equals(defaultValue)) {
                            defValObj = defaultValue;
                            break;
                        } else {
                            throw new ParseException("default value for Boolean type provides only true or false.", 0);
                        }
                    case "decimal":
                    case "bigdecimal":
                        defValObj = new BigDecimal(defaultValue);
                        break;
                    case "integer":
                        defValObj = Integer.valueOf(defaultValue);
                        break;
                    case "short":
                        defValObj = Short.valueOf(defaultValue);
                        break;
                    case "long":
                        defValObj = Long.valueOf(defaultValue);
                        break;
                    case "byte":
                        defValObj = Byte.valueOf(defaultValue);
                        break;
                    case "float":
                        defValObj = Float.valueOf(defaultValue);
                        break;
                    case "double":
                        defValObj = Double.valueOf(defaultValue);
                        break;
                    case "date":
                        if (!"now".equalsIgnoreCase(defaultValue)) {
                            defValObj = parseDate(defaultValue);
                        } else {
                            defValObj = defaultValue;
                        }
                        break;
                    case "localdatetime":
                        if (!"now".equalsIgnoreCase(defaultValue)) {
                            defValObj = parseLocalDateTime(defaultValue);
                        } else {
                            defValObj = defaultValue;
                        }
                        break;
                    case "localdate":
                        if (!"now".equalsIgnoreCase(defaultValue)) {
                            defValObj = LocalDate.parse(defaultValue, DateTimeFormatter.ofPattern(DATE_PATTERN));
                        } else {
                            defValObj = defaultValue;
                        }
                        break;
                    case "offsetdatetime":
                        if (!"now".equalsIgnoreCase(defaultValue)) {
                            defValObj = parseOffsetDateTime(defaultValue);
                        } else {
                            defValObj = defaultValue;
                        }
                        break;
                    default:
                        defValObj = defaultValue;
                }
            } catch (NumberFormatException | ParseException | DateTimeParseException exc) {
                throw new DefaultPrimitiveValueException(property, exc.getMessage());
            }
            String infoLoggerMessage = getLogMessage(property) + ' ' + defValObj;
            LOGGER.info(infoLoggerMessage);
        });
    }

    private Date parseDate(String value) throws ParseException {
        ParseException ex = new ParseException("Initial exception", 0);
        for (DateFormat formatter : DATE_FORMATTERS) {
            try {
                return formatter.parse(value);
            } catch (ParseException exception) {
                ex = exception;
            }
        }
        throw ex;
    }

    private LocalDateTime parseLocalDateTime(String value) {
        DateTimeParseException ex = new DateTimeParseException("Initial exception", value, 0);
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException exception) {
                ex = exception;
            }
        }
        throw ex;
    }

    private OffsetDateTime parseOffsetDateTime(String value) {
        DateTimeParseException ex = new DateTimeParseException("Initial exception", value, 0);
        for (DateTimeFormatter formatter : OFFSET_FORMATTERS) {
            try {
                return OffsetDateTime.parse(value, formatter);
            } catch (DateTimeParseException exception) {
                ex = exception;
            }
        }
        throw ex;
    }

    private String getLogMessage(XmlModelClassProperty property) {
        return "Default value for " +
            property.getClass().getName() +
            '.' +
            property.getName();
    }

    private void checkNoManyToManyLink(XmlModelClass modelClass) {
        List<XmlModelClassProperty> properties = modelClass.getPropertiesAsList().stream()
            .filter(modelClassProperty -> ObjectLinks.M2M == modelClassProperty.getObjectLinks())
            .toList();

        if (!properties.isEmpty()) {
            throw new ManyToManyNotSupportedException(modelClass.getName(), properties);
        }
    }

    /**
     * Initialization of embeddedList on classes. If values exist, they are only supplemented in the part of missing data
     */
    public static void initEmbeddedList(XmlModelClass modelClass, int maxColumnNameLength) {
        modelClass.getPropertiesWithIncome().stream().filter(XmlModelClassProperty::isEmbedded)
            .flatMap(property -> ModelHelper.propertyToPropertyEmbeddable(property).stream())
            .filter(property -> property.getColumnName() == null)
            .forEach(property -> {
                property.setColumnName(
                    NameHelper.getEmbeddedColumnName(
                        property.getParentProperty(),
                        property.getEmbeddableProperty(),
                        maxColumnNameLength)

                );
                addColumnNameToClass(modelClass, property);
            });
    }

    private void setupLengthOffsetDateTime(XmlModelClass xmlModelClass) {
        xmlModelClass.getPropertiesAsList().stream()
            .filter(property -> "offsetdatetime".equalsIgnoreCase(property.getType()))
            .filter(property -> property.getLength() == null)
            .forEach(property -> property.setLength(3));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * CHECKING QUERY
     */

    // The main method for checking all Query is the entry point of the check method(the main method in the Checker)
    private void checkQueries() {
        //list of all exceptions for query
        List<SdkException> listQueryException = new ArrayList<>();
        Collection<XmlQuery> queriesAsList = model.getQueriesAsList();
        queriesAsList.forEach(query -> checkEveryQuery(listQueryException, query));

        if (!listQueryException.isEmpty()) {
            throw new GeneralizedModelException(listQueryException);
        }
    }

    //Check each element of the Query separately
    private void checkEveryQuery(List<SdkException> list, XmlQuery query) {

        // Check that each query contains at least one column tag
        if (query.getProperties() == null || query.getProperties().size() == 0) {
            list.add(new UserQueryHasNoPropertiesException(query.getName()));
        }
        checkNameIsNotEqualToOtherTagNames(list, query.getName());
        checkDialectsInBodies(list, query);
        checkQueryDuplicatedProperty(list, query);
        query.getParams().forEach(param -> checkQueryParam(list, param));

    }

    //Checking that among all the body tags in a certain query there are no duplicates
    private void checkDialectsInBodies(List<SdkException> list, XmlQuery query) {
        List<XmlQueryDBMS> dialects = query.getImplementations().stream()
            .flatMap(userQuery -> userQuery.getDbms().stream())
            .toList();

        Set<XmlQueryDBMS> duplicatedDialects = dialects.stream()
            .filter(i -> Collections.frequency(dialects, i) > 1)
            .collect(Collectors.toSet());

        if (!duplicatedDialects.isEmpty()) {
            StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
            for (XmlQueryDBMS duplicatedDialect : duplicatedDialects) {
                if (duplicatedDialect == XmlQueryDBMS.ANY) {
                    stringJoiner.add("''empty value (default dialect)'");
                } else {
                    stringJoiner.add(duplicatedDialect.getValue());
                }
            }
            list.add(new UserQueryDialectDuplicateException(query.getName(), stringJoiner.toString()));
        }
    }

    // Check for duplication of property -->
    //will issue a list of duplicate property -> if the list is empty, it does not throw an exception; otherwise, we add it to the list of exceptions
    private List<String> checkDuplicatePropertyList(XmlQuery xmlQuery) {

        List<XmlQueryProperty> duplicatedProperty = xmlQuery.getPropertiesAsList().stream()
            .filter(property -> Collections.frequency(xmlQuery.getPropertiesAsList(), property) > 1)
            .toList();
        List<String> list = new ArrayList<>();
        for (XmlQueryProperty property : duplicatedProperty) {
            list.add("|| QUERY_NAME:" + property.getXmlQuery().getName() + "; PROPERTY_NAME:" + property.getName() + "; PROPERTY_TYPE:" + property.getType() + " ||");
        }
        return list;
    }

    private void checkQueryDuplicatedProperty(List<SdkException> list, XmlQuery xmlQuery) {
        if (!checkDuplicatePropertyList(xmlQuery).isEmpty()) {
            list.add(DuplicateException.queryDuplicateProperty(checkDuplicatePropertyList(xmlQuery)));
        }
        for (XmlQueryProperty property : xmlQuery.getPropertiesAsList()) {
            TypeInfo typeInfo = null;
            try {
                typeInfo = TYPES_INFO.get(property.getType().toLowerCase(Locale.ENGLISH));
            } catch (Exception ex) {
                if (typeInfo == null) {
                    list.add(new UnknownTypeException(property));

                }
            }
        }
    }

    //<--

    // Checking parameters of query for correctness of completion
    private void checkQueryParam(List<SdkException> list, XmlQueryParam xmlQueryParam) {
        if (xmlQueryParam.getType() == null) {
            list.add(new NullTypeException(xmlQueryParam));
        }
        TypeInfo typeInfo = null;
        try {
            typeInfo = TYPES_INFO.get(xmlQueryParam.getType().toLowerCase(Locale.ENGLISH));
        } catch (Exception ex) {
            if (typeInfo == null) {
                list.add(new UnknownTypeException(xmlQueryParam));
            }
        }
    }

    // Checking the name of the Query
    private void checkNameIsNotEqualToOtherTagNames(List<SdkException> list, String name) {
        if (model.containsClass(name)) {
            list.add(new UserQueryNameAsModelTagNameException(name, "class"));
        }
        if (model.containsInterface(name)) {
            list.add(new UserQueryNameAsModelTagNameException(name, "interface"));
        }
        if (model.containsAlias(name)) {
            list.add(new UserQueryNameAsModelTagNameException(name, "alias type"));
        }
        if (model.containsEnum(name)) {
            list.add(new UserQueryNameAsModelTagNameException(name, "enumerations"));
        }
        if (TYPES_INFO.containsKey(name)) {
            list.add(new UserQueryNameAsModelTagNameException(name, "примитива"));
        }
    }

    private void checkDiffs(ParameterContext parameterContext, PdmModel lastPdmVersion, boolean disableCompatibilityCheck) {
        ModelParameters modelParameters = parameterContext.getModelParameters();
        PluginParameters pluginParameters = parameterContext.getPluginParameters();
        boolean generateForeignKeys = isEnableCreateForeignKeys(pluginParameters);
        XmlModel newModel = modelParameters.getModel();
        if (lastPdmVersion == null) {
            newModel.getClassesAsList().forEach(modelClass -> {
                modelParameters.addDiffObject(ElementState.NEW, modelClass);
                modelClass.getPropertiesAsList().forEach(xmlModelClassProperty -> {
                    modelParameters.addDiffObject(ElementState.NEW, xmlModelClassProperty);
                    if (generateForeignKeys) {
                        prepareGenerationForeignKey(xmlModelClassProperty, pluginParameters.isEnableDeleteCascade());
                    }
                });
            });
            return;
        }

        XmlModel lastModelVersion = lastPdmVersion.getModel();

        handleTablePrefix(lastModelVersion, newModel);
        handleAutoIdMethod(lastModelVersion, newModel);

        // When this function is running, the old model should not be changed yet, namely, the property types should not be changed,
        // otherwise, the algorithm will not find fields to link metadata about embeddable fields with the fields of the embeddable class
        initEmbeddedInPreviousModel(lastModelVersion) ;

        // In the old model, there may be indices with undisclosed embedded fields(a legacy), they need to be disclosed.
        setIncomeProperties(lastModelVersion);
        ModelIndexLogic.checkAllIndexPropertyExistsInClassesAndSetXmlProperty(lastModelVersion);
        ModelIndexLogic.replaceEmbeddedPropertiesInIndex(lastModelVersion);
        lastModelVersion.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().forEach(Models::fillCategoryAndTypeInfo));
        lastModelVersion.getInterfacesAsList().forEach(modelInterface ->
            modelInterface.getPropertiesAsList().forEach(xmlModelInterfaceProperty ->
                xmlModelInterfaceProperty.setTypeInfo(getTypeInfoForProperty(xmlModelInterfaceProperty))));

        // Replacing the model in modelParameters with a model from pdm
        modelParameters.setModel(lastModelVersion);

        swapData(newModel, lastModelVersion);

        // changes in non-deleted properties are processed
        new PropertyDiff(disableCompatibilityCheck).handler(newModel, lastModelVersion, parameterContext);
        // changes in remote properties and classes are handled
        new ClassDiff(disableCompatibilityCheck).handler(newModel, lastModelVersion, parameterContext);
        new EventDiff().handler(newModel, lastModelVersion);
        new BaseModelDiff().handler(newModel, lastModelVersion, parameterContext);
        new EnumDiff().handler(newModel, lastModelVersion, parameterContext);
        new QueryDiff().handler(newModel, lastModelVersion, parameterContext);

        // move user requests
        lastModelVersion.setQueries(newModel.getQueries().stream().map(XmlQuery::copy).toList());

        addIncomeObjectIdPropertiesForInheritors(lastModelVersion);

        setVersionDeprecated(lastModelVersion, modelParameters);

        if (!pluginParameters.isDeprecateDeletedItems()) {
            addDeletedItemsToXmlUnusedSchemaItems(newModel, lastModelVersion, modelParameters, pluginParameters);
            unusedSchemaItemsDiffHandler(newModel, lastModelVersion);
        }

        // here we mark model elements as isRemoved = true and set the version
        if ((pluginParameters.isDeprecateDeletedItems() && pluginParameters.isDropRemovedItems()) ||
            disableCompatibilityCheck ||
            !pluginParameters.isDeprecateDeletedItems()) {
            checkReadyDeprecatedElementsForRemove(newModel, lastModelVersion, modelParameters, disableCompatibilityCheck, pluginParameters);
        }

        // And here we add elements marked as isRemoved to the script generation list for deletion
        if (pluginParameters.isDeprecateDeletedItems() &&
            (pluginParameters.isDropRemovedItems() || disableCompatibilityCheck)) {
            addMarkedElementsForDeletion(lastModelVersion, modelParameters);
        }

        // Indices
        new IndexDiff(disableCompatibilityCheck).handler(newModel, lastModelVersion, parameterContext);

        /**
         The text "AbstractPlugin.deprecateDeletedItems" is already in English, so no translation is necessary. The original text remains unchanged.
         and when the flag dropDeletedItemsImmediately is enabled, our columns and tables are removed in the same release.
         of which they were deleted from model.xml. However, when deleting such objects, scripts are pre-generated
         * for deletion of indexes, setting of remarks, removal of the mandatory attribute for such objects.
         * But if it happens in this same release, in case of an attempt to roll back, an error will be received,
         * since the object itself for which this operation should be performed will not exist,
         * since in the new mechanism of rollback to delete a column or table we do not have it.
         * This was the problem description. The solution below is that when installing the flag dropDeletedItemsImmediately,
         for deleted elements we block the generation of any other scripts
         *   + upd.1 - do not generate scripts for setting remarks and any property changes (e.g., dropNotNullConstraint)
         * to remove index generation did not work out (there was an idea to delete all additional properties
         * through drop column cascade, but it is not supported by h2); the index will have to be dropped explicitly by substituting it in
         *   pluginParameters.isDropDeletedItemsImmediately пустой rollback
         *
         */
        if (!pluginParameters.isDeprecateDeletedItems() && pluginParameters.isDropDeletedItemsImmediately()) {
            List<XmlIndex> indexes = modelParameters.getObjectByType(ElementState.REMOVED, XmlIndex.class);
            List<XmlModelClassProperty> properties = modelParameters.getObjectByType(ElementState.DEPRECATED, XmlModelClassProperty.class);
            properties.stream()
                .filter(property -> modelParameters.containsObjectInDiff(ElementState.UPDATED, property))
                .forEach(property -> modelParameters.dropFromCategory(ElementState.UPDATED, property));
            properties.forEach(property -> modelParameters.dropFromCategory(ElementState.DEPRECATED, property));
            List<XmlModelClass> classes = modelParameters.getObjectByType(ElementState.DEPRECATED, XmlModelClass.class);
            classes.forEach(modelClass -> modelParameters.dropFromCategory(ElementState.DEPRECATED, modelClass));
        }

        modelParameters.getExecutingModelGenerate().forEach(modelGenerate ->
            modelGenerate.checkDiffs(modelParameters, lastPdmVersion));

        // And once again we set the income property, after updating the pdm, otherwise added in abstract
        // classes properties will not be processed in existing non-abstract descendants
        // TODO this should be reworked.
        setIncomeProperties(lastModelVersion);

        lastModelVersion.getImports().forEach(xmlImport -> {
            Optional<ModelGenerate> first = modelGenerates.stream()
                .filter(modelGenerate -> modelGenerate.getProjectName().equals(xmlImport.getType()))
                .findFirst();

            if (first.isEmpty()) {
                LOGGER.warning("The specified import (" + xmlImport.getType() + ") was not found in the file system: " + xmlImport.getFile());
            }
        });

        lastModelVersion.setVersion(newModel.getVersion());
    }

    /**
     * Ensures that there are no two history classes referencing the same base class
     */
    private void checkSingularHistoryClassReference(XmlModel model) {
        // Group by model class, take groups with more than 1 history class
        Map<String, List<XmlModelClass>> intersectingClasses = model.getClassesAsList().stream()
            .filter(XmlModelClass::isHistoryClass)
            .collect(Collectors.collectingAndThen(
                Collectors.groupingBy(XmlModelClass::getHistoryForClassName),
                groups -> groups.entrySet().stream()
                    .filter(group -> group.getValue().size() > 1)
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                    ))
            ));
        if (!intersectingClasses.isEmpty()) {
            throw new CheckModelException(
                "Only 1 history class is allowed for the historized class, intersection of history classes:" + intersectingClasses,
                "An abnormal situation occurred, requiring attention from the manufacturer"
            );
        }
    }

    /**
     * All deleted elements that passed the check (similar to {@link #checkDeletedElementsFromModel(ModelParameters)}),
     * are entered in the corresponding elements of the section <unusedSchemaItems> (XmlUnusedSchemaItems)
     *
     * @param newModel
     * @param baseModel
     * @param modelParameters
     */
    private void addDeletedItemsToXmlUnusedSchemaItems(XmlModel newModel, XmlModel baseModel, ModelParameters modelParameters, PluginParameters pluginParameters) {
        String newModelVersion = getModelVersion(modelParameters, model);
        if (isSnapshotVersion(newModelVersion)) {
            return;
        }

        int newMajorModelVersion = Semver.of(newModelVersion).getMajorVersion();

        XmlUnusedSchemaItems unusedSchemaItems =
            Objects.nonNull(newModel.getUnusedSchemaItems()) ?
                newModel.getUnusedSchemaItems() :
                new XmlUnusedSchemaItems();

        baseModel.getClassesAsList().stream()
            .forEach(oldClass -> {
                if (newModel.containsClass(oldClass.getName()) && (oldClass.getClassAccess() == Changeable.UPDATE)) {

                    handleDeleteProperties(
                        baseModel,
                        pluginParameters,
                        newModelVersion,
                        newMajorModelVersion,
                        unusedSchemaItems,
                        oldClass
                    );

                } else if (!newModel.containsClass(oldClass.getName())) {
                    handleDeleteClass(
                        baseModel,
                        modelParameters,
                        pluginParameters,
                        newModelVersion,
                        newMajorModelVersion,
                        unusedSchemaItems,
                        oldClass
                    );
                }
            });

        if (!unusedSchemaItems.getUnusedColumns().isEmpty() || !unusedSchemaItems.getUnusedTables().isEmpty()) {
            unusedSchemaItems.setModel(newModel);
        }
    }

    private void handleDeleteClass(XmlModel baseModel,
                                   ModelParameters modelParameters,
                                   PluginParameters pluginParameters,
                                   String newModelVersion,
                                   int newMajorModelVersion,
                                   XmlUnusedSchemaItems unusedSchemaItems,
                                   XmlModelClass oldClass) {

        if (Boolean.TRUE.equals(oldClass.isDictionary())) {
            throw new DictionaryClassDeleteException(oldClass.getName());
        }

        if (Boolean.FALSE.equals(oldClass.isDeprecated())) {
            return;
        }
        if (Objects.isNull(oldClass.getVersionDeprecated())) {
            return;
        }

        final List<XmlUnusedTable> unusedTables = unusedSchemaItems.getUnusedTables();
        final List<XmlUnusedColumn> unusedColumns = unusedSchemaItems.getUnusedColumns();

        int deprecatedClassMajorVersion = getDeprecateMajorVersion(oldClass.getVersionDeprecated());
        // this check has already been performed only for user classes in the method
        // checkDeletedElementsFromModel, here it is added to verify all classes
        if (isVersionAllowsDeleteItems(newMajorModelVersion, deprecatedClassMajorVersion, pluginParameters)) {
            if (oldClass.isEmbeddable()) {
                baseModel.getClassesAsList().stream()
                    .filter(modelClass -> !modelClass.isAbstract())
                    .filter(modelClass -> modelClass.getPropertiesAsList().stream()
                        .anyMatch(property -> property.getType().equals(oldClass.getName())))
                    .forEach(modelClass -> modelClass.getPropertiesAsList().stream()
                        .filter(property -> property.getType().equals(oldClass.getName()))
                        .forEach(property ->
                            XmlModelClass.getEmbeddedList(property).getEmbeddedPropertyList()
                                .forEach(xmlEmbeddedProperty ->
                                    unusedColumns.add(new XmlUnusedColumn(xmlEmbeddedProperty.getColumnName(), modelClass.getTableName(), newModelVersion))
                                )
                        )
                    );
            } else {
                if (Objects.nonNull(oldClass.getTableName())) {
                    if (oldClass.getStrategy() == ClassStrategy.SINGLE_TABLE) {
                        oldClass.getPropertiesAsList().stream()
                            .filter(XmlModelClassProperty::isUserProperty)
                            .forEach(property -> handleDeletedProperty(baseModel, oldClass, property, unusedTables, unusedColumns, newModelVersion));
                    } else {
                        addPrimitiveCollectionToUnused(unusedTables, oldClass, newModelVersion);
                        addEntityTableToUnused(unusedTables, oldClass, newModelVersion);
                    }
                }
            }
        }
    }

    private void handleDeleteProperties(
        XmlModel baseModel,
        PluginParameters pluginParameters,
        String newModelVersion,
        int newMajorModelVersion,
        XmlUnusedSchemaItems unusedSchemaItems,
        XmlModelClass oldClass
    ) {
        XmlModelClass newModelClass = this.model.getClass(oldClass.getName());

        if (Boolean.TRUE.equals(oldClass.isDictionary())) {
            final List<String> deletedProperties = oldClass.getPropertiesAsList().stream()
                .map(XmlModelClassProperty::getName)
                .filter(name -> !newModelClass.containsProperty(name))
                .toList();
            if (!deletedProperties.isEmpty()) {
                throw new DictionaryPropertyDeleteException(oldClass.getName(), deletedProperties);
            }
        }

        oldClass.getPropertiesAsList().stream()
            .filter(property -> property.isUserProperty() || property.isExternalLink())
            .filter(oldProperty -> !newModelClass.containsProperty(oldProperty.getName()))
            .filter(oldProperty -> Boolean.TRUE.equals(oldProperty.isDeprecated()))
            .filter(oldProperty -> Objects.nonNull(oldProperty.getVersionDeprecated()))
            .filter(oldProperty -> {
                int deprecatedPropertyMajorVersion = getDeprecateMajorVersion(oldProperty.getVersionDeprecated());
                return isVersionAllowsDeleteItems(newMajorModelVersion, deprecatedPropertyMajorVersion, pluginParameters);
            })
            .forEach(oldProperty -> handleDeletedProperty(
                baseModel,
                oldClass,
                oldProperty,
                unusedSchemaItems.getUnusedTables(),
                unusedSchemaItems.getUnusedColumns(),
                newModelVersion
            ));
    }

    private void addPrimitiveCollectionToUnused(List<XmlUnusedTable> unusedTables, XmlModelClass modelClass, String modelVersion) {
        modelClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isUserProperty)
            .filter(property -> Objects.nonNull(property.getCollectionTableName()))
            .forEach(property -> unusedTables.add(new XmlUnusedTable(property.getCollectionTableName(), modelVersion)));
    }

    private void addEntityTableToUnused(List<XmlUnusedTable> unusedTables, XmlModelClass modelClass, String modelVersion) {
        unusedTables.add(new XmlUnusedTable(modelClass.getTableName(), modelVersion));
    }

    private void handleDeletedProperty(XmlModel baseModel,
                                       XmlModelClass oldClass,
                                       XmlModelClassProperty oldProperty,
                                       List<XmlUnusedTable> unusedTables,
                                       List<XmlUnusedColumn> unusedColumns,
                                       String newModelVersion) {
        if (Objects.nonNull(oldProperty.getCollectionType())) {
            if (Objects.nonNull(oldProperty.getCollectionTableName())) {
                unusedTables.add(new XmlUnusedTable(oldProperty.getCollectionTableName(), newModelVersion));
            }
        } else {
            if (Objects.isNull(oldProperty.getMappedBy())) {
                if (oldClass.isEmbeddable()) {

                    baseModel.getClassesAsList().stream()
                        .filter(modelClass -> !modelClass.isAbstract())
                        .filter(modelClass -> modelClass.getPropertiesAsList().stream()
                            .anyMatch(property -> property.getType().equals(oldClass.getName())))
                        .forEach(modelClass -> modelClass.getPropertiesAsList().stream()
                            .filter(property -> property.getType().equals(oldClass.getName()))
                            .forEach(property -> {

                                    String columnName = modelClass.getEmbeddedPropertyList().stream()
                                        .filter(embeddedList -> Objects.equals(embeddedList.getName(), property.getName()))
                                        .findFirst()
                                        .orElseThrow(() -> new EmbeddedListNotFoundException(
                                            property.getName(),
                                            modelClass.getName())
                                        )
                                        .getEmbeddedPropertyList().stream()
                                        .filter(embeddedProperty -> Objects.equals(embeddedProperty.getName(), oldProperty.getName()))
                                        .findFirst()
                                        .orElseThrow(() -> new NoFoundEmbeddedPropertyException(
                                            oldProperty.getName(),
                                            modelClass)
                                        )
                                        .getColumnName();

                                    unusedColumns.add(new XmlUnusedColumn(columnName, modelClass.getTableName(), newModelVersion));
                                }
                            )
                        );
                } else {
                    if (oldProperty.isEmbedded()) {
                        if (oldClass.isAbstract()) {
                            List<XmlModelClass> notAbstractChilds = findClosestNotAbstractChilds(oldClass);
                            notAbstractChilds.forEach(modelClass ->
                                addEmbeddedPropertiesToUnusedColumns(oldClass, oldProperty, modelClass.getTableName(), unusedColumns, newModelVersion)
                            );
                        } else {
                            addEmbeddedPropertiesToUnusedColumns(oldClass, oldProperty, oldClass.getTableName(), unusedColumns, newModelVersion);
                        }
                    } else {
                        if (oldClass.isAbstract()) {
                            List<XmlModelClass> notAbstractChilds = findClosestNotAbstractChilds(oldClass);
                            if (Boolean.TRUE.equals(oldProperty.isExternalLink())) {
                                notAbstractChilds.forEach(xmlModelClass ->
                                    addEmbeddedPropertiesToUnusedColumns(oldClass, oldProperty, xmlModelClass.getTableName(), unusedColumns, newModelVersion));
                            } else {
                                if (Objects.nonNull(oldProperty.getColumnName())) {
                                    notAbstractChilds.forEach(xmlModelClass ->
                                        unusedColumns.add(new XmlUnusedColumn(oldProperty.getColumnName(),
                                            xmlModelClass.getTableName(),
                                            newModelVersion)));
                                }
                            }

                        } else {
                            if (Boolean.TRUE.equals(oldProperty.isExternalLink())) {
                                addEmbeddedPropertiesToUnusedColumns(oldClass, oldProperty, oldClass.getTableName(), unusedColumns, newModelVersion);
                            } else {
                                if (Objects.nonNull(oldProperty.getColumnName())) {
                                    unusedColumns.add(new XmlUnusedColumn(oldProperty.getColumnName(),
                                        oldProperty.getModelClass().getTableName(),
                                        newModelVersion));
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void addEmbeddedPropertiesToUnusedColumns(XmlModelClass modelClass, XmlModelClassProperty property, String tableName, List<XmlUnusedColumn> unusedColumns, String newModelVersion) {
        modelClass.getEmbeddedPropertyList().stream()
            .filter(embeddedList -> Objects.equals(embeddedList.getName(), property.getName()))
            .findFirst()
            .orElseThrow(() -> new EmbeddedListNotFoundException(
                property.getName(),
                modelClass.getName())
            )
            .getEmbeddedPropertyList()
            .forEach(embeddedProperty -> unusedColumns.add(new XmlUnusedColumn(embeddedProperty.getColumnName(), tableName, newModelVersion)));
    }

    private boolean isVersionAllowsDeleteItems(int newMajorModelVersion, int deprecatedClassMajorVersion, PluginParameters pluginParameters) {
        if (pluginParameters.isAllowDeleteNonDeprecatedItems()) {
            return newMajorModelVersion >= deprecatedClassMajorVersion;
        } else {
            return newMajorModelVersion > deprecatedClassMajorVersion;
        }
    }

    private void unusedSchemaItemsDiffHandler(XmlModel newModel, XmlModel baseModel) {
        XmlUnusedSchemaItems baseModelUnusedSchemaItems = baseModel.getUnusedSchemaItems();
        XmlUnusedSchemaItems newModelUnusedSchemaItems = newModel.getUnusedSchemaItems();

        if (Objects.isNull(newModelUnusedSchemaItems)) {
            return;
        }

        if (Objects.nonNull(baseModelUnusedSchemaItems)) {
            List<XmlUnusedTable> baseUnusedTables = baseModelUnusedSchemaItems.getUnusedTables();
            List<XmlUnusedColumn> baseUnusedColumns = baseModelUnusedSchemaItems.getUnusedColumns();

            newModelUnusedSchemaItems.getUnusedTables().stream()
                .filter(unusedTable -> baseUnusedTables.stream()
                    .map(XmlUnusedTable::getName)
                    .noneMatch(nameBaseUnusedTable -> unusedTable.getName().equals(nameBaseUnusedTable)))
                .forEach(baseUnusedTables::add);

            newModelUnusedSchemaItems.getUnusedColumns().stream()
                .filter(unusedColumn -> baseUnusedColumns.stream()
                    .noneMatch(baseUnusedColumn ->
                        unusedColumn.getName().equals(baseUnusedColumn.getName()) &&
                            unusedColumn.getTableName().equals(baseUnusedColumn.getTableName())))
                .forEach(baseUnusedColumns::add);
        } else {
            XmlUnusedSchemaItems newBaseModelUnusedSchemaItems = new XmlUnusedSchemaItems(baseModel);
            newModelUnusedSchemaItems.getUnusedTables()
                .forEach(unusedTable -> newBaseModelUnusedSchemaItems.getUnusedTables().add(unusedTable));
            newModelUnusedSchemaItems.getUnusedColumns()
                .forEach(unusedColumn -> newBaseModelUnusedSchemaItems.getUnusedColumns().add(unusedColumn));
        }

    }

    // In the last model, we do a set because there may be a difference with null and empty value.
    private void handleTablePrefix(XmlModel lastModelVersion, XmlModel newModel) {
        lastModelVersion.setTablePrefix(newModel.getTablePrefix());
    }

    private void handleAutoIdMethod(XmlModel lastModelVersion, XmlModel newModel) {
        lastModelVersion.setAutoIdMethod(newModel.getAutoIdMethod());
    }

    /**
     * If the field or class is marked as deprecated and the deprecatedVersion field is empty, then we set the value in this field.
     * If the field or class is not marked as deprecated, then we nullify the deprecatedVersion
     */
    private void setVersionDeprecated(XmlModel previousModel, ModelParameters modelParameters) {
        String newModelVersion = getModelVersion(modelParameters, model);
        boolean needSetDeprecatedVersion = !isSnapshotVersion(newModelVersion);
        previousModel.getClassesAsList().forEach(xmlModelClass -> {
            // handle deprecated field on classes
            if (needSetDeprecatedVersion
                && xmlModelClass.isDeprecated()
                && !xmlModelClass.isRemoved()
                && Strings.isNullOrEmpty(xmlModelClass.getVersionDeprecated())) {
                xmlModelClass.setVersionDeprecated(newModelVersion);
            }

            // If the class is not deleted, then we clean up VersionDeprecated
            if (!xmlModelClass.isDeprecated()) {
                xmlModelClass.setVersionDeprecated(null);
            }

            // handle the deprecated field on the fields
            xmlModelClass.getPropertiesAsList()
                .forEach(property -> {
                    if (needSetDeprecatedVersion
                        && property.isDeprecated()
                        && !property.isRemoved()
                        && Strings.isNullOrEmpty(property.getVersionDeprecated())) {
                        property.setVersionDeprecated(newModelVersion);
                    }

                    // If the property is not deleted, then we clean up VersionDeprecated
                    if (!property.isDeprecated()) {
                        property.setVersionDeprecated(null);
                    }

                });
        });

        previousModel.getQueriesAsList().forEach(xmlQuery -> {
            // handle deprecated field on classes
            if (needSetDeprecatedVersion
                && xmlQuery.isDeprecated()
                && !xmlQuery.isRemoved()
                && Strings.isNullOrEmpty(xmlQuery.getVersionDeprecated())) {
                xmlQuery.setVersionDeprecated(newModelVersion);
            }

            // If the class has not been deleted, then we clean up VersionDeprecated
            if (!xmlQuery.isDeprecated()) {
                xmlQuery.setVersionDeprecated(null);
            }
        });
    }

    private void deletedClassSetAttrs(XmlModelClass xmlModelClass, String newModelVersion) {
        xmlModelClass.addChangedProperty(XmlModelClass.REMOVED_TAG, xmlModelClass.isRemoved());
        xmlModelClass.setRemoved(Boolean.TRUE);
        xmlModelClass.setVersionRemoved(newModelVersion);
    }

    private void deleteClassProps(XmlModelClass xmlModelClass, ModelParameters modelParameters) {
        xmlModelClass.getPropertiesAsList().forEach(it ->
            modelParameters.addDiffObject(ElementState.REMOVED, it));
    }

    @Deprecated
    private void setMarkIsRemovedToSingleTableClass(XmlModelClass xmlModelClass, ModelParameters modelParameters, String newModelVersion) {
        // no check for singleTable, as the check should be done earlier
        if (xmlModelClass.getVersionRemoved() != null) {
            return;
        }

        deletedClassSetAttrs(xmlModelClass, newModelVersion);
        modelParameters.addBackwardCompatibilityViolationMessage(getMessageRemoveClass(xmlModelClass));
    }

    private void setMarkIsRemovedToXmlClass(XmlModelClass xmlModelClass, ModelParameters modelParameters, String newModelVersion) {
        // Was the class already processed?
        if (xmlModelClass.getVersionRemoved() != null) {
            return;
        }

        if (xmlModelClass.getStrategy() != ClassStrategy.SINGLE_TABLE) {
            deletedClassSetAttrs(xmlModelClass, newModelVersion);
            modelParameters.addBackwardCompatibilityViolationMessage(getMessageRemoveClass(xmlModelClass));
        } else {
            // need to delete classes from farthest to root
            ModelHelper.getChildClasses(xmlModelClass).forEach(childClass ->
                setMarkIsRemovedToXmlClass(childClass, modelParameters, newModelVersion)
            );
            setMarkIsRemovedToSingleTableClass(xmlModelClass, modelParameters, newModelVersion);
        }
    }

    private void setMarkIsRemovedToXmlQuery(XmlQuery xmlQuery, String newModelVersion) {
        if (xmlQuery.getVersionRemoved() != null) {
            return;
        }

        xmlQuery.addChangedProperty(XmlModelClass.REMOVED_TAG, xmlQuery.isRemoved());
        xmlQuery.setRemoved(Boolean.TRUE);
        xmlQuery.setVersionRemoved(newModelVersion);
    }

    /**
     * Here we prepare model elements for deletion: set them to isRemoved and versionRemoved
     *
     * @param previousModel
     * @param modelParameters
     * @param disableCompatibilityCheck
     * @param pluginParameters
     */
    private void checkReadyDeprecatedElementsForRemove(XmlModel newModel, XmlModel previousModel, ModelParameters modelParameters, boolean disableCompatibilityCheck, PluginParameters pluginParameters) {
        String newModelVersion = getModelVersion(modelParameters, model);
        if (isSnapshotVersion(newModelVersion)) {
            return;
        }

        boolean isAllowDeleteNonDeprecatedItems = pluginParameters.isAllowDeleteNonDeprecatedItems();
        BiPredicate<String, String> predicateDeprecatedVersion;

        if (disableCompatibilityCheck || isAllowDeleteNonDeprecatedItems) {
            predicateDeprecatedVersion = (currentVersion, elementVersion) -> {
                int newMajorModelVersion = Semver.of(currentVersion).getMajorVersion();
                int deprecatedClassMajorVersion = getDeprecateMajorVersion(elementVersion);
                /*
                    For information:
                    - previously it was a condition that currentVersion == elementVersion,
                    what could lead to a situation where the consumer sets the isAllowDeleteNonDeprecatedItems flag
                    and will try to delete elements in the usual way (going through all 3 stages of the three-stage deletion).
                    these elements will not be permanently deleted (apparently until the flag isAllowDeleteNonDeprecatedItems is removed, I haven't checked).
                    Moreover, in some situations, a build error is even possible.
                 */
                return newMajorModelVersion >= deprecatedClassMajorVersion;
            };
        } else {
            predicateDeprecatedVersion = (currentVersion, elementVersion) -> {
                int newMajorModelVersion = Semver.of(currentVersion).getMajorVersion();
                int deprecatedClassMajorVersion = getDeprecateMajorVersion(elementVersion);
                return newMajorModelVersion > deprecatedClassMajorVersion;
            };
        }

        previousModel.getClassesAsList().forEach(xmlModelClass -> {
            if (xmlModelClass.isDeprecated() && !xmlModelClass.isRemoved() && isClassWasRemovedFromModelXml(newModel, xmlModelClass, pluginParameters.isDeprecateDeletedItems())) {
                String classVersionDeprecated = xmlModelClass.getVersionDeprecated();
                if (predicateDeprecatedVersion.test(newModelVersion, classVersionDeprecated)
                    // TODO remove this condition, it has already been verified!xmlModelClass.isRemoved()
                    && Strings.isNullOrEmpty(xmlModelClass.getVersionRemoved())) {
                    setMarkIsRemovedToXmlClass(xmlModelClass, modelParameters, newModelVersion);
                }
            }

            xmlModelClass.getPropertiesAsList().stream()
                .filter(property -> !xmlModelClass.isRemoved() || Objects.nonNull(property.getComputedExpression()) || (Objects.nonNull(property.getCollectionType()) && property.getCategory() == PropertyType.PRIMITIVE))
                .filter(property -> property.isDeprecated() && !property.isRemoved() && isPropertyWasRemovedFromModelXml(newModel, xmlModelClass, property, pluginParameters.isDeprecateDeletedItems()))
                .forEach(property -> {
                    String propertyVersionDeprecated = property.getVersionDeprecated();

                    // TODO remove the condition Strings.isNullOrEmpty(property.getVersionRemoved()), it has already been checked !property.isRemoved()
                    if (predicateDeprecatedVersion.test(newModelVersion, propertyVersionDeprecated) && Strings.isNullOrEmpty(property.getVersionRemoved())) {
                        property.addChangedProperty(XmlModelClassProperty.REMOVED_TAG, property.isRemoved());
                        property.setRemoved(Boolean.TRUE);
                        property.setVersionRemoved(newModelVersion);
                        modelParameters.addBackwardCompatibilityViolationMessage(getMessageRemoveProperty(property));
                    }
                });
        });

        previousModel.getQueriesAsList().forEach(xmlQuery -> {
            if (xmlQuery.isDeprecated() && !xmlQuery.isRemoved()) {
                String classVersionDeprecated = xmlQuery.getVersionDeprecated();
                if (predicateDeprecatedVersion.test(newModelVersion, classVersionDeprecated)
                    && Strings.isNullOrEmpty(xmlQuery.getVersionRemoved())) {
                    setMarkIsRemovedToXmlQuery(xmlQuery, newModelVersion);
                }
            }
        });
    }

    private boolean isClassWasRemovedFromModelXml(XmlModel newModel, XmlModelClass oldModelClass, boolean isDeprecateDeletedItems) {
        if (!isDeprecateDeletedItems) {
            return !newModel.containsClass(oldModelClass.getName());
        }
        return true;
    }

    private boolean isPropertyWasRemovedFromModelXml(XmlModel newModel, XmlModelClass oldModelClass, XmlModelClassProperty oldProperty, boolean isDeprecateDeletedItems) {
        if (!isDeprecateDeletedItems) {
            XmlModelClass newModelClass = newModel.getClassNullable(oldModelClass.getName());
            return Objects.nonNull(newModelClass) && !newModelClass.containsProperty(oldProperty.getName());
        }
        return true;
    }

    private void deleteSingleTableClass(XmlModelClass xmlModelClass, ModelParameters modelParameters) {
        if (!xmlModelClass.isBaseClassMark()) {
            deleteClassProps(xmlModelClass, modelParameters);
        } else {
            modelParameters.addDiffObject(ElementState.REMOVED, xmlModelClass);
        }

        // If the predecessor of this class is abstract,then it is necessary to check whether there are any unremoved descendants.
        // if there are no descendants, then you need to delete the columns of the abstract class
        // if there are undeleted descendants, then deleting is not allowed, because these columns are used in them
        while (xmlModelClass.getExtendedClass() != null && xmlModelClass.getExtendedClass().isAbstract()) {
            XmlModelClass abstractParent = xmlModelClass.getExtendedClass();
            boolean nonDeletedAbstractClassChildExists = ModelHelper.getAllChildClasses(abstractParent).stream()
                .anyMatch(it -> !it.isAbstract() && (it.getVersionRemoved() == null || it.getVersionRemoved().isEmpty()));
            // If abstract classes ' unremoved classes are missing, then we delete the abstract class fields
            if (!nonDeletedAbstractClassChildExists) {
                deleteClassProps(abstractParent, modelParameters);
            }
            xmlModelClass = abstractParent;
        }
    }

    private void deleteXmlClass(XmlModelClass xmlModelClass, ModelParameters modelParameters) {
        if (xmlModelClass.getStrategy() != ClassStrategy.SINGLE_TABLE) {
            modelParameters.addDiffObject(ElementState.REMOVED, xmlModelClass);
        } else {
            // need to delete classes from farthest to root
            ModelHelper.getChildClasses(xmlModelClass).forEach(childClass ->
                deleteXmlClass(childClass, modelParameters)
            );
            deleteSingleTableClass(xmlModelClass, modelParameters);
        }
    }

    /**
     * Here the elements that are marked as isRemoved=true, we prepare for scripting deletion.
     *
     * @param xmlModel
     * @param modelParameters
     */
    private void addMarkedElementsForDeletion(XmlModel xmlModel,
                                              ModelParameters modelParameters) {
        xmlModel.getClassesAsList().forEach(xmlModelClass ->
            xmlModelClass.getPropertiesAsList().stream()
                .filter(XmlModelClassProperty::isRemoved)
                .forEach(property -> {
                    if (property.getMappedBy() == null) {
                        modelParameters.addDiffObject(ElementState.REMOVED, property);
                    }
                })
        );

        xmlModel.getClassesAsList().stream()
            .filter(XmlModelClass::isRemoved)
            .forEach(xmlModelClass -> deleteXmlClass(xmlModelClass, modelParameters));

        xmlModel.getQueriesAsList().stream()
            .filter(XmlQuery::isRemoved)
            .forEach(xmlQuery -> modelParameters.addDiffObject(ElementState.REMOVED, xmlQuery));
    }

    /**
     * Copies the flag versionedEntities
     * Checks the import abbreviation
     * Overwrites interfaces from newModel
     * Overwrites external-types from newModel
     */
    private void swapData(XmlModel newModel, XmlModel baseModel) {
        baseModel.setVersionedEntities(newModel.isVersionedEntities());

        Set<XmlImport> difference = Sets.difference(
                baseModel.getImports(),
                newModel.getImports())
            .stream()
            .collect(Collectors.toSet());

        if (!difference.isEmpty() && isSupportableImports(difference)) {
            throw new LessImportDefinedException(difference);
        }

        baseModel.getImports().addAll(newModel.getImports());
        // rewriting interfaces (did not investigate why)
        baseModel.setInterfaces(newModel.getInterfacesAsList());

        // external-types is the configuration that can be freely overwritten
        baseModel.setWithClearExternalTypes(newModel.getExternalTypes());
    }

    private boolean isSupportableImports(Set<XmlImport> imports) {
        Set<String> supportedImports =
            modelGenerates.stream().map(ModelGenerate::getProjectName).collect(Collectors.toSet());
        Set<XmlImport> notSupportedImports = imports.stream()
            .filter(xmlImport -> !supportedImports.contains(xmlImport.getType()))
            .collect(Collectors.toSet());

        return notSupportedImports.isEmpty();
    }

    private void initEmbeddedInPreviousModel(XmlModel baseModel) {
        baseModel.getClassesAsList()
            .forEach(prevModelClass -> {
                // We iterate through the list of meta information fields that can be embedded.
                prevModelClass.getEmbeddedPropertyList().forEach(xmlEmbeddedListItem -> {
                    XmlModelClassProperty modelClassProperty = prevModelClass.getPropertyWithHierarchyInSingleTable(xmlEmbeddedListItem.getName());

                    String type = modelClassProperty.getType();
                    XmlModelClass propertyType = baseModel.getClass(type);

                    //XmlModelClass propertyType = baseModel.getClass(modelClassProperty.getType());

                    // If the field is a reference to a class with a composite key,then the type of the field in pdm is the type of the class.
                    // но на физике это embeddable объект, представляющий ключ класса, поля которого лежат в таблице, которая ссылается
                    // Therefore, we replace the class type with the embeddable object key type.
                    XmlModelClass classKeyId = baseModel.getClassNullable(propertyType.getId().getType());
                    XmlModelClass embeddableClass = classKeyId != null ? classKeyId : propertyType;

                    // Bypassing meta-information about embeddable fields and placing a link to the fields of the embeddable class
                    xmlEmbeddedListItem.getEmbeddedPropertyList().forEach(xmlEmbeddedProperty -> {
                        XmlModelClassProperty propertyName = null;
                        XmlModelClass countingEmbeddableClass = embeddableClass;
                        // descending to the innermost embedded property of the most nested embeddable
                        // P.S. embedded prohibited earlier (but left the code)
                        for (String prop : xmlEmbeddedProperty.getName().split("\\.")) {
                            propertyName = countingEmbeddableClass.getPropertyWithHierarchyInSingleTable(prop);
                            countingEmbeddableClass = baseModel.getClassNullable(propertyName.getType());
                        }
                        xmlEmbeddedProperty.setProperty(propertyName);
                    });
                });
            });
    }


    //The same thing as in BaseEntityGenerator - why
    private void addIncomeObjectIdPropertiesForInheritors(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(XmlModelClass::isBaseClassMark)
            .forEach(modelClass -> {
                Set<XmlModelClass> allInheritedClasses = ModelHelper.getAllChildClasses(modelClass);
                allInheritedClasses.forEach(modelClass1 ->
                    modelClass1.addIncomePropertyWithoutCheck(
                        modelClass.getPropertyWithHierarchyInSingleTable(JpaConstants.OBJECT_ID)
                    ));
            });
    }

    private void setIncomeProperties(XmlModel model) {
        XmlModelClass baseClass = model.getClass(JpaConstants.ENTITY_CLASS_NAME);
        recursiveAdditionalPropertiesInit(model, baseClass);

        setIncomeToDictionaryClasses(model);
        setIncomeToEventClasses(model);
    }

    private void setIncomeToDictionaryClasses(XmlModel model) {
        List<XmlModelClass> dictionarySuperClasses = model.getClassesAsList().stream()
            .filter(it -> it.isDictionary() && it.getExtendedClassName() == null)
            .toList();
        dictionarySuperClasses.forEach(it -> recursiveAdditionalPropertiesInit(model, it));
    }

    private void setIncomeToEventClasses(XmlModel model) {
        List<XmlModelClass> eventSuperClasses = model.getClassesAsList().stream()
            .filter(it -> it.isEvent() && it.getExtendedClassName() == null)
            .toList();
        eventSuperClasses.forEach(it -> recursiveAdditionalPropertiesInit(model, it));
    }

    private void recursiveAdditionalPropertiesInit(XmlModel model, XmlModelClass xmlModelClass) {
        List<XmlModelClass> childClasses = model.getClassesAsList().stream()
            .filter(modelClass -> xmlModelClass.getName().equals(modelClass.getExtendedClassName()))
            .toList();

        if (xmlModelClass.isAbstract()) {
            childClasses.forEach(modelClass -> {
                modelClass.clearIncomeProperties();
                xmlModelClass.getPropertiesAsList().forEach(modelClass::addIncomePropertyWithoutCheck);
                xmlModelClass.getIncomePropertiesAsList().forEach(modelClass::addIncomePropertyWithoutCheck);
            });
        }

        childClasses.forEach(modelClass -> recursiveAdditionalPropertiesInit(model, modelClass));
    }

    // TODO rewrite by avoiding reprocessing of the same classes within inheritance
    //   does not check property names in different inheritance chains SingleTable
    private static void checkEqualPropertyName(ParameterContext parameterContext) {
        parameterContext.getModelParameters().getModel().getClassesAsList().forEach(modelClass -> {
            List<XmlModelClass> superClasses = ModelHelper.getAllSuperClasses(modelClass, true);

            List<Pair<String, XmlModelClassProperty>> allSuperProperties = superClasses.stream()
                .flatMap(superCLass -> superCLass.getPropertiesAsList().stream())
                .map(property -> new Pair<>(property.getName().toLowerCase(Locale.ENGLISH), property))
                .toList();

            for (int i = 0; i < allSuperProperties.size(); ++i) {
                Pair<String, XmlModelClassProperty> curProperty = allSuperProperties.get(i);
                for (int j = i + 1; j < allSuperProperties.size(); ++j) {
                    Pair<String, XmlModelClassProperty> nextProperty = allSuperProperties.get(j);

                    if (Objects.equals(curProperty.getFirst(), nextProperty.getFirst())) {
                        XmlModelClassProperty firstProperty = curProperty.getSecond();
                        XmlModelClassProperty secondProperty = nextProperty.getSecond();

                        if (!parameterContext.getPluginParameters().isDisableCompatibilityCheck()) {
                            if (firstProperty.isDeprecated() || secondProperty.isDeprecated()) {
                                XmlModelClassProperty deprecated = firstProperty.isDeprecated() ? firstProperty : secondProperty;
                                XmlModelClassProperty notDeprecated = firstProperty.isDeprecated() ? secondProperty : firstProperty;

                                throw new NewPropertyAsDeprecatedException(deprecated, notDeprecated);
                            } else {
                                throw new ParentPropertyNameAsChildException(firstProperty, secondProperty);
                            }
                        }

                    }
                }
            }
        });
    }

    private static void checkDuplicatePhysicNames(ParameterContext parameterContext) {
        // If the check is turned off, then we do nothing
        if (parameterContext.getPluginParameters().isDisablePhysicNamesCheck()) {
            return;
        }
        checkDuplicateTableNames(parameterContext);
        checkDuplicateColumnNames(parameterContext);
    }

    /**
     * Checks that there are no duplicate table names on the model
     */
    private static void checkDuplicateTableNames(ParameterContext parameterContext) {
        Map<String, List<Object>> tableNameToModelObj = new HashMap<>();
        parameterContext.getModelParameters().getModel().getClassesAsList()
            .forEach(modelClass -> {
                // Skipping classes that will be physically removed
                if (parameterContext.getPluginParameters().isIntermediaryBuild() && modelClass.isRemoved()) {
                    return;
                }

                if (modelClass.getTableName() != null) {
                    // For SingleTable tables, it makes sense to check only for the base class,since all descendants
                    // have the same table name
                    if (modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE && !modelClass.isBaseClassMark()) {
                        return;
                    }
                    CheckerUtils.addValueToListMap(tableNameToModelObj, modelClass.getTableName().toLowerCase(), modelClass);
                }
                modelClass.getPropertiesAsList().stream()
                    .filter(prop -> prop.getCollectionTableName() != null)
                    // Skip the fields that will be deleted
                            .filter(prop -> !parameterContext.getPluginParameters().isIntermediaryBuild() || !prop.isRemoved())
                    .forEach(prop -> CheckerUtils.addValueToListMap(
                        tableNameToModelObj,
                        prop.getCollectionTableName().toLowerCase(),
                        prop)
                    );
            });

        Map<String, List<Object>> objectWithDuplicatePhisicNames = tableNameToModelObj.entrySet().stream()
            .filter(it -> it.getValue().size() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (objectWithDuplicatePhisicNames.size() > 0) {
            throw DuplicateException.duplicatePhisicNameException(objectWithDuplicatePhisicNames);
        }
    }

    /**
     * Checks that there are no duplicate field names on the model
     */
    private static void checkDuplicateColumnNames(ParameterContext parameterContext) {
        Set<XmlModelClass> processedClasses = new HashSet<>();
        XmlModelClass baseEntityClass = parameterContext
            .getModelParameters()
            .getModel()
            .getClass(JpaConstants.ENTITY_CLASS_NAME);
        checkDuplicateColumnNames(baseEntityClass, new HashMap<>(), parameterContext, processedClasses);
        // It is necessary to process classes that are not descendants of the base entity.
        parameterContext.getModelParameters().getModel().getClassesAsList()
            .forEach(modelClass -> {
                if (processedClasses.contains(modelClass)) {
                    return;
                }
                XmlModelClass baseClass = ModelHelper.getBaseClassNotNullable(modelClass);
                checkDuplicateColumnNames(baseClass, new HashMap<>(), parameterContext, processedClasses);
            });
    }

    /**
     * Auxiliary method to check for duplicate physical column names on the class and its subclasses
     */
    private static void checkDuplicateColumnNames(XmlModelClass modelClass,
                                                  Map<String, List<XmlModelClassProperty>> columnMap,
                                                  ParameterContext parameterContext,
                                                  Set<XmlModelClass> processedClasses) {
        processedClasses.add(modelClass);
        // If the strategy of the previous class is not SingleTable, then we should not modify the original map
        XmlModelClass extendedClass = modelClass.getExtendedClass();
        Map<String, List<XmlModelClassProperty>> clonedColumnMap
            = extendedClass != null && extendedClass.getStrategy() != ClassStrategy.SINGLE_TABLE
            ? CheckerUtils.cloneListMap(columnMap)
            : columnMap;

        // Skipping classes that will be physically removed
        if (parameterContext.getPluginParameters().isIntermediaryBuild() && modelClass.isRemoved()) {
            return;
        }

        modelClass.getPropertiesAsList().stream()
            // In basic classes, we throw away the id and type fields because they are duplicated with BaseEntity.
            .filter(prop -> !prop.getModelClass().isBaseClassMark() || !prop.isId() && !prop.getName().equals(JPA_DISCRIMINATOR_NAME))
            // Создает директорию по указанному File*
                .filter(prop -> !parameterContext.getPluginParameters().isIntermediaryBuild() || !prop.isRemoved())
            .flatMap(prop -> !prop.isEmbedded() ? Stream.of(prop) : ModelHelper.propertyToPropertyEmbeddable(prop).stream())
            .filter(prop -> prop.getColumnName() != null)
            .forEach(prop -> CheckerUtils.addValueToListMap(clonedColumnMap, prop.getColumnName(), prop));

        Map<String, List<XmlModelClassProperty>> errorColumnMap = clonedColumnMap.entrySet().stream()
            .filter(it -> it.getValue().size() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!errorColumnMap.isEmpty()) {
            throw DuplicateException.duplicatePhisicNameException(errorColumnMap);
        }

        // If there are no errors, then we check all the descendants
        Set<XmlModelClass> childClasses = ModelHelper.getChildClasses(modelClass);
        childClasses.forEach(childClass -> checkDuplicateColumnNames(childClass, clonedColumnMap, parameterContext, processedClasses));
    }

    private void checkIntegrityCheckReferenceTag(XmlModelClass xmlModelClass) {
        xmlModelClass
            .getReferencesAsList()
            .stream()
            .filter(it -> it.isIntegrityCheck() == Boolean.TRUE)
            .filter(reference -> xmlModelClass.getModel().findClass(reference.getType()).isEmpty())
            .findFirst()
            .ifPresent(reference -> {
                throw new ReferenceIntegrityCheckRuleException(reference);
            });
    }


    private void checkCloneable() {

        final List<XmlModelClass> wrongClasses =
            model
                .getClassesAsList()
                .stream()
                .filter(UserXmlModelClass::isCloneable)
                .filter(xmlModelClass -> Boolean.TRUE.equals(xmlModelClass.isDictionary())
                    || Boolean.TRUE.equals(xmlModelClass.isAbstract()))
                .sorted(Comparator.comparing(UserXmlModelClass::getName))
                .toList();

        if (!wrongClasses.isEmpty()) {
            throw new CloneablePositionException(wrongClasses);
        }

    }


    private void setPartitionKeyByPartitionKeyRegex(XmlModelClass xmlModelClass) {

        // set a key on the class according to the filling of the regex
        if (xmlModelClass.getPartitionKeyRegexNotNullAsEmpty().isEmpty()) {
            xmlModelClass.setPartitionKey(xmlModelClass.getPartitionKeyRegex());
        } else {
            xmlModelClass.setPartitionKey(JpaConstants.OBJECT_ID);
        }

        final Consumer<String> propagateToClass = type ->
            Optional.ofNullable(xmlModelClass.getModel().getClassNullable(type)).ifPresent(
                aClass -> {
                    aClass.setPartitionKey(xmlModelClass.getPartitionKey());
                    aClass.setPartitionKeyRegex(xmlModelClass.getPartitionKeyRegex());
                }
            );

        final Set<String> systemPropertyHolderSet = Stream.of(
            "apiCalls",
            "historyChanges"
        ).collect(Collectors.toSet());

        // looking at associated classes by properties of the current one without inheritance taken into account
        xmlModelClass.getPropertiesAsList().forEach(property -> {

            if (Boolean.TRUE.equals(property.isExternalLink()) && Objects.nonNull(property.getCollectionType())) {
                propagateToClass.accept(property.getType());
            }

            if (systemPropertyHolderSet.contains(property.getName())) {
                propagateToClass.accept(property.getType());
            }

        });

    }

    /* Restores the names of pk indexes for primitive collection fields, as they were not previously saved in pdm **/
    private void restorePkIndexNameOnCollectionProperties() {
// Here nothing to change - this is for backward compatibility with old pdm
        pdmModel.getModel().getClassesAsList().stream()
            .flatMap(oldClass -> oldClass.getPropertiesAsList().stream())
            .filter(modelClassProperty -> ModelIndexLogic.indexedCollectionPredicate.test(modelClassProperty)
                // This condition is just in case
                &&modelClassProperty.getCollectionTableName() != null
                && modelClassProperty.getCollectionPkIndexName() == null)
                .forEach(modelClassProperty ->
                // The value of 63 should not be changed to maintain backward compatibility.
                // this is the old maxSchemaNameLength (maxDBObjectNameLength)
                modelClassProperty.setCollectionPkIndexName(
                    NameHelper.getName("PK_" + modelClassProperty.getCollectionTableName(), 63)
                )
        );
    }

    public XmlModel getModel() {
        return this.model;
    }
}
