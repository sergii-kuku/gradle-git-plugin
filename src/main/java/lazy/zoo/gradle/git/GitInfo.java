package lazy.zoo.gradle.git;

import lazy.zoo.gradle.utils.Cmd;
import lazy.zoo.gradle.utils.ExecuteResult;
import org.gradle.api.Project;

import java.util.Arrays;
import java.util.Optional;

public class GitInfo {
    private static final String UNIDENTIFIED_BRANCH = "unidentified-git-branch";

    private final BranchType branchType;
    private final String shortBranchName;
    private final String fullBranchName;
    private final String lastCommitHash;
    private final Integer numberOfCommits;

    private GitInfo(BranchType branchType, String shortBranchName, String fullBranchName, String lastCommitHash, Integer numberOfCommits) {
        this.branchType = branchType;
        this.shortBranchName = shortBranchName;
        this.fullBranchName = fullBranchName;
        this.lastCommitHash = lastCommitHash;
        this.numberOfCommits = numberOfCommits;
    }

    public BranchType getBranchType() {
        return branchType;
    }

    public String getShortBranchName() {
        return shortBranchName;
    }

    public String getFullBranchName() {
        return fullBranchName;
    }

    public Optional<String> getLastCommitHash() {
        return Optional.ofNullable(lastCommitHash);
    }

    public Optional<Integer> getNumberOfCommits() {
        return Optional.ofNullable(numberOfCommits);
    }

    public static GitInfo getGitInfo(Project project, String branchName) {
        String fullBranchName;
        String lastCommitHash;
        Integer numberOfCommits;
        if (branchName == null) {
            fullBranchName = getCurrentBranchName(project);
            lastCommitHash = getCommitHash(project, "HEAD");
            numberOfCommits = getNumberOfCommits(project, "HEAD");
        } else {
            fullBranchName = branchName;
            lastCommitHash = getCommitHash(project, fullBranchName);
            numberOfCommits = getNumberOfCommits(project, lastCommitHash);
        }

        return parseBranchType(project.getName(), fullBranchName.replaceAll("^.*/", ""), fullBranchName, lastCommitHash, numberOfCommits);
    }

    private static GitInfo parseBranchType(String projectName, String shortBranchName, String fullBranchName, String lastCommitHash, Integer numberOfCommits) {
        BranchType branchType;
        if ("master".equals(shortBranchName)) {
            branchType = BranchType.MASTER;
        } else if (shortBranchName.matches(projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]")
                || shortBranchName.matches(projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]")
                || shortBranchName.matches(projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)")) {
            branchType = BranchType.RELEASE_BRANCH;
        } else {
            branchType = BranchType.DEV_BRANCH;
        }

        return new GitInfo(branchType, shortBranchName, fullBranchName, lastCommitHash, numberOfCommits);
    }

    private static String getCurrentBranchName(Project project) {
        String currentBranch = UNIDENTIFIED_BRANCH;
        ExecuteResult er = Cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", "HEAD"), project.getProjectDir());
        if (er.getExitVal() == 0 && er.getStdout() != null && !"HEAD".equals(er.getStdout())) {
            currentBranch = er.getStdout().trim().replaceAll("\n", "");
        }
        return currentBranch;
    }

    private static String getCommitHash(Project project, String branchRev) {
        String commitHash = null;
        if (branchRev != null) {
            ExecuteResult er = Cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", branchRev), project.getProjectDir());
            if (er.getExitVal() == 0 && er.getStdout() != null && !"HEAD".equals(er.getStdout())) {
                commitHash = er.getStdout().trim().replaceAll("\n", "");
            }
        }
        return commitHash;
    }

    private static Integer getNumberOfCommits(Project project, String commitHash) {
        Integer nbCommits = null;
        if (commitHash != null) {
            ExecuteResult er = Cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", commitHash), project.getProjectDir());
            if (er.getExitVal() == 0 && er.getStdout() != null && !"HEAD".equals(er.getStdout())) {
                final String stdOut = er.getStdout().trim().replaceAll("\n", "");
                try {
                    nbCommits = Integer.parseInt(stdOut);
                } catch (NumberFormatException ex) {
                    project.getLogger().error("Unable to parse number of commits git output as int (input='" + stdOut + "')", ex);
                }
            }
        }
        return nbCommits;
    }
}
