package lazy.zoo.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

public final class DataUtils {
    public static final String PROJECT_NAME = "gradle-git-plugin";
    private static final String PROJECT_GROUP = "lazy.zoo";

    public static Project getProject() {
        Project project = ProjectBuilder.builder().withName(PROJECT_NAME).build();
        project.setGroup(PROJECT_GROUP);
        project.getPlugins().apply("lazy.zoo.gradle.git-data-plugin");
        return project;
    }
}
