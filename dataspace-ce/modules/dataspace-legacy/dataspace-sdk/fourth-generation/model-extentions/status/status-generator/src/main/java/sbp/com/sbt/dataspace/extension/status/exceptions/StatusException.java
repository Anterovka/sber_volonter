package sbp.com.sbt.dataspace.extension.status.exceptions;

import com.sbt.mg.exception.AnyPositionException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusException extends AnyPositionException {

    public StatusException(Map<String, List<String>> classToGroupMap) {
        super("The default status must be specified for each group(isInitial=\"true\")." +
                "This status is not specified " + makeMessage(classToGroupMap),
            "Define the default status for each specified group."
        );
    }

    private static String makeMessage(Map<String, List<String>> classToGroupMap) {
        return classToGroupMap.entrySet().stream()
            .map(entry -> {
                StringBuilder builder = new StringBuilder();

                builder.append("for class ").append(entry.getKey()).append(" in groups: [");
                builder.append(String.join(",", entry.getValue()));
                builder.append("]");

                return builder.toString();
            })
            .collect(Collectors.joining(", "));
    }
}
