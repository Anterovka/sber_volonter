package com.sbt.model.exception;

import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.model.exception.parent.CheckModelException;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Exception: For the SINGLE_TABLE strategy, property names are duplicated in the class hierarchy.
 */
public class DuplicateSingleTablePropertyNameException extends CheckModelException {

    public DuplicateSingleTablePropertyNameException(XmlModelClass modelClass, Set<String> names) {
        super(join("For class '", modelClass.getName(), "' with inheritance strategy",
                ClassStrategy.SINGLE_TABLE, "in the inheritance hierarchy duplicates of property names were found:",
                getNames(names), "which is not allowed for the specified inheritance strategy."),
            join("It is necessary to get rid of property ambiguity. Move the duplicated properties.",
                "in the parent or add an additional class to the model hierarchy, to which you transfer",
                "указанные свойства."));
    }

    private static String getNames(Set<String> names) {
        return "[" + StringUtils.join(names, ", ") + "]";
    }
}
