package sbp.sbt.dataspacecore.utils;

import java.util.List;

public class ListUtils {
    private ListUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static void addAllIfNotNull(List dst, List src) {
        if (src != null) {
            dst.addAll(src);
        }
    }
}
