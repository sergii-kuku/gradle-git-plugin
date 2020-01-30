package lazy.zoo.gradle.extension;

import lazy.zoo.gradle.GitDataPlugin;
import lazy.zoo.gradle.git.BranchType;

public class GitDataPluginExtension {
    private final GitDataPlugin plugin;

    public GitDataPluginExtension(GitDataPlugin plugin) {
        this.plugin = plugin;
    }

    public String getCurrentBranchType() {
        return plugin.getGitInfo().getCurrentBranchType().name();
    }

    public String getCurrentBranchName() {
        return plugin.getGitInfo().getCurrentBranchName();
    }

    public String getProjectVersionWithBranch() {
        String version = plugin.getProjectVersion();
        if (plugin.getGitInfo() != null && plugin.getGitInfo().getCurrentBranchType() == BranchType.DEV_BRANCH) {
            return version.replace("-SNAPSHOT", "-" + plugin.getGitInfo().getCurrentBranchName() + "-SNAPSHOT");
        } else {
            return version;
        }
    }
}
