package lazy.zoo.gradle.utils;

import org.gradle.api.GradleException;
import org.gradle.api.Project;

public class ValidationUtils {
    public static void checkVersion(Project project) {
        String version = project.getVersion().toString();
        if ("".equals(version) || project.DEFAULT_VERSION.equals(version)) {
            throw new GradleException("please specify a version for " + project.getName());
        }
    }
}
