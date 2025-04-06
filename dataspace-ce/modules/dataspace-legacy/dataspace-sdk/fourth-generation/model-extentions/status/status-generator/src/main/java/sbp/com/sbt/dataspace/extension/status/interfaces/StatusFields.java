package sbp.com.sbt.dataspace.extension.status.interfaces;

import com.sbt.parameters.enums.Changeable;
import com.sbt.xmlmarker.Access;
import com.sbt.xmlmarker.Embeddable;
import com.sbt.xmlmarker.Label;

@Label("Status fields type")
@Access(Changeable.SYSTEM)
@Embeddable
public interface StatusFields {

    @Label("Status code")
    String getCode();

    @Label("Status reason")
    String getReason();
}
