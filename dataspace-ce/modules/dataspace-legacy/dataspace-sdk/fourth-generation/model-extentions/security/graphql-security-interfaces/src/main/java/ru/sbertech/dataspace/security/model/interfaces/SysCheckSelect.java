package ru.sbertech.dataspace.security.model.interfaces;

import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Aggregate;
import com.sbt.xmlmarker.Final;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.Table;
import com.sbt.xmlmarker.Type;

/**
 * Check query table, executed before/after main request
 */
@Id(IdCategory.SNOWFLAKE)
@Label("Table of verification requests performed before/after the main request")
@Final
@Access(Changeable.READ_ONLY)
@Name(SysCheckSelect.NAME)
@Table(SysCheckSelect.TABLE_NAME)
@NoIdempotence
public interface SysCheckSelect extends IdEntity<String> {
    String NAME = "SysCheckSelect";
    String TABLE_NAME = "T_SEC_CHECKSELECT";

    /**
     * User-defined description
     */
    @Label("User-defined description")
    String getDescription();

    /**
     * Entity type on which the request is based
     */
    @Label("Entity type on which the request is built")
    String getTypeName();

    /**
     * Filtering condition
     */
    @Label("Filtering condition")
    @Type("text")
    String getConditionValue();

//    Order of display
    @Label("Order of display")

    Integer getOrderValue();

    /** GQL request to which the check is applied  */
    @Label("GQL request to which the check is applied")
    @Aggregate
    SysOperation getOperation();

    /** Suppress execution of the check before the request */
    @Label("Should the check be suppressed before the request?")
    @Type("Boolean")
    Boolean getBeforeOperationDisable();

    /** Filtering condition */
    @Type("Boolean")
    Boolean getBeforeCommitEnable();
}
