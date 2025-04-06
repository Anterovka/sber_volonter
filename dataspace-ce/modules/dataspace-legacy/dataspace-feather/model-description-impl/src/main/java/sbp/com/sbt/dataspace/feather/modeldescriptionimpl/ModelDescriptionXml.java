package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * XML model description
 */
@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.FIELD)
class ModelDescriptionXml {

    @XmlElement(name = "enum")
    List<EnumDescriptionXml> enumDescriptions = new ArrayList<>();
    @XmlElement(name = "entity")
    List<EntityDescriptionXml> entityDescriptions = new ArrayList<>();
}
