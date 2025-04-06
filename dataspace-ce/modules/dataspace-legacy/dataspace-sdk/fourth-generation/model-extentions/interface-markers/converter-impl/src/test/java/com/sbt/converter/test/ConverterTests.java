package com.sbt.converter.test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.converter.InterfaceConverterToXml;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConverterTests {

    private static final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void compositeIdTest() throws IOException {
        XmlModel model = xmlMapper.readValue(this.getClass().getResourceAsStream("/model.xml"), XmlModel.class);
        InterfaceConverterToXml.convertToXml(CompositeIdClass.class, model);
        InterfaceConverterToXml.convertToXml(SingleCompositeIdClass.class, model);
        InterfaceConverterToXml.convertToXml(NoIdClass.class, model);

        Assertions.assertEquals(5, model.getClassesAsList().size());

        // Compound key 2 fields.
        final XmlModelClass sysCompositeIdClass = model.getClass("SysCompositeIdClass");
        Assertions.assertEquals(2, sysCompositeIdClass.getPropertiesAsList().size());
        Assertions.assertTrue(sysCompositeIdClass.isNoIdempotence());
        Assertions.assertEquals(Changeable.SYSTEM, sysCompositeIdClass.getClassAccess());

        final XmlModelClassProperty serNumber = sysCompositeIdClass.getProperty(JpaConstants.COMPOSITE_ID);
        Assertions.assertEquals("SysCompositeIdClassPK", serNumber.getType());

        final XmlModelClass sysCompositeIdClassPK = model.getClass("SysCompositeIdClassPK");
        Assertions.assertTrue(sysCompositeIdClassPK.isEmbeddable());
        Assertions.assertEquals(Changeable.SYSTEM, sysCompositeIdClassPK.getClassAccess());

        Assertions.assertEquals(2, sysCompositeIdClassPK.getPropertiesAsList().size());
        final XmlModelClassProperty ser = sysCompositeIdClassPK.getProperty("ser");
        Assertions.assertEquals("String", ser.getType());
        final XmlModelClassProperty number = sysCompositeIdClassPK.getProperty("number");
        Assertions.assertEquals("String", number.getType());

        // The composite key 1 field
        final XmlModelClass singleCompositeIdClass = model.getClass("SysSingleCompositeIdClass");
        Assertions.assertEquals(1, singleCompositeIdClass.getPropertiesAsList().size());
        Assertions.assertTrue(singleCompositeIdClass.isNoIdempotence());
        Assertions.assertEquals(Changeable.SYSTEM, singleCompositeIdClass.getClassAccess());

        final XmlModelClassProperty compositeId = singleCompositeIdClass.getPropertiesAsList().get(0);
        Assertions.assertEquals("compositeId", compositeId.getName());
        Assertions.assertEquals("SysSingleCompositeIdClassPK", compositeId.getType());
        Assertions.assertTrue(compositeId.isId());

        final XmlModelClass sysSingleCompositeIdClassPK = model.getClass("SysSingleCompositeIdClassPK");
        Assertions.assertTrue(sysSingleCompositeIdClassPK.isEmbeddable());
        Assertions.assertEquals(Changeable.SYSTEM, sysSingleCompositeIdClassPK.getClassAccess());
        Assertions.assertEquals(1, sysSingleCompositeIdClassPK.getPropertiesAsList().size());

        //Without a key
        final XmlModelClass sysNoIdClass = model.getClass("SysNoIdClass");
        Assertions.assertEquals(1, sysNoIdClass.getPropertiesAsList().size());
        Assertions.assertTrue(sysNoIdClass.isNoIdempotence());
        Assertions.assertEquals(Changeable.SYSTEM, sysNoIdClass.getClassAccess());
        Assertions.assertFalse(sysNoIdClass.getPropertiesAsList().get(0).isId());

        // To look.
        System.out.println(xmlMapper.writeValueAsString(model));
    }
}
