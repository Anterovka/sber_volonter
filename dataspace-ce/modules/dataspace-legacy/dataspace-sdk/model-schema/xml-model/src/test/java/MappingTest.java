import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.usermodel.UserXmlModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

class MappingTest {

    @Test
    void mappingTest() throws IOException {

        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final UserXmlModel userXmlModel = mapper.readValue(this.getClass().getResource("/userModel.xml"), UserXmlModel.class);

        String string = mapper.writeValueAsString(userXmlModel);
        System.out.println(string);

        XmlModel xmlModel = mapper.readValue(string, XmlModel.class);

        Assertions.assertTrue(
            xmlModel.getClass("MyMaxClass").getStatuses().getGroups()
                .stream()
                .filter(it -> Objects.equals(it.getCode(), "service"))
                .findFirst()
                .get()
                .getStatuses()
                .stream()
                .filter(it -> Objects.equals(it.getCode(), "new"))
                .findFirst()
                .get()
                .isInitial()
        );


        System.out.println(">>>>>");
        System.out.println( mapper.writeValueAsString(xmlModel));
    }
}
