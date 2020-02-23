package lazy.zoo.gradle.git;


public class GitInfo {
    private final String inputBranchName;
    private final boolean isValidGitBranch;
    private final BranchType branchType;
    private final String shortBranchName;
    private final String fullBranchName;
    private final String lastCommitHash;
    private final Integer numberOfCommits;

    /**
     * Git Branch data
     *
     * @param inputBranchName  - git branch parameter value if present (-PbranchName). "HEAD" otherwise (points to current checked out branch)
     * @param isValidGitBranch - true if "git rev-parse --verify $inputBranchName" returns with 0 exit code
     * @param branchType       - branch type (one of: MASTER/DEV/RELEASE)
     * @param shortBranchName  - name of the branch, without the grouping/folder prefixes (e.g. some/feature/foobar becomes foobar)
     * @param fullBranchName   - full name of the branch (e.g. some/feature/foobar stays some/feature/foobar)
     * @param lastCommitHash   - short hash of the last commit on the specified branch
     * @param numberOfCommits  - total number of commits on the specified branch
     */
    GitInfo(String inputBranchName, boolean isValidGitBranch, BranchType branchType, String shortBranchName, String fullBranchName, String lastCommitHash, Integer numberOfCommits) {
        this.isValidGitBranch = isValidGitBranch;
        this.inputBranchName = inputBranchName;
        this.branchType = branchType;
        this.shortBranchName = shortBranchName;
        this.fullBranchName = fullBranchName;
        this.lastCommitHash = lastCommitHash;
        this.numberOfCommits = numberOfCommits;
    }

    public String getInputBranchName() {
        return inputBranchName;
    }

    public boolean isValidGitBranch() {
        return isValidGitBranch;
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

    public String getLastCommitHash() {
        return lastCommitHash;
    }

    public Integer getNumberOfCommits() {
        return numberOfCommits;
    }
}
