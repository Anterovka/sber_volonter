package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * The aggregate feature for describing a non-root entity was detected.
 */
public class AggregateForNotRootEntityDescriptionFoundException extends FeatherException {

    AggregateForNotRootEntityDescriptionFoundException() {
        super("The aggregate attribute detection sign for non-root entity description is found", "Only the root entity can be an aggregate");
    }
}
