package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.ParamDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Implementation of parameter description
 */
public class ParamDescriptionImpl implements ParamDescription {

    String name;
    DataType type;
    boolean collection;
    Object defaultValue;

    /**
     * @param name         Name
     * @param type         Тип
     * @param collection   Is it a collection?
     * @param defaultValue Default value
     */
    ParamDescriptionImpl(String name, DataType type, boolean collection, Object defaultValue) {
        this.name = name;
        this.type = checkNotNull(type, "Тип");
        this.collection = collection;
        this.defaultValue = defaultValue;
        if (defaultValue != null) {
            if (collection) {
                throw new CollectionParamDefaultValueNotSupportedException(name);
            }
            if (!type.getClass0().isInstance(defaultValue)) {
                throw new ParamDefaultValueIsIncorrectException(name);
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
}
