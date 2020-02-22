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
        final GitDataPluginExtension ext = project.getExtensions().create("gitData", GitDataPluginExtension.class, this);
        this.projectVersion = project.getVersion().toString();
        this.gitInfo = GitInfo.getGitInfo(project, project.getProperties().get("branchName") != null ? (String) project.getProperties().get("branchName") : null);

        // TODO: GitInfo reduce boilerplate
        // TODO: more version builders (e.g. including hash and nb of commits)
        // TODO: more UTs
        // TODO: custom release version patterns

        project.getTasks().register("gitData", task -> {
            task.setDescription("display git information");
            task.doLast(s -> {
                project.getLogger().lifecycle("branchType: {}", gitInfo.getBranchType());
                project.getLogger().lifecycle("shortBranchName: {}", gitInfo.getShortBranchName());
                project.getLogger().lifecycle("fullBranchName: {}", gitInfo.getFullBranchName());
                project.getLogger().lifecycle("lastCommitHash: {}", gitInfo.getLastCommitHash().orElse(""));
                project.getLogger().lifecycle("numberOfCommits: {}", gitInfo.getNumberOfCommits().orElse(-1));
                project.getLogger().lifecycle("versionWithShortBranchName: {}", ext.getVersionWithShortBranchName());
                project.getLogger().lifecycle("versionWithFullBranchName: {}", ext.getVersionWithFullBranchName());
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
