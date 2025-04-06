package ru.sbertech.dataspace.security.model.interfaces;

import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Aggregate;
import com.sbt.xmlmarker.Final;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Index;
import com.sbt.xmlmarker.Indexes;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.Table;
import com.sbt.xmlmarker.Type;

/**
 * Security settings
 */
@Id(IdCategory.SNOWFLAKE)
@Label("Table with security parameters")
@Final
@Access(Changeable.READ_ONLY)
@Name(SysAdminSettings.NAME)
@Table(SysAdminSettings.TABLE_NAME)
@Indexes(
        value = {@Index(properties = "key", unique = true)}
)
@NoIdempotence
public interface SysAdminSettings extends IdEntity<String> {
    String NAME = "SysAdminSettings";
    String TABLE_NAME = "T_SEC_ADMINSETTINGS";

    @Label("Security settings key")
    String getKey();

    @Label("Security setting value")
    @Type("text")
    String getValue();

    @Aggregate
    @Label("Root")
    SysRootSecurity getRootSecurity();
}
