package ru.sbertech.dataspace.security.model.interfaces;

import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Aggregate;
import com.sbt.xmlmarker.Final;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Length;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.Table;

/**The additional conditions for the query variables are as follows. */
@Id(IdCategory.SNOWFLAKE)
@Label("Table with additional conditions")
@Final
@Access(Changeable.READ_ONLY)
@Name(SysParamAddition.NAME)
@Table(SysParamAddition.TABLE_NAME)
@NoIdempotence
public interface SysParamAddition extends IdEntity<String> {
    String NAME = "SysParamAddition";
    String TABLE_NAME = "T_SEC_PARAMADDITION";

    @Label("Declared variable name")
    String getParamName();

    @Label("Additional condition to the variable")
    @Length(4000)
    String getParamAddition();

    @Label("GQL request to which additional conditions are applied")
    @Aggregate
    SysOperation getOperation();
}
