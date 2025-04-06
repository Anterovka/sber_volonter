package sbp.sbt.dataspacecore.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

public class CommonUtils {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CommonUtils() {
throw new UnsupportedOperationException("The object of this class cannot be created");
    }

    /** Wraps a string in single quotes, inside the string replaces single quotes with two single quotes */
    public static String screenString(String input) {
        if (input == null) {
            return null;
        }
        return "'" + input.replace("'", "''") + "'";
    }

    /** Returns the first part of the path */
    public static String getFirstPathPart(String path, String delimeter) {
        return path.split(delimeter)[0];
    }

    public static <T, E> List<E> arrayToList(Enumeration<T> array, Function<T, E> func) {
        ArrayList<E> result = new ArrayList<>();
        while(array.hasMoreElements()) {
            result.add(func.apply(array.nextElement()));
        }
        return result;
    }
}
