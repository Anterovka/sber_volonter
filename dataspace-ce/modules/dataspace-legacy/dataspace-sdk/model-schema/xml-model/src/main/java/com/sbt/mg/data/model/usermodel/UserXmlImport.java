package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import java.util.Objects;

@XmlTagName(XmlModel.IMPORT_TAG)
public class UserXmlImport {

    public static final String TYPE_TAG = "type";
    public static final String FILE_TAG = "file";

    private final String type;
    private String file;

    public UserXmlImport(@JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                         @JacksonXmlProperty(isAttribute = true, localName = FILE_TAG) String file) {
        this.type = type;
        this.file = file;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }

    @JacksonXmlProperty(isAttribute = true, localName = FILE_TAG)
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserXmlImport xmlImport = (UserXmlImport) o;
        return Objects.equals(type, xmlImport.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "XmlImport{" +
                "type='" + type + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
