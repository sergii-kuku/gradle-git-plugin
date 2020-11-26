package lazy.zoo.gradle.extension;

import lazy.zoo.gradle.GitDataPlugin;

import java.util.List;

public class GitDataPluginExtension {
    private final GitDataPlugin plugin;

    public GitDataPluginExtension(GitDataPlugin plugin) {
        this.plugin = plugin;
    }

    public String getInputBranchName() {
        return plugin.gitInfo().getInputBranchName();
    }

    public boolean isValidGitBranch() {
        return plugin.gitInfo().isValidGitBranch();
    }

    public String getBranchType() {
        return plugin.gitInfo().getBranchType() != null ? plugin.gitInfo().getBranchType().name() : null;
    }

    public String getShortBranchName() {
        return plugin.gitInfo().getShortBranchName();
    }

    public String getFullBranchName() {
        return plugin.gitInfo().getFullBranchName();
    }

    public String getLastCommitHash() {
        return plugin.gitInfo().getLastCommitHash();
    }

    public Integer getNumberOfCommits() {
        return plugin.gitInfo().getNumberOfCommits();
    }

    public List<String> getTags() {
        return plugin.gitInfo().getTags();
    }

    public void setReleaseBranchPatterns(List<String> releaseBranchPatterns) {
        plugin.refreshGitInfo(releaseBranchPatterns);
    }
}
