package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * XML entity description
 */
class EntityDescriptionXml {

    @XmlAttribute
    String name;
    @XmlAttribute(name = "extends")
    String parentEntityType;
    @XmlAttribute(name = "belongs")
    String aggregateEntityType;
    @XmlAttribute(name = "final")
    boolean final0;
    @XmlAttribute
    boolean aggregate;
    @XmlAttribute
    InheritanceStrategy inheritanceStrategy;
    @XmlAttribute
    TableType tableType = TableType.SIMPLE;
    @XmlAttribute
    String tableName;
    @XmlAttribute
    String idColumnName;
    @XmlAttribute
    String typeColumnName;
    @XmlAttribute
    String aggregateColumnName;
    @XmlAttribute
    String systemLocksTableName;
    @XmlAttribute
    String systemLocksAggregateColumnName;
    @XmlAttribute
    String systemLocksVersionColumnName;
    @XmlElement(name = "param")
    List<ParamDescriptionXml> paramDescriptions = new ArrayList<>();
    @XmlElement(name = "primitive")
    List<PrimitiveDescriptionXml> primitiveDescriptions = new ArrayList<>();
    @XmlElement(name = "primitivesCollection")
    List<PrimitivesCollectionDescriptionXml> primitivesCollectionDescriptions = new ArrayList<>();
    @XmlElement(name = "reference")
    List<ReferenceDescriptionXml> referenceDescriptions = new ArrayList<>();
    @XmlElement(name = "referencesCollection")
    List<ReferencesCollectionDescriptionXml> referencesCollectionDescriptions = new ArrayList<>();
    @XmlElement(name = "group")
    List<GroupDescriptionXml> groupDescriptions = new ArrayList<>();
}
