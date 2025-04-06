package sbp.com.sbt.dataspace;

import com.sbt.dataspace.pdm.PdmModel;
import liquibase.changelog.DatabaseChangeLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ModelArtifacts {

    private PdmModel pdmModel;
    private DatabaseChangeLog changeLog;
}
