package com.sbt.model.diff;

import com.google.common.collect.Sets;
import com.sbt.dataspace.pdm.ParameterContext;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.model.exception.diff.EnumValueCutException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class EnumDiff implements DiffHandler {
    @Override
    public void handler(XmlModel newModel, XmlModel baseModel, ParameterContext parameterContext) {
        checkEnumPropertyChange(newModel, baseModel);
        checkEnumAdd(newModel, baseModel);
    }

    private void checkEnumAdd(XmlModel newModel, XmlModel baseModel) {
        newModel.getEnums().forEach(classEnum -> {
            if (!baseModel.containsEnum(classEnum.getName())) {
                baseModel.addEnum(classEnum);
            }
        });
    }

    private void checkEnumPropertyChange(XmlModel newModel, XmlModel baseModel) {
        baseModel.getEnums().forEach(classEnum -> {
            Optional<XmlModelClassEnum> any = newModel.getEnums().stream()
                    .filter(classEnum1 -> classEnum.getName().equals(classEnum1.getName()))
                    .findAny();

            if (!any.isPresent()) {
                return;
            }

            checkEnumLabelChange(classEnum, any.get());

            Set<XmlEnumValue> newEnumValues = new HashSet<>(any.get().getEnumValues());
            Set<XmlEnumValue> baseEnumValues = new HashSet<>(classEnum.getEnumValues());

            setEnumValuesLabelDescription(baseEnumValues, newEnumValues);

            Sets.SetView<XmlEnumValue> dropped = Sets.difference(baseEnumValues, newEnumValues);

            if (!dropped.isEmpty()) {
                throw new EnumValueCutException(classEnum.getName(), dropped);
            }

            Sets.SetView<XmlEnumValue> added = Sets.difference(newEnumValues, baseEnumValues);
            classEnum.getEnumValues().addAll(added);

            Sets.SetView<XmlEnumValue> intersected = Sets.intersection(baseEnumValues, newEnumValues);

            intersected.forEach(enumValue -> enumValue.setExtensions(newEnumValues.stream()
                    .filter(newModelEnum -> newModelEnum.equals(enumValue))
                    .findAny().get().getExtensions()));
        });
    }

    private void checkEnumLabelChange(XmlModelClassEnum currentEnum, XmlModelClassEnum newEnum) {
        if (!Objects.equals(currentEnum.getLabel(), newEnum.getLabel())) {
            currentEnum.setLabel(newEnum.getLabel());
        }
    }

    private void setEnumValuesLabelDescription(Set<XmlEnumValue> baseEnumValues, Set<XmlEnumValue> newEnumValues) {
        baseEnumValues.forEach(baseEnumValue -> {
            newEnumValues.stream()
                    .filter(it -> it.getName().equals(baseEnumValue.getName()))
                    .findFirst()
                    .ifPresent(it -> {
                        baseEnumValue.setLabel(it.getLabel());
                        baseEnumValue.setDescription(it.getDescription());

                        setEnumExtensions(baseEnumValue, it);
                    });
        });
    }

    private void setEnumExtensions(XmlEnumValue baseValue, XmlEnumValue newValue) {
        baseValue.setExtensions(newValue.getExtensions());
    }
}
