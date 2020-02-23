package lazy.zoo.gradle.git;

import lazy.zoo.gradle.utils.ExecuteResult;
import lazy.zoo.gradle.utils.ICmdExecutor;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class GitInfoFactory {
    private final ICmdExecutor cmdExec;
    private final List<String> releaseBranchPatterns = new ArrayList<>();

    public GitInfoFactory(ICmdExecutor cmdExec) {
        this.cmdExec = cmdExec;
    }

    /**
     * Get GitInfo for the specified branch
     * If no branch parameter specified, rev is automatically set to HEAD, thus, working on a current checked out branch
     *
     * @param project    - gradle project
     * @param branchName - the name of the branch to get data for, null if requesting data for current branch
     * @return - GitInfo object, containing data for the specified git branch
     */
    public GitInfo getGitInfo(Project project, String branchName) {
        String rev = branchName != null ? branchName : "HEAD";
        if (isValidRev(project, rev)) {
            String fullBranchName = getBranchName(project, rev);
            String shortBranchName = fullBranchName.replaceAll("^.*/", "");
            String lastCommitHash = getCommitHash(project, rev);
            Integer numberOfCommits = getNumberOfCommits(project, rev);
            BranchType branchType = getBranchType(project.getName(), fullBranchName, shortBranchName);
            return new GitInfo(rev, true, branchType, shortBranchName, fullBranchName, lastCommitHash, numberOfCommits);
        } else {
            return new GitInfo(rev, false, null, null, null, null, null);
        }
    }

    public void setReleaseBranchPatterns(List<String> releaseBranchPatterns) {
        if (releaseBranchPatterns != null) {
            this.releaseBranchPatterns.clear();
            this.releaseBranchPatterns.addAll(releaseBranchPatterns);
        }
    }

    private BranchType getBranchType(String projectName, String fullBranchName, String shortBranchName) {
        if ("master".equals(shortBranchName)) {
            return BranchType.MASTER;
        } else if (checkReleasePatterns(projectName, fullBranchName)) {
            return BranchType.RELEASE_BRANCH;
        } else {
            return BranchType.DEV_BRANCH;
        }
    }

    private boolean checkReleasePatterns(String projectName, String fullBranchName) {
        return releaseBranchPatterns.stream().anyMatch(fullBranchName::matches)
                || fullBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]")
                || fullBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]")
                || fullBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)");
    }

    private boolean isValidRev(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-parse", "--verify", rev),
                er -> true, false);
    }

    private String getBranchName(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", rev),
                this::parseStdOutAsString, null);
    }

    private String getCommitHash(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-parse", "--short", rev),
                this::parseStdOutAsString, null);
    }

    private Integer getNumberOfCommits(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-list", "--count", rev), er -> {
            final String stdOut = parseStdOutAsString(er);
            try {
                return Integer.parseInt(stdOut);
            } catch (NumberFormatException ex) {
                project.getLogger().error("Unable to parse number of commits git output as int (input='" + stdOut + "')", ex);
                return null;
            }
        }, null);
    }

    private String parseStdOutAsString(ExecuteResult er) {
        return er.getStdout().trim().replaceAll("\n", "");
    }

    private <T> T exec(Project project, List<String> command, Function<ExecuteResult, T> executeResultHandler, T valueOnError) {
        final ExecuteResult er = cmdExec.executeCommands(project, command, project.getProjectDir());
        if (er.getExitVal() == 0 && er.getStdout() != null && !"HEAD".equals(er.getStdout())) {
            return executeResultHandler.apply(er);
        } else {
            return valueOnError;
        }
    }
}
