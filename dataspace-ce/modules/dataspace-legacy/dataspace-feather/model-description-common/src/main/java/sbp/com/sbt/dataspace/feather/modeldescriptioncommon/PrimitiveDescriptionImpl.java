package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.checkEnumValueType;

/**
 * Implementation of primitive description
 */
class PrimitiveDescriptionImpl extends AbstractPropertyDescriptionWithColumnName implements PrimitiveDescription {

    DataType type;
    boolean mandatory;
    EnumDescription enumDescription;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        super.process(propertyDescriptionSettings);
        PrimitiveDescriptionSettings primitiveDescriptionSettings = (PrimitiveDescriptionSettings) propertyDescriptionSettings;
        type = checkNotNull(primitiveDescriptionSettings.type, "Тип");
        mandatory = primitiveDescriptionSettings.mandatory;
        if (primitiveDescriptionSettings.enumType != null) {
            checkEnumValueType(this);
            enumDescription = ownerEntityDescription.getModelDescription().getEnumDescription(primitiveDescriptionSettings.enumType);
        }
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public EnumDescription getEnumDescription() {
        return enumDescription;
    }
}
