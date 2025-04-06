package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

/**
 * XML description of grouping
 */
class GroupDescriptionXml {

    @XmlAttribute
    String name;
    @XmlAttribute
    String groupName;
    @XmlElement(name = "primitive")
    List<PrimitiveDescriptionXml> primitiveDescriptions = new ArrayList<>();
    @XmlElement(name = "reference")
    List<ReferenceDescriptionXml> referenceDescriptions = new ArrayList<>();
}
