package ru.sbertech.dataspace.model.pdm

import com.sbt.dataspace.pdm.PdmModel
import com.sbt.mg.ModelHelper
import com.sbt.mg.data.model.ClassStrategy
import com.sbt.mg.data.model.XmlModel
import com.sbt.mg.data.model.XmlModelClass
import com.sbt.mg.data.model.XmlModelClassProperty
import com.sbt.mg.jpa.JpaConstants
import com.sbt.parameters.enums.Changeable
import com.sbt.parameters.enums.IdCategory
import ru.sbertech.dataspace.EntityWithStatus
import ru.sbertech.dataspace.StatusModel
import ru.sbertech.dataspace.StatusProperty
import ru.sbertech.dataspace.StatusType
import ru.sbertech.dataspace.data.Status
import ru.sbertech.dataspace.data.StatusGroup
import ru.sbertech.dataspace.data.StatusTransition
import ru.sbertech.dataspace.model.Component
import ru.sbertech.dataspace.model.EnumValue
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.aggregates.Aggregate
import ru.sbertech.dataspace.model.aggregates.AggregatesModel
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.dictionaries.DictionariesModel
import ru.sbertech.dataspace.model.dictionaries.Dictionary
import ru.sbertech.dataspace.model.idstrategy.AutoOnEmptyIdStrategy
import ru.sbertech.dataspace.model.idstrategy.ManualIdStrategy
import ru.sbertech.dataspace.model.idstrategy.StringSnowflakeIdStrategy
import ru.sbertech.dataspace.model.idstrategy.StringUUIDIdStrategy
import ru.sbertech.dataspace.model.property.BasicProperty
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.MappedReferenceProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.system.SystemModel
import ru.sbertech.dataspace.model.system.extension.EntityTypeExtension
import ru.sbertech.dataspace.model.system.extension.Index
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.model.type.EnumType
import ru.sbertech.dataspace.model.type.StructuredType
import ru.sbertech.dataspace.model.type.Type
import java.io.File
import java.io.InputStream

private const val API_CALL_POSTFIX = "ApiCall"
private const val DEFAULT_ID_PROPERTY_NAME = "objectId"
private const val DEFAULT_AGGREGATE_ROOT_PROPERTY_NAME = "aggregateRoot"
private const val ROOT_DICTIONARY = "RootDictionary"
private const val ROOT_SECURITY = "SysRootSecurity"
private const val AGGREGATE_VERSION_PROPERTY_NAME = "sys_ver"
private const val SYS_STATUS_FIELDS = "SysStatusFields"

fun modelBuilder(pdmModel: PdmModel): Model.Builder {
    val xmlModel = pdmModel.model
    return Model.Builder().also { model ->
        model.name = xmlModel.modelName
        model.description = xmlModel.description
        model.types =
            arrayListOf<Type.Builder>().also { types ->
                xmlModel.enums.mapTo(types) { xmlModelClassEnum ->
                    EnumType.Builder().apply {
                        name = xmlModelClassEnum.name
                        description = xmlModelClassEnum.label
                        values =
                            xmlModelClassEnum.enumValues.mapTo(arrayListOf()) { xmlEnumValue ->
                                EnumValue.Builder().apply {
                                    name = xmlEnumValue.name
                                    description = xmlEnumValue.description
                                }
                            }
                    }
                }
                xmlModel.classesAsList
                    .asSequence()
                    .filter { !it.isAbstract && it.isNotExcluded() }
                    .mapTo(types) { xmlModelClass ->
                        if (xmlModelClass.isEmbeddable) {
                            EmbeddableType.Builder().also { embeddableType ->
                                embeddableType.name = xmlModelClass.name
                                embeddableType.description = xmlModelClass.description
                                embeddableType.properties = arrayListOf()
                                addProperties(embeddableType, xmlModel, xmlModelClass)
                            }
                        } else {
                            EntityType.Builder().also { entityType ->
                                entityType.name = xmlModelClass.name
                                entityType.description = xmlModelClass.description
                                entityType.properties = arrayListOf()
                                var parentXmlModelClass = xmlModelClass
                                do {
                                    addProperties(entityType, xmlModel, parentXmlModelClass)
                                    parentXmlModelClass = parentXmlModelClass.extendedClass
                                } while (parentXmlModelClass != null && parentXmlModelClass.isAbstract)
                                when (parentXmlModelClass) {
                                    null -> entityType.inheritanceStrategy = inheritanceStrategy(xmlModelClass.strategy)
                                    else -> entityType.parentTypeName = parentXmlModelClass.name
                                }
                                if (parentXmlModelClass == null || xmlModelClass.strategy == ClassStrategy.JOINED) {
                                    entityType.table = xmlModelClass.tableName
                                }
                            }
                        }
                    }
            }
    }
}

fun modelBuilder(pdmFile: File): Model.Builder = modelBuilder(PdmModel.readModelByPath(pdmFile))

fun modelBuilder(inputStream: InputStream): Model.Builder = modelBuilder(PdmModel.readModelByInputStream(inputStream))

fun aggregatesModelBuilder(pdmModel: PdmModel): AggregatesModel.Builder {
    val xmlModel = pdmModel.model

    val leavesByRootType =
        xmlModel.classesAsList
            .asSequence()
            .filter {
                !it.isAbstract &&
                    it.isNotExcluded() &&
                    !it.isEmbeddable &&
                    it.affinity != DEFAULT_ID_PROPERTY_NAME
            }.groupByTo(
                hashMapOf(),
                keySelector = { it.getAggregateRootType(xmlModel).name },
                valueTransform = { xmlModelClass ->
                    Leaf
                        .Builder()
                        .apply {
                            externalReferences = getExternalReferences(xmlModelClass)
                            name = xmlModelClass.name
                            parentProperty = xmlModelClass.affinity
                            aggregateRootProperty = DEFAULT_AGGREGATE_ROOT_PROPERTY_NAME
                            treeParentProperty =
                                xmlModelClass.propertiesAsList.firstOrNull { it.isParent && it.name != xmlModelClass.affinity }?.name
                        }
                },
            )

    val dictionaries =
        xmlModel.classesAsList
            .asSequence()
            .filter { it.isDictionary() && it.affinity == ROOT_DICTIONARY }
            .filter { xmlModelClass -> !xmlModelClass.isAbstract }
            .mapTo(arrayListOf()) { xmlModelClass ->
                Leaf
                    .Builder()
                    .apply {
                        name = xmlModelClass.name
                        aggregateRootProperty = DEFAULT_AGGREGATE_ROOT_PROPERTY_NAME
                    }
            }

    if (dictionaries.isNotEmpty()) {
        leavesByRootType[ROOT_DICTIONARY] = dictionaries
    }

    return AggregatesModel.Builder().apply {
        externalReferenceTypes =
            xmlModel.classesAsList
                .asSequence()
                .filter { it.isExternalReference }
                .map { it.name }
                .toMutableList()
        aggregates =
            xmlModel.classesAsList
                .asSequence()
                .filter {
                    !it.isAbstract &&
                        !it.isExternalReference &&
                        it.isNotExcluded() &&
                        it.affinity == DEFAULT_ID_PROPERTY_NAME
                }.mapTo(arrayListOf()) {
                    Aggregate.Builder().apply {
                        aggregateVersionPropertyName = AGGREGATE_VERSION_PROPERTY_NAME
                        externalReferences = getExternalReferences(it)
                        name = it.name
                        leaves = leavesByRootType[it.name]
                        idempotenceDataEntityTypeName =
                            if (ROOT_DICTIONARY == it.name || ROOT_SECURITY == it.name) {
                                null
                            } else {
                                ModelHelper.getBaseClass(it).name + API_CALL_POSTFIX
                            }
                    }
                }
    }
}

fun dictionariesModelBuilder(pdmModel: PdmModel): DictionariesModel.Builder =
    DictionariesModel.Builder().apply {
        dictionaries =
            pdmModel.model.classesAsList
                .asSequence()
                .filter { it.isDictionary() }
                .filter { xmlModelClass -> !xmlModelClass.isAbstract }
                .mapTo(arrayListOf()) { xmlModelClass ->
                    Dictionary.Builder().apply {
                        name = xmlModelClass.name
                    }
                }
    }

fun statusModelBuilder(pdmModel: PdmModel): StatusModel.Builder? {
    val entitiesWithStatusBuilders = arrayListOf<EntityWithStatus.Builder>()
    val statusGroupsBuilders = arrayListOf<StatusGroup.Builder>()

    pdmModel.model.classesAsList.forEach { xmlModelClass ->
        val statuses = xmlModelClass.statuses ?: return@forEach

        val statusesBuilders = arrayListOf<Status.Builder>()
        val entityWithStatus =
            EntityWithStatus.Builder().apply {
                entityType = xmlModelClass.name
            }

        val statusPropertiesBuilders = arrayListOf<StatusProperty.Builder>()
        statuses.groups.forEach { xmlGroup ->
            val statusGroupCode = xmlGroup.code
            statusGroupsBuilders.add(
                StatusGroup.Builder().apply {
                    code = statusGroupCode
                },
            )

            xmlGroup.statuses.forEach { xmlStatus ->
                statusesBuilders.add(
                    Status.Builder().apply {
                        code = xmlStatus.code
                        isInitial = xmlStatus.isInitial
                        groupCode = statusGroupCode
                        transitions =
                            xmlStatus.statusTos
                                .map {
                                    StatusTransition.Builder().apply {
                                        statusTo = it.statusTo
                                    }
                                }.toMutableList()
                    },
                )
            }

            statusPropertiesBuilders.add(
                StatusProperty.Builder().apply {
                    propertyName = "statusFor${statusGroupCode.replaceFirstChar { it.uppercase() }}"
                    groupCode = statusGroupCode
                },
            )
        }
        entityWithStatus.statusProperties = statusPropertiesBuilders
        entityWithStatus.statuses = statusesBuilders
        entitiesWithStatusBuilders.add(entityWithStatus)
    }

    if (entitiesWithStatusBuilders.isEmpty()) {
        return null
    }

    return StatusModel
        .Builder()
        .apply {
            statusType =
                StatusType.Builder().apply {
                    type = SYS_STATUS_FIELDS
                    codeProperty = "code"
                    reasonProperty = "reason"
                }
            statusGroups = statusGroupsBuilders
            entitiesWithStatus = entitiesWithStatusBuilders
        }
}

fun systemModelBuilder(pdmModel: PdmModel): SystemModel.Builder {
    val embeddableWithRequiredProperties: MutableMap<String, Set<String>> =
        pdmModel
            .model
            .classesAsList
            .asSequence()
            .filter { xmlModelClass -> xmlModelClass.isEmbeddable }
            .filter { xmlModelClass -> xmlModelClass.classAccess == Changeable.UPDATE }
            .map { xmlModelClass ->
                xmlModelClass.name to
                    xmlModelClass
                        .propertiesAsList
                        .asSequence()
                        .filter { xmlModelClassProperty -> xmlModelClassProperty.isMandatory }
                        .map { xmlModelClassProperty -> xmlModelClassProperty.name }
                        .toSet()
            }.filter { pair -> pair.second.isNotEmpty() }
            .toMap(mutableMapOf())

    val indexesByTypeName: MutableMap<String, MutableList<Index.Builder>> =
        pdmModel.model.classesAsList
            .asSequence()
            .filter { xmlModelClass -> xmlModelClass.indices.isNotEmpty() }
            .map { xmlModelClass ->
                xmlModelClass.name to
                    xmlModelClass.indices
                        .asSequence()
                        // TODO
                        .filter { xmlIndex -> !xmlIndex.properties.any { it.name == "type" } }
                        .map { xmlIndex ->
                            Index.Builder().apply {
                                this.isUnique = xmlIndex.isUnique
                                this.properties = xmlIndex.properties.map { property -> property.name }.toMutableList()
                            }
                        }.toMutableList()
            }.toMap(mutableMapOf())

    val entityTypeExtensions: MutableList<EntityTypeExtension.Builder> =
        pdmModel.model.classesAsList
            .asSequence()
            // TODO системные типы сейчас не заносятся в Model
            .filter { xmlModelClass -> xmlModelClass.isNotExcluded() }
            // TODO
            .filter { xmlModelClass -> !xmlModelClass.isEmbeddable }
            .filter { xmlModelClass -> !xmlModelClass.isAbstract }
            .mapNotNull { xmlModelClass ->
                if (xmlModelClass.classAccess == Changeable.SYSTEM || indexesByTypeName.containsKey(xmlModelClass.name)) {
                    EntityTypeExtension.Builder().apply {
                        this.typeName = xmlModelClass.name
                        this.isSystem = xmlModelClass.classAccess == Changeable.SYSTEM
                        this.indexes = indexesByTypeName[xmlModelClass.name]
                    }
                } else {
                    null
                }
            }.toMutableList()

    return SystemModel.Builder().apply {
        this.entityTypeExtensions = entityTypeExtensions
        this.embeddableWithRequiredProperties = embeddableWithRequiredProperties
    }
}

private fun XmlModelClass.getAggregateRootType(xmlModel: XmlModel): XmlModelClass {
    val parentProperty = this.propertiesAsList.firstOrNull { it.isParent && it.name == this.affinity }
    return if (!this.isAbstract && parentProperty == null) {
        this
    } else {
        xmlModel.getClass(parentProperty!!.type).getAggregateRootType(xmlModel)
    }
}

private fun XmlModelClass.isNotExcluded() = classAccess != Changeable.SYSTEM || name.endsWith(API_CALL_POSTFIX) || name == SYS_STATUS_FIELDS

private fun getExternalReferences(xmlModelClass: XmlModelClass) =
    xmlModelClass.propertiesAsList
        .asSequence()
        .filter { it.isExternalLink || it.isExternalSoftReference }
        .map { property ->
            val isMandatory = xmlModelClass.embeddedPropertyList.firstOrNull { it.name == property.name }?.isMandatory ?: false
            property.name to isMandatory
        }.toMutableList()

private fun Component.Builder.setComponentAttributes(xmlModelClassProperty: XmlModelClassProperty) {
    name = xmlModelClassProperty.name
    description = xmlModelClassProperty.description
}

private fun Property.Builder.setPropertyAttributes(xmlModelClassProperty: XmlModelClassProperty) {
    setComponentAttributes(xmlModelClassProperty)
}

private fun BasicProperty.Builder.setBasicPropertyAttributes(
    xmlModelClassProperty: XmlModelClassProperty,
    structuredType: StructuredType.Builder,
) {
    setPropertyAttributes(xmlModelClassProperty)
    isOptional =
        if (structuredType is EmbeddableType.Builder) {
            true
        } else {
            !xmlModelClassProperty.isMandatory
        }
    if (xmlModelClassProperty.changeable == Changeable.READ_ONLY &&
        xmlModelClassProperty.name != DEFAULT_ID_PROPERTY_NAME &&
        xmlModelClassProperty.name != AGGREGATE_VERSION_PROPERTY_NAME
    ) {
        isSettableOnCreate = false
        isSettableOnUpdate = false
    }
    if (structuredType is EntityType.Builder) column = xmlModelClassProperty.columnName
}

private fun addProperties(
    structuredType: StructuredType.Builder,
    xmlModel: XmlModel,
    xmlModelClass: XmlModelClass,
) {
    val properties = structuredType.properties!!
    xmlModelClass.propertiesAsList.forEach { xmlModelClassProperty ->
        if (xmlModelClassProperty.isId && structuredType is EntityType.Builder) {
            structuredType.idPropertyName = xmlModelClassProperty.name
            structuredType.idStrategy =
                when (xmlModelClass.id.idCategory) {
                    IdCategory.MANUAL -> ManualIdStrategy
                    IdCategory.SNOWFLAKE -> StringSnowflakeIdStrategy
                    IdCategory.AUTO_ON_EMPTY -> AutoOnEmptyIdStrategy(StringSnowflakeIdStrategy)
                    IdCategory.UUIDV4 -> StringUUIDIdStrategy
                    IdCategory.UUIDV4_ON_EMPTY -> AutoOnEmptyIdStrategy(StringUUIDIdStrategy)
                    else -> throw IllegalArgumentException("Unsupported id category")
                }
        }
        if (xmlModelClassProperty.changeable != Changeable.SYSTEM || xmlModelClass.name.endsWith(API_CALL_POSTFIX)) {
            if (xmlModelClassProperty.isEmbedded) {
                properties +=
                    EmbeddedProperty.Builder().apply {
                        setPropertyAttributes(xmlModelClassProperty)
                        typeName = xmlModelClassProperty.type
                        propertyOverrides =
                            xmlModelClass.embeddedPropertyList
                                .find { it.name == xmlModelClassProperty.name }!!
                                .embeddedPropertyList
                                .mapTo(arrayListOf()) {
                                    if (xmlModel.getClass(xmlModelClassProperty.type).getProperty(it.name).isEnum) {
                                        EnumProperty.Override.Builder().apply {
                                            propertyName = it.name
                                            column = it.columnName
                                        }
                                    } else {
                                        PrimitiveProperty.Override.Builder().apply {
                                            propertyName = it.name
                                            column = it.columnName
                                        }
                                    }
                                }
                    }
            } else {
                if (xmlModelClassProperty.isEnum) {
                    when (xmlModelClassProperty.collectionType) {
                        null ->
                            properties +=
                                EnumProperty.Builder().apply {
                                    setBasicPropertyAttributes(xmlModelClassProperty, structuredType)
                                    typeName = xmlModelClassProperty.type
                                }

                        else ->
                            properties +=
                                EnumCollectionProperty.Builder().apply {
                                    setPropertyAttributes(xmlModelClassProperty)
                                    if (structuredType is EntityType.Builder) {
                                        table = xmlModelClassProperty.collectionTableName
                                        ownerIdPropertyOverride =
                                            PrimitiveProperty.Override.Builder().apply {
                                                column = xmlModelClassProperty.keyColumnName
                                            }
                                        elementColumn = xmlModelClassProperty.columnName
                                    }
                                    typeName = xmlModelClassProperty.type
                                }
                    }
                } else {
                    when (val type = primitiveType(xmlModelClassProperty)) {
                        null ->
                            when (xmlModelClassProperty.collectionType) {
                                null ->
                                    when (xmlModelClassProperty.mappedBy) {
                                        null ->
                                            properties +=
                                                ReferenceProperty.Builder().apply {
                                                    setPropertyAttributes(xmlModelClassProperty)
                                                    typeName = xmlModelClassProperty.type
                                                    val isHistoryOwnerField =
                                                        xmlModelClass.isHistoryClass &&
                                                            JpaConstants.HISTORY_OWNER_PROPERTY == xmlModelClassProperty.name
                                                    isOptional = isHistoryOwnerField || !xmlModelClassProperty.isMandatory
                                                    if (xmlModelClassProperty.changeable == Changeable.READ_ONLY &&
                                                        xmlModelClassProperty.name != DEFAULT_AGGREGATE_ROOT_PROPERTY_NAME
                                                    ) {
                                                        isSettableOnCreate = false
                                                        isSettableOnUpdate = false
                                                    }
                                                    if (structuredType is EntityType.Builder) {
                                                        idPropertyOverride =
                                                            PrimitiveProperty.Override.Builder().apply {
                                                                column = xmlModelClassProperty.columnName
                                                            }
                                                    }
                                                }

                                        else ->
                                            properties +=
                                                MappedReferenceProperty.Builder().apply {
                                                    setPropertyAttributes(xmlModelClassProperty)
                                                    typeName = xmlModelClassProperty.type
                                                    mappingPropertyPath = arrayListOf(xmlModelClassProperty.mappedBy)
                                                }
                                    }

                                else ->
                                    properties +=
                                        MappedReferenceCollectionProperty.Builder().apply {
                                            setPropertyAttributes(xmlModelClassProperty)
                                            typeName = xmlModelClassProperty.type
                                            mappingPropertyPath = arrayListOf(xmlModelClassProperty.mappedBy)
                                        }
                            }

                        else ->
                            when (xmlModelClassProperty.collectionType) {
                                null -> {
                                    if (xmlModelClassProperty.name == "type" && structuredType is EntityType.Builder) {
                                        structuredType.discriminatorColumn = xmlModelClassProperty.columnName
                                    } else {
                                        properties +=
                                            PrimitiveProperty.Builder().apply {
                                                setBasicPropertyAttributes(xmlModelClassProperty, structuredType)
                                                this.type = type
                                            }
                                    }
                                }

                                else ->
                                    properties +=
                                        PrimitiveCollectionProperty.Builder().apply {
                                            setPropertyAttributes(xmlModelClassProperty)
                                            if (structuredType is EntityType.Builder) {
                                                table = xmlModelClassProperty.collectionTableName
                                                ownerIdPropertyOverride =
                                                    PrimitiveProperty.Override.Builder().apply {
                                                        column = xmlModelClassProperty.keyColumnName
                                                    }
                                                elementColumn = xmlModelClassProperty.columnName
                                            }
                                            this.type = type
                                        }
                            }
                    }
                }
            }
        }
    }
}
