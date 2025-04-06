package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * XML enumeration description
 */
class EnumDescriptionXml {

    @XmlAttribute
    String name;
    @XmlElement(name = "value")
    Set<String> values = new LinkedHashSet<>();
}
