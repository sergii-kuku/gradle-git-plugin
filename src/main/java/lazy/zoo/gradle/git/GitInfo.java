package lazy.zoo.gradle.git;


import java.util.List;

public class GitInfo {
    private final boolean isValidGitBranch;
    private final String inputBranchName;
    private final BranchType branchType;
    private final String shortBranchName;
    private final String fullBranchName;
    private final String lastCommitHash;
    private final Integer numberOfCommits;
    private final List<String> tags;

    /**
     * Git Branch data
     *
     * @param isValidGitBranch - if working on a valid git branch or unidentified one
     * @param inputBranchName - git branch parameter value if present (-PbranchName). "HEAD" otherwise (points to current checked out branch)
     * @param branchType      - branch type (one of: MASTER/DEV/RELEASE)
     * @param shortBranchName - name of the branch, without the grouping/folder prefixes (e.g. some/feature/foobar becomes foobar)
     * @param fullBranchName  - full name of the branch (e.g. some/feature/foobar stays some/feature/foobar)
     * @param lastCommitHash  - short hash of the last commit on the specified branch
     * @param numberOfCommits - total number of commits on the specified branch
     * @param tags            - commit tags
     */
    GitInfo(boolean isValidGitBranch, String inputBranchName, BranchType branchType,
            String shortBranchName, String fullBranchName, String lastCommitHash,
            Integer numberOfCommits, List<String> tags) {
        this.isValidGitBranch = isValidGitBranch;
        this.inputBranchName = inputBranchName;
        this.branchType = branchType;
        this.shortBranchName = shortBranchName;
        this.fullBranchName = fullBranchName;
        this.lastCommitHash = lastCommitHash;
        this.numberOfCommits = numberOfCommits;
        this.tags = tags;
    }

    public boolean isValidGitBranch() {
        return isValidGitBranch;
    }

    public String getInputBranchName() {
        return inputBranchName;
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

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "GitInfo{" +
                "inputBranchName='" + inputBranchName + '\'' +
                ", branchType=" + branchType +
                ", shortBranchName='" + shortBranchName + '\'' +
                ", fullBranchName='" + fullBranchName + '\'' +
                ", lastCommitHash='" + lastCommitHash + '\'' +
                ", numberOfCommits=" + numberOfCommits +
                ", tags=" + String.join(",", tags) +
                '}';
    }
}
