package lazy.zoo.gradle;

import lazy.zoo.gradle.extension.GitDataPluginExtension;
import lazy.zoo.gradle.git.GitInfo;
import lazy.zoo.gradle.utils.ValidationUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitDataPlugin implements Plugin<Project> {
    private String projectVersion;
    private GitInfo gitInfo;

    @Override
    public void apply(Project project) {
        ValidationUtils.checkProperty("name", "current project", project.getName());
        ValidationUtils.checkProperty("version", project.getName(), project.getVersion().toString());

        project.getExtensions().create("gitData", GitDataPluginExtension.class, this);
        this.projectVersion = project.getVersion().toString();
        this.gitInfo = GitInfo.getGitInfo(project, project.getName(), project.getProperties().get("branchName") != null ? (String) project.getProperties().get("branchName") : null);

        project.getTasks().register("gitData", task -> {
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
