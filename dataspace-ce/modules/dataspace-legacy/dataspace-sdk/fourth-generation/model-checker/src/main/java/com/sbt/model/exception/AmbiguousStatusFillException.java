package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.model.exception.parent.TestGeneratorException;

public class AmbiguousStatusFillException extends TestGeneratorException {
    public AmbiguousStatusFillException() {
        super(GeneralSdkException.join("Populating the status is allowed in one place.",
                "Или это отдельный файл status.xml или в модели(model.xml)"),
            GeneralSdkException.join("Уберите импорт статуса из файла(<import type=\"STATUS\"/>)",
                "or remove the status description in the model (<status>)"));
    }
}
