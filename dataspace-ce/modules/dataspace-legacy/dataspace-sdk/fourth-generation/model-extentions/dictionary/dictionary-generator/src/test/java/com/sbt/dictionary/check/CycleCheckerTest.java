package com.sbt.dictionary.check;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.mg.data.model.XmlModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CycleCheckerTest {

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    @Test
    void cycleNoDictionaryTest() throws IOException {

        final XmlModel model = XML_MAPPER.readValue(getClass().getResourceAsStream("/cycleNoDictionary/model.xml"), XmlModel.class);

        final CycleChecker cycleChecker = CycleChecker.of(model);

        Assertions.assertTrue(cycleChecker.getCycleDictionaries().isEmpty());
        Assertions.assertTrue(cycleChecker.getNoCycleDictionaries().isEmpty());
    }

    @Test
    void cycleNoCycleTest() throws IOException {
        final XmlModel model = XML_MAPPER.readValue(getClass().getResourceAsStream("/cycleNoCycle/model.xml"), XmlModel.class);

        final CycleChecker cycleChecker = CycleChecker.of(model);

        Assertions.assertTrue(cycleChecker.getCycleDictionaries().isEmpty());
        Assertions.assertEquals(3, cycleChecker.getNoCycleDictionaries().size());
    }

    @Test
    void cycleDictionaryTest() throws IOException {
        final XmlModel model = XML_MAPPER.readValue(getClass().getResourceAsStream("/cycleDictionary/model.xml"), XmlModel.class);

        final CycleChecker cycleChecker = CycleChecker.of(model);

        Assertions.assertEquals(2, cycleChecker.getCycleDictionaries().size());
        Assertions.assertEquals(1, cycleChecker.getNoCycleDictionaries().size());
    }
}
