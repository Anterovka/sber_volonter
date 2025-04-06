package ru.sbertech.dataspace.security.model.interfaces;

import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.base.IdEntity;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Id;
import com.sbt.xmlmarker.Label;
import com.sbt.xmlmarker.MappedBy;
import com.sbt.xmlmarker.Name;
import com.sbt.xmlmarker.NoIdempotence;
import com.sbt.xmlmarker.Table;

import java.util.Set;

/** Security Table Aggregate */
@Label("Class of security tables aggregate")
@Name(SysRootSecurity.NAME)
@Table(SysRootSecurity.TABLE_NAME)
@Access(Changeable.READ_ONLY)
@Id(IdCategory.MANUAL)
@NoIdempotence
public interface SysRootSecurity extends IdEntity<String> {
    String NAME = JpaConstants.ROOT_SECURITY_CLASS_NAME;
    String TABLE_NAME = "T_SEC_ROOTSECURITY";

@Label("Operations")
    @MappedBy("rootSecurity")
    Set<SysOperation> getOperations();

@Label("Settings")
    @MappedBy("rootSecurity")
    Set<SysAdminSettings> getAdminSettings();
}
