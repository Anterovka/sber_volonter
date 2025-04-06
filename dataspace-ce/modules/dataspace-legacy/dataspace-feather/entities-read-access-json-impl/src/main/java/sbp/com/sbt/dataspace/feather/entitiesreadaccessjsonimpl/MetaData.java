package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetPrimitiveValueFunction;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetPrimitiveValueFunction2;

/**
 * Metadata
 */
final class MetaData {

    static final Object WRITE_KEY = new Object();

    private MetaData() {
    }

    /**
     * Put
     *
     * @param modelDescription      Model description
     * @param offsetDateTimeZoneId  The time zone identifier with an offset
     */
    static void put(ModelDescription modelDescription, ZoneId offsetDateTimeZoneId) {
        DataType.CHARACTER.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::char"),
                getGetPrimitiveValueFunction(ResultSet::getString),
                Function.identity()));
        DataType.STRING.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::varchar"),
                getGetPrimitiveValueFunction(ResultSet::getString),
                Function.identity()));
        DataType.TEXT.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::text"),
                getGetPrimitiveValueFunction(ResultSet::getString),
                Function.identity()));
        DataType.BYTE.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::int2"),
                getGetPrimitiveValueFunction(ResultSet::getByte),
                Object::toString));
        DataType.SHORT.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::int2"),
                getGetPrimitiveValueFunction(ResultSet::getShort),
                Object::toString));
        DataType.INTEGER.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::int4"),
                getGetPrimitiveValueFunction(ResultSet::getInt),
                Object::toString));
        DataType.LONG.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::bigint"),
                getGetPrimitiveValueFunction(ResultSet::getLong),
                Object::toString));
        DataType.BIG_DECIMAL.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::decimal"),
                getGetPrimitiveValueFunction((resultSet, columnIndex) -> {
                    BigDecimal result = resultSet.getBigDecimal(columnIndex);
                    if (result != null) {
                        result = result.stripTrailingZeros();
                        if (result.scale() < 0) {
                            result = result.setScale(0);
                        }
                    }
                    return result;
                }),
                Object::toString));
        DataType.FLOAT.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::float4"),
                getGetPrimitiveValueFunction(ResultSet::getFloat),
                Object::toString));
        DataType.DOUBLE.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::float8"),
                getGetPrimitiveValueFunction(ResultSet::getDouble),
                Object::toString));
        DataType.DATE.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::date"),
                getGetPrimitiveValueFunction(ResultSet::getDate),
                value -> DateTimeFormatter.ISO_LOCAL_DATE.format(((Date) value).toLocalDate())));
        DataType.DATETIME.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::timestamp"),
                getGetPrimitiveValueFunction(ResultSet::getTimestamp),
                value -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(((Timestamp) value).toLocalDateTime())));
        DataType.OFFSET_DATETIME.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::timestamp"),
                getGetPrimitiveValueFunction(ResultSet::getTimestamp),
                value -> DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(((Timestamp) value).toInstant().atZone(offsetDateTimeZoneId).toOffsetDateTime())));
        DataType.TIME.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::time"),
                getGetPrimitiveValueFunction2(SqlDialect::readTime),
                value -> DateTimeFormatter.ISO_LOCAL_TIME.format((LocalTime) value)));
        DataType.BOOLEAN.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::bool"),
                getGetPrimitiveValueFunction(ResultSet::getBoolean),
                Function.identity()));
        DataType.BYTE_ARRAY.getMetaDataManager().put(DataTypeMetaData.class, WRITE_KEY, new DataTypeMetaData(
                node("::bytea"),
                getGetPrimitiveValueFunction(ResultSet::getBytes),
                value -> Base64.getEncoder().encodeToString((byte[]) value)));

        Map<EntityDescription, Set<String>> heirTypes = new LinkedHashMap<>();
        modelDescription.getEntityDescriptions().forEach((entityType, entityDescription) -> {
            EntityDescriptionMetaData entityDescriptionMetaData = new EntityDescriptionMetaData();

            EntityDescription currentEntityDescription = entityDescription;
            do {
                heirTypes.computeIfAbsent(currentEntityDescription, key -> new LinkedHashSet<>()).add(entityDescription.getName());
                currentEntityDescription = currentEntityDescription.getParentEntityDescription();
            } while (currentEntityDescription != null);

            entityDescription.getPrimitiveDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processPrimitiveSpecification));
            entityDescription.getPrimitivesCollectionDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processPrimitivesCollectionSpecification));
            entityDescription.getReferenceDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processReferenceSpecification));
            entityDescription.getReferenceBackReferenceDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processBackReferenceReferenceSpecification));
            entityDescription.getReferencesCollectionDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processReferencesCollectionSpecification));
            entityDescription.getReferencesCollectionBackReferenceDescriptions().keySet().forEach(propertyName -> entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processBackReferenceReferencesCollectionSpecification));
            entityDescription.getGroupDescriptions().forEach((propertyName, groupDescription) -> {
                entityDescriptionMetaData.propertyProcessFunctions.put(propertyName, SqlQueryProcessor::processGroupSpecification);
                GroupDescriptionMetaData groupDescriptionMetaData = new GroupDescriptionMetaData();
                groupDescription.getPrimitiveDescriptions().keySet().forEach(propertyName2 -> groupDescriptionMetaData.propertyProcessFunctions.put(propertyName2, SqlQueryProcessor::processGroupPrimitiveSpecification));
                groupDescription.getReferenceDescriptions().keySet().forEach(propertyName2 -> groupDescriptionMetaData.propertyProcessFunctions.put(propertyName2, SqlQueryProcessor::processGroupReferenceSpecification));
                groupDescription.getMetaDataManager().put(GroupDescriptionMetaData.class, WRITE_KEY, groupDescriptionMetaData);
            });
            entityDescription.getMetaDataManager().put(EntityDescriptionMetaData.class, WRITE_KEY, entityDescriptionMetaData);
        });

        modelDescription.getEntityDescriptions().forEach((entityType, entityDescription) -> {
            EntityDescriptionMetaData entityDescriptionMetaData = entityDescription.getMetaDataManager().get(EntityDescriptionMetaData.class);
            entityDescriptionMetaData.inHeirTypesStringNode = node(" in (" + heirTypes.get(entityDescription).stream().
                    map(heirType -> '\'' + heirType + '\'')
                    .collect(Collectors.joining(", ")) + ')');
        });
    }

    /**
     * Delete
     *
     * @param modelDescription Model description
     */
    static void remove(ModelDescription modelDescription) {
        Arrays.stream(DataType.values()).forEach(dataType -> dataType.getMetaDataManager().remove(DataTypeMetaData.class, WRITE_KEY));
        modelDescription.getEntityDescriptions().forEach((entityType, entityDescription) -> entityDescription.getMetaDataManager().remove(EntityDescriptionMetaData.class, WRITE_KEY));
    }
}
