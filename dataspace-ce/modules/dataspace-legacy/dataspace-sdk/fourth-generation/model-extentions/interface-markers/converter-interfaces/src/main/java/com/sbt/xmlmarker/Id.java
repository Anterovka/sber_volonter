package com.sbt.xmlmarker;

import com.sbt.parameters.enums.IdCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    String type() default "String";
    int length() default 254;

    IdCategory value();
}

