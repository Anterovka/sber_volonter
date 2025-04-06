package sbp.com.sbt.dataspace.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sbp.com.sbt.dataspace.utils.exceptions.ClassPathUtilsException;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class SonarCoverageTest {

    public static final String TEST_CLASS_PATH = "TestClass.xml";

    public static class TestClass {
        public String code;
        public Long num;

        public String getCode() {
            return code;
        }

        public TestClass setCode(String code) {
            this.code = code;
            return this;
        }

        public Long getNum() {
            return num;
        }

        public TestClass setNum(Long num) {
            this.num = num;
            return this;
        }
    }


    @Test
    void classPathUtilsTest() throws IOException {
        TestClass testClass = ClassPathUtils.readResource("/" + TEST_CLASS_PATH, new XmlMapper(), TestClass.class);
        assertThat(testClass).extracting("code", "num")
            .doesNotContainNull()
            .containsExactly("testCode", 25L);
        XmlMapper xmlMapperMock = Mockito.mock(XmlMapper.class);
        Mockito.when(xmlMapperMock.readValue(any(InputStream.class), any(Class.class)))
            .thenThrow(new RuntimeException("Test error"));
        assertThatThrownBy(() ->
            ClassPathUtils.readResource("TestClass.xml", xmlMapperMock, TestClass.class))
            .isInstanceOf(ClassPathUtilsException.class)
            .hasMessageContaining("Error when reading resource TestClass.xml")
            .getCause()
            .hasMessage("Test error");

        String fileStr = ClassPathUtils.fileToString(TEST_CLASS_PATH);
        assertThat(fileStr).isNotEmpty();

        InputStream inputStream = ClassPathUtils.fileToInputStream(TEST_CLASS_PATH);
        assertThat(inputStream).isNotNull();
    }

    @Test
    void classPathUtilsExceptionTest() {
        ClassPathUtilsException ex = new ClassPathUtilsException();
        ex = new ClassPathUtilsException("some message");
        ex = new ClassPathUtilsException("some message", new RuntimeException());
        ex = new ClassPathUtilsException(new RuntimeException());
    }
}
