package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import java.util.Collections;
import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * EnumDescriptionSettings
 */
public final class EnumDescriptionSettings {

    Set<String> values = Collections.emptySet();

    /**
     * Get values
     */
    // NotNull
    public Set<String> getValues() {
        return values;
    }

    /**
     * Set values
     *
     * @param values Values
     * @return Current settings
     */
    public EnumDescriptionSettings setValues(Set<String> values) {
        this.values = checkNotNull(values, "Values");
        return this;
    }
}
