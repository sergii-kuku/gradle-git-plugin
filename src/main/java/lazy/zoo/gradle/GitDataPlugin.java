package lazy.zoo.gradle;

import lazy.zoo.gradle.extension.GitDataPluginExtension;
import lazy.zoo.gradle.git.GitInfo;
import lazy.zoo.gradle.git.GitInfoFactory;
import lazy.zoo.gradle.utils.Cmd;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.List;

public class GitDataPlugin implements Plugin<Project> {
    private final GitInfoFactory gitInfoFactory = new GitInfoFactory(new Cmd());
    private GitInfo gitInfo;
    private Project project;

    @Override
    public void apply(Project project) {
        project.getExtensions().create("gitData", GitDataPluginExtension.class, this);
        this.project = project;
        this.gitInfo = parseGitData(project);

        project.getTasks().register("gitData", task -> {
            task.setDescription("display git branch information");
            task.doLast(s -> project.getLogger().lifecycle(gitInfo.toString()));
        });
    }

    public GitInfo gitInfo() {
        return gitInfo;
    }

    private GitInfo parseGitData(Project project) {
        return gitInfoFactory.getGitInfo(project, project.getProperties().get("branchName") != null ? (String) project.getProperties().get("branchName") : null);
    }

    public void refreshGitInfo(List<String> releaseBranchPatterns) {
        gitInfoFactory.setReleaseBranchPatterns(releaseBranchPatterns);
        gitInfo = parseGitData(project);
    }
}
