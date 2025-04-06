package com.sbt.jpa.annotation;

import java.util.Objects;

public final class AggregatePropagationHelper {
    private AggregatePropagationHelper() {
        /* no-ops */
    }

    /**
     * Instantiates an aggregate instance based on the instance of the BaseAggregateEntity interface.
     * If the instance does not implement the interface, then the result of the method will be the instance itself
     *
     * @param instance object for which the aggregate will be determined
     * @param <R>      result type
     * @param <V>      type of instance
     * @return result
     */
    public static <R, V> R defineAggregateRootByParent(V instance) {
        Objects.requireNonNull(instance, "Define aggregate root by parent fail, instance is null");

        if (BaseAggregateEntity.class.isAssignableFrom(instance.getClass())) {
            return (R) ((BaseAggregateEntity<?>) instance).getAggregateRoot();
        }

        return (R) instance;
    }

    /**
     * Sets the value of aggregateRoot for the instance if the instance implements the interface
     * BaseAggregateEntity. The parameter aggregateRootSourceInstance specifies the object according to which
     * it is required to compute the aggregate
     *
     * @param instance                    object for which the installation of aggregateRoot is performed
     * @param aggregateRootSourceInstance object from which the aggregate is computed
     */
    public static void applyAggregateRootValue(Object instance,
                                               Object aggregateRootSourceInstance) {

        Objects.requireNonNull(instance, "Apply aggregate root value fail, instance is null");

        if (BaseAggregateEntity.class.isAssignableFrom(instance.getClass())) {
            ((BaseAggregateEntity<?>) instance).setAggregateRoot(defineAggregateRootByParent(aggregateRootSourceInstance));
        }


    }


}
