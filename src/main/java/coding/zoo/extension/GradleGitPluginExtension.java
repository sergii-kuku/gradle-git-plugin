package coding.zoo.extension;

import coding.zoo.GradleGitPlugin;
import coding.zoo.git.BranchType;

public class GradleGitPluginExtension {
    private final GradleGitPlugin plugin;

    public GradleGitPluginExtension(GradleGitPlugin plugin) {
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
