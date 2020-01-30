package lazy.zoo.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

public final class DataUtils {
    private static final String PROJECT_NAME = "gradle-git-plugin";
    private static final String PROJECT_GROUP = "coding.zoo";
    private static final String PROJECT_ART_VER = "1.0.0-SNAPSHOT";

    public static Project getProject() {
        Project project = ProjectBuilder.builder().withName(PROJECT_NAME).build();
        project.setGroup(PROJECT_GROUP);
        project.setVersion(PROJECT_ART_VER);
        project.getPlugins().apply("lazy.zoo.gradle.git-data-plugin");
        return project;
    }
}