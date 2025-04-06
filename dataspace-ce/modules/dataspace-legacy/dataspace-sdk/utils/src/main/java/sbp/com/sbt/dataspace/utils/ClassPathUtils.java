package sbp.com.sbt.dataspace.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ClassUtils;
import sbp.com.sbt.dataspace.utils.exceptions.ClassPathUtilsException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


public final class ClassPathUtils {

    private ClassPathUtils() {}

    public static <T> T readResource(String relativePath, XmlMapper xmlMapper, Class<T> goalClass) {
    // The collection is chosen because it has historically developed that way.
    // Before implementation, I did not dare to change it to a single selection
    // TODO rework to single selection
        URL[] resource = findClassPathResources(relativePath);
        try {
            return xmlMapper.readValue(resource[0].openConnection().getInputStream(), goalClass);
        } catch (Exception ex) {
            throw new ClassPathUtilsException("Error when reading resource " + relativePath, ex);
        }
    }

    public static URL[] findClassPathResources(String relativePath) {
        String path = relativePath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Set<URL> result = _findClassPathResources(path);

        return result.toArray(new URL[0]);
    }

    public static URL findClassPathResource(String relativePath) {
        URL[] result = findClassPathResources(relativePath);

        if (result.length != 1) {
            String subString = "";
            if (result.length > 1) {
                for (int i = 0; i < result.length; ++i) {
                    subString += "\r\n" + result[i];
                }
                subString += "\r\n";
            }
            String message = "Found in classpath " + result.length
                + " resources for: " + relativePath
                + subString
                + ". It is necessary to check the classPath of the project and the dependencies used";
            throw new ClassPathUtilsException(message);
        }

        return result[0];
    }

    public static String fileToString(String relativePath) {
        URL resource = findClassPathResource(relativePath);
        try {
            return IOUtils.toString(resource.openStream(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new ClassPathUtilsException("Error reading file and converting it to string", e);
        }
    }

    public static InputStream fileToInputStream(String relativePath) throws IOException {
        URL resource = findClassPathResource(relativePath);
        return resource.openStream();
    }

    private static Set<URL> _findClassPathResources(String path) {
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        Enumeration<URL> resourceUrls;
        try {
            resourceUrls = cl.getResources(path);
        } catch (IOException ex) {
            throw new ClassPathUtilsException("Error finding resource at " + path, ex);
        }

        Set<URL> result = new LinkedHashSet<>();
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(url);
        }
        return result;
    }
}
