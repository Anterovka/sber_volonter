package com.sbt.mg.data.model;

import com.sbt.mg.data.model.usermodel.UserXmlCciIndex;

import java.util.ArrayList;
import java.util.List;

public class XmlCciIndex extends UserXmlCciIndex<Property> {

    @Override
    public List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }

        return properties;
    }

}
