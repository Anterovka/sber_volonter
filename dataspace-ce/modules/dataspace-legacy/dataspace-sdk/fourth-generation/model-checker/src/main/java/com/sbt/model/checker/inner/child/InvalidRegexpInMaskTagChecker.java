package com.sbt.model.checker.inner.child;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.InvalidRegularExpressionInMaskTagException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class InvalidRegexpInMaskTagChecker implements ChildPropertiesChecker {
    @Override
    public void check(XmlModelClass xmlModelClass, Set<XmlModelClassProperty> properties) {
        Map<XmlModelClassProperty, String> propsWithInvalidRegexpInMaskTag = new HashMap<>();

        properties.forEach(property -> {
            String errorMessage = validateRegexp(property.getMask());
            if (errorMessage != null) {
                propsWithInvalidRegexpInMaskTag.put(property, errorMessage);
            }
        });

        if (!propsWithInvalidRegexpInMaskTag.isEmpty()) {
            throw new InvalidRegularExpressionInMaskTagException(xmlModelClass, propsWithInvalidRegexpInMaskTag);
        }
    }

    private String validateRegexp(String regexp) {
        try {
            Pattern.compile(regexp);
            return null;
        } catch (PatternSyntaxException syntaxException) {
            return syntaxException.getDescription() + " near index " + syntaxException.getIndex();
        } catch (Throwable throwable) {
            return "error localization failed";
        }
    }
}
