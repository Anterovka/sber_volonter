package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitivesCollectionDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.checkEnumValueType;

/**
 * Implementation of the primitives collection description
 */
class PrimitivesCollectionDescriptionImpl extends AbstractCollectionDescription implements PrimitivesCollectionDescription {

    DataType type;
    EnumDescription enumDescription;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        super.process(propertyDescriptionSettings);
        PrimitivesCollectionDescriptionSettings primitivesCollectionDescriptionSettings = (PrimitivesCollectionDescriptionSettings) propertyDescriptionSettings;
        type = checkNotNull(primitivesCollectionDescriptionSettings.type, "Тип");
        if (primitivesCollectionDescriptionSettings.enumType != null) {
            checkEnumValueType(this);
            enumDescription = ownerEntityDescription.getModelDescription().getEnumDescription(primitivesCollectionDescriptionSettings.enumType);
        }
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public EnumDescription getEnumDescription() {
        return enumDescription;
    }
}
