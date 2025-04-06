package sbp.com.sbt.dataspace.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.mg.data.model.XmlModel;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class GoalDirectory {
    private static final XmlMapper MAPPER = new XmlMapper();

    private GoalDirectory() {
        //Utility class
    }

    private static String defineName(boolean isSnapshot) {
        return isSnapshot ? "snapshot" : "model";
    }

    public static String snapshot() {
        return defineName(true);
    }

    public static String nameByModel(File modelFile) {
        String snapshot = "SNAPSHOT";
        try {
            XmlModel xmlModel = MAPPER.readValue(modelFile, XmlModel.class);
            return defineName(
                xmlModel.getVersion().toUpperCase(Locale.ENGLISH).contains(snapshot)
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
