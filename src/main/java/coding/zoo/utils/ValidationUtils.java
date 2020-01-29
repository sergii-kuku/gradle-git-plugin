package coding.zoo.utils;

import org.gradle.api.GradleException;

public class ValidationUtils {
    public static void checkProperty(String name, String projectName, String value) {
        if (value == null || "".equals(value)) {
            throw new GradleException("missing " + name + " for " + projectName);
        }
    }
}
