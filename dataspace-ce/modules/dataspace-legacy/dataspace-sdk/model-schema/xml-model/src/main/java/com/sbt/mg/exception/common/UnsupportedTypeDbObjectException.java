package com.sbt.mg.exception.common;

import com.sbt.mg.NameHelper;
import com.sbt.mg.exception.GeneralSdkException;

public class UnsupportedTypeDbObjectException extends GeneralSdkException {
    public UnsupportedTypeDbObjectException(NameHelper.TypeDbObject typeDbObject) {
        super(join("Unsupported type of DB object - ", typeDbObject));
    }
}
