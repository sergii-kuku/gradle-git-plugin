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

    public String getCurrentBranchFullName() {
        return plugin.getGitInfo().getCurrentBranchFullName();
    }

    public String getProjectVersionWithBranch() {
        return parseVersionWithBranch(getCurrentBranchName().replace("/", "-"));
    }

    public String getProjectVersionWithFullBranch() {
        return parseVersionWithBranch(getCurrentBranchFullName().replace("/", "-"));
    }

    private String parseVersionWithBranch(String branchName) {
        String version = plugin.getProjectVersion();
        if (plugin.getGitInfo() != null && plugin.getGitInfo().getCurrentBranchType() == BranchType.DEV_BRANCH) {
            return version.replace("-SNAPSHOT", "-" + branchName + "-SNAPSHOT");
        } else {
            return version;
        }
    }
}
