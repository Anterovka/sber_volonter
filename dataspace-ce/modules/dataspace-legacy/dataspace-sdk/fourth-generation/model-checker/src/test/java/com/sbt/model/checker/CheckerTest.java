package com.sbt.model.checker;

import com.sbt.dataspace.pdm.StreamModel;
import com.sbt.mg.data.model.XmlModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CheckerTest {

    @Test
    public void indexWarningTest() {
        StreamModel streamModel = StreamModel.forStreams()
                .setRootModel(this.getClass().getResourceAsStream("/warnings/index/model.xml"));

        List<String> warnings = new Checker(streamModel).check();

        System.out.println(">>>");
        System.out.println(warnings);

        Assertions.assertEquals(1, warnings.size());
        Assertions.assertTrue(warnings.get(0).contains("In the model, there are indices that overlap in their composition with other indices. " +
            "Indices are contained in classes: Class 'MyClass', field set:"));
        Assertions.assertTrue(warnings.get(0).contains("['code'],['name'];"));
        Assertions.assertTrue(warnings.get(0).contains("['code'];"));
    }

    @Test
    public void aggregateTest() {
        StreamModel streamModel = StreamModel.forStreams()
            .setRootModel(this.getClass().getResourceAsStream("/aggregate/model.xml"));

        Checker checker = new Checker(streamModel);
        checker.check();
        XmlModel model = checker.getModel();

        Assertions.assertTrue(model.getClass("MyClass").containsProperty("sys_ver"));
        Assertions.assertTrue(model.getClass("RootDictionary").containsProperty("sys_ver"));

        Assertions.assertFalse(model.getClass("MyClassApiCall").containsProperty("sys_ver"));
        Assertions.assertFalse(model.getClass("MySecondClass").containsProperty("sys_ver"));

    }
}

