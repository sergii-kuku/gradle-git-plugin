package coding.zoo;

import coding.zoo.extension.GradleGitPluginExtension;
import coding.zoo.git.GitInfo;
import coding.zoo.utils.ValidationUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradleGitPlugin implements Plugin<Project> {
    private String projectVersion;
    private GitInfo gitInfo;

    @Override
    public void apply(Project project) {
        ValidationUtils.checkProperty("version", project.getName(), project.getVersion().toString());
        project.getExtensions().create("gitInfo", GradleGitPluginExtension.class, this);
        this.projectVersion = project.getVersion().toString();
        this.gitInfo = GitInfo.getGitInfo(project, project.getName(), project.getProperties().get("branchName") != null ? (String) project.getProperties().get("branchName") : null);

        project.getTasks().register("gitInfo", task -> {
            task.setDescription("display git information");
            task.doLast(s -> {
                project.getLogger().lifecycle("currentBranchType: {}", gitInfo.getCurrentBranchType().name());
                project.getLogger().lifecycle("currentBranchName: {}", gitInfo.getCurrentBranchName());
            });
        });
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public GitInfo getGitInfo() {
        return gitInfo;
    }
}
