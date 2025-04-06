package ru.sbertech.dataspace.security.model.interfaces;

import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Aggregate;
import com.sbt.xmlmarker.Final;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.MappedBy;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.Table;
import com.sbt.xmlmarker.Type;

import java.util.Set;

/**
 * Table of valid GQL requests
 */
@Id(IdCategory.MANUAL)
@Label("Table of allowed GQL requests")
@Final
@Access(Changeable.READ_ONLY)
@Name(SysOperation.NAME)
@Table(SysOperation.TABLE_NAME)
@NoIdempotence
public interface SysOperation extends IdEntity<String> {
    String NAME = "SysOperation";
    String TABLE_NAME = "T_SEC_OPERATION";

    @Label("Хэш")
    String getHashValue();

    @Label("Request")
    @Type("text")
    String getBody();

    @Label("Applicability flag of empty conditions")
    Boolean getAllowEmptyChecks();

    @Label("JWT checks ignoring flag")
// TODO strange flag. According to the idea, it should be determined by the application level settings
    Boolean getDisableJwtVerification();

    @Label("Conditions")
    @MappedBy("operation")
    Set<SysCheckSelect> getCheckSelects();

    @Label("Additional conditions")
    @MappedBy("operation")
    Set<SysParamAddition> getParamAdditions();

    // Stored in the form of a serialized json string
    @Label("Additional routing conditions for fields")
    @Type("text")
    String getPathConditions();

    @Aggregate
    @Label("Root")
    SysRootSecurity getRootSecurity();
}
