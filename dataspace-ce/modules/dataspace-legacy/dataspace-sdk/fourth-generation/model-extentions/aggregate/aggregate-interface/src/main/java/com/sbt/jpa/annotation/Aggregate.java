package com.sbt.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggregate {
    /**
     * @return list of managed classes
     * @deprecated {@link #managedClass()}
     */
    @Deprecated
    Class<?>[] value();

    Class<?> managedClass() default Object.class;

    String from();

    /**
     * @return the property through which management is performed
     * @deprecated {@link #aggregate()}
     */
    @Deprecated
    String affinity() default "aggregateRoot";

    String aggregate() default "aggregateRoot";
}
