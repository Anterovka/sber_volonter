package ru.sbertech.dataspace.security.model.helper;

import com.sbt.mg.data.model.XmlModelClass;
import ru.sbertech.dataspace.security.model.interfaces.SysAdminSettings;
import ru.sbertech.dataspace.security.model.interfaces.SysCheckSelect;
import ru.sbertech.dataspace.security.model.interfaces.SysOperation;
import ru.sbertech.dataspace.security.model.interfaces.SysParamAddition;
import ru.sbertech.dataspace.security.model.interfaces.SysRootSecurity;

import java.util.Arrays;
import java.util.List;

public class SecurityClassesHolder {

    private SecurityClassesHolder() {}

    private static final List<String> SECURITY_CLASS_NAMES =
            Arrays.asList(SysAdminSettings.NAME, SysCheckSelect.NAME, SysOperation.NAME,
                    SysParamAddition.NAME, SysRootSecurity.NAME);

    public static boolean isSecurityClass(XmlModelClass xmlModelClass) {
        return isSecurityClass(xmlModelClass.getName());
    }

    public static boolean isSecurityClass(String modelClassName) {
        return SECURITY_CLASS_NAMES.contains(modelClassName);
    }
}
