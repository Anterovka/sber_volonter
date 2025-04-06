package com.sbt.dictionary.exceptions;

import com.sbt.dictionary.DictionaryGenerator;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryDeleteOldNamedFileException extends DictionaryCheckParentException {

    public DictionaryDeleteOldNamedFileException(String fileName) {
        super(join("There was a problem deleting the file '", fileName, "'"),
            join("To improve the intuitive readability of the model schema,",
                "to reference files where current model data is stored, a prefix is added", "'",
                DictionaryGenerator.CURRENT_DATA_PREFIX, "'.",
                "Old files without a prefix need to be deleted from the model directory.",
                "Check security access to delete the specified file and",
                "is the file already in use by another application."));
    }
}
