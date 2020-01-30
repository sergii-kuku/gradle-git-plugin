package lazy.zoo.gradle.git;

import lazy.zoo.gradle.utils.Cmd;
import lazy.zoo.gradle.utils.ExecuteResult;
import org.gradle.api.Project;

import java.util.Arrays;

public class GitInfo {
    private static final String UNIDENTIFIED_BRANCH = "unidentified-git-branch";
    private final BranchType currentBranchType;
    private final String currentBranchName;

    private GitInfo(BranchType currentBranchType, String currentBranchName) {
        this.currentBranchType = currentBranchType;
        this.currentBranchName = currentBranchName;
    }

    public BranchType getCurrentBranchType() {
        return currentBranchType;
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public static GitInfo getGitInfo(Project project, String projectName, String branchName) {
        project.getLogger().info("Getting gitInfo for branch: {}", branchName);
        String currentBranch = UNIDENTIFIED_BRANCH;
        if (branchName == null) {
            ExecuteResult er = Cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", "HEAD"), project.getProjectDir());
            project.getLogger().info("git output:\n{}", er.getStdout());
            if (er.getExitVal() == 0 && er.getStdout() != null && !"HEAD".equals(er.getStdout())) {
                currentBranch = er.getStdout().trim().replaceAll("\n", "");
            }
        } else {
            currentBranch = branchName;
        }

        return parseBranchType(projectName, currentBranch.replaceAll("^.*/", ""));
    }

    private static GitInfo parseBranchType(String projectName, String currentBranch) {
        BranchType branchType;
        if ("master".equals(currentBranch)) {
            branchType = BranchType.MASTER;
        } else if (currentBranch.matches(projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]")
                || currentBranch.matches(projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]")
                || currentBranch.matches(projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)")) {
            branchType = BranchType.RELEASE_BRANCH;
        } else {
            branchType = BranchType.DEV_BRANCH;
        }

        return new GitInfo(branchType, currentBranch);
    }
}
