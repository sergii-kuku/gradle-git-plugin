package lazy.zoo.gradle;

import lazy.zoo.gradle.extension.GitDataPluginExtension;
import lazy.zoo.gradle.git.GitInfo;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GitDataPlugin implements Plugin<Project> {
    private String projectVersion;
    private GitInfo gitInfo;

    @Override
    public void apply(Project project) {
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
