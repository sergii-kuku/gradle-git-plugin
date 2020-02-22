package lazy.zoo.gradle.extension;

import lazy.zoo.gradle.GitDataPlugin;
import lazy.zoo.gradle.git.BranchType;

public class GitDataPluginExtension {
    private final GitDataPlugin plugin;


    public GitDataPluginExtension(GitDataPlugin plugin) {
        this.plugin = plugin;
    }

    public String getBranchType() {
        return plugin.getGitInfo().getBranchType().name();
    }

    public String getShortBranchName() {
        return plugin.getGitInfo().getShortBranchName();
    }

    public String getFullBranchName() {
        return plugin.getGitInfo().getFullBranchName();
    }

    public String getLastCommitHash() {
        return plugin.getGitInfo().getLastCommitHash().orElse("");
    }

    public Integer getNumberOfCommits() {
        return plugin.getGitInfo().getNumberOfCommits().orElse(-1);
    }

    public String getVersionWithShortBranchName() {
        return parseVersionWithBranch(getShortBranchName());
    }

    public String getVersionWithFullBranchName() {
        return parseVersionWithBranch(getFullBranchName().replace("/", "-"));
    }

    private String parseVersionWithBranch(String branchName) {
        String version = plugin.getProjectVersion();
        if (plugin.getGitInfo() != null && plugin.getGitInfo().getBranchType() == BranchType.DEV_BRANCH) {
            if (version.endsWith("-SNAPSHOT")) {
                return version.replace("-SNAPSHOT", "-" + branchName + "-SNAPSHOT");
            } else {
                return version + "-" + branchName;
            }
        } else {
            return version;
        }
    }
}
