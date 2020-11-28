package lazy.zoo.gradle.git;

import lazy.zoo.gradle.utils.ExecuteResult;
import lazy.zoo.gradle.utils.ICmdExecutor;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitInfoFactory {
    protected static final String UNIDENTIFIED_BRANCH = "unidentified-git-branch";
    private static final String HEAD = "HEAD";

    private final ICmdExecutor cmdExec;

    private final List<Pattern> staticReleaseBranchPatterns = Arrays.asList(
            Pattern.compile("^([0-9]+\\.[0-9]+\\.[0-9]+)$"),
            Pattern.compile("^([0-9]+\\.[0-9]+)$"),
            Pattern.compile("^([0-9]+)$"),
            Pattern.compile("^v([0-9]+\\.[0-9]+\\.[0-9]+)$"),
            Pattern.compile("^v([0-9]+\\.[0-9]+)$"),
            Pattern.compile("^v([0-9]+)$")
    );

    private final List<String> additionalReleaseBranchPatterns = new ArrayList<>();

    public GitInfoFactory(ICmdExecutor cmdExec) {
        this.cmdExec = cmdExec;
    }

    /**
     * Get git branch information
     *
     * @param project  - project to apply to (using projectDir)
     * @param revParam - if not null - get info for the input branch, otherwise work on current HEAD
     * @return GitInfo object, containing git data. Contains type=DEV_BRANCH and currentBranch=unidentified-git-branch if failed to get proper results
     */
    public GitInfo getGitInfo(Project project, String revParam) {
        final String currentBranchFull = getFullRevName(project, revParam);
        final String currentBranchShort = currentBranchFull.replaceAll("^.*/", "");
        // At this point currentBranchFull contains either of:
        // full branch/tag name, commit hash or unidentified-git-branch (last case implies nulls in the below)
        final String commitId = getShortHash(project, currentBranchFull);
        final List<String> tags = getTags(project, currentBranchFull);
        final Integer numberOfCommits = getNumberOfCommits(project, currentBranchFull);
        BranchType branchType = getBranchType(project.getName(), currentBranchShort);

        project.getLogger().lifecycle("gitData returned with branchType = {}, short rev = {}, full rev = {}, " +
                        "commit hash = {}, tags = {}, number of commits = {}", branchType, currentBranchShort, currentBranchFull, commitId,
                (!tags.isEmpty()) ? String.join(", ", tags) : "N/A", numberOfCommits);
        return new GitInfo(!currentBranchFull.equals(UNIDENTIFIED_BRANCH), revParam, branchType, currentBranchShort, currentBranchFull, commitId, numberOfCommits, tags);
    }

    private boolean isValidRev(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-parse", "--verify", rev),
                er -> true, false);
    }

    private String getAbbrevRef(Project project) {
        return exec(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", HEAD),
                this::parseStdOutAsString, null);
    }

    private String getShortHash(Project project, String rev) {
        return exec(project, Arrays.asList("git", "rev-parse", "--short", rev),
                this::parseStdOutAsString, null);
    }

    private List<String> getTags(Project project, String rev) {
        return exec(project, Arrays.asList("git", "tag", "-l", "--points-at", rev), er ->
                Arrays.stream(er.getStdout().split("\\s+")).filter(v -> !v.isEmpty()).collect(Collectors.toList()), Collections.emptyList());
    }

    private String getFullRevName(Project project, String revParam) {
        String rev = revParam;
        // No parameters imply working on the HEAD
        if (revParam == null || HEAD.equals(revParam)) {
            rev = parseHead(project);
            project.getLogger().lifecycle("parse HEAD returned {}", rev);
        }
        // Skip the below if parseHead did not succeed
        if (rev != null && !rev.isEmpty()) {
            project.getLogger().lifecycle("verifying rev {}", rev);
            // Otherwise go through prefixes to define the correct reference
            // Empty prefix check is to verify the parameter passed in as is (hash, upstream, refs/..., etc.)
            // Empty prefix is checked last to enforce returning prefix if possible even if the branch is checked out locally
            String[] prefixes = {"origin/", "refs/tags/", "",};
            for (String prefix : prefixes) {
                if (isValidRev(project, prefix + rev)) {
                    return prefix + rev;
                }
            }
        }
        project.getLogger().error("Could not determine git branch");
        // correct rev was not found - nothing to return
        return UNIDENTIFIED_BRANCH;
    }

    /**
     * Parse abbrev-ref on the HEAD
     * - if failed - return unidentified-git-branch
     * - if abbrev-ref on HEAD returns HEAD and has tags - return the first tag
     * - if abbrev-ref on HEAD returns HEAD and has no tags - return current commit hash
     * - else return the output of abbrev-ref on HEAD (branch name)
     */
    private String parseHead(Project project) {
        project.getLogger().lifecycle("parsing HEAD");
        final String abbrevRef = getAbbrevRef(project);
        if (abbrevRef == null) {
            // if abbrev-ref failed on HEAD - we cannot identify the branch
            return null;
        } else if (HEAD.equals(abbrevRef)) {
            // if we are on a tag or commit - abbrev-ref on HEAD returns HEAD
            final List<String> tags = getTags(project, HEAD);
            if (tags.isEmpty()) {
                // if no tags found pointing to HEAD - return current commit hash
                project.getLogger().lifecycle("detached HEAD with no tags, returning hash");
                return getShortHash(project, HEAD);
            } else {
                // if there are pointing tags - return the first tag
                project.getLogger().lifecycle("detached HEAD with tags, returning the first tag");
                return tags.get(0);
            }
        } else {
            // otherwise return the name
            return abbrevRef;
        }
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

    public void setAdditionalReleaseBranchPatterns(List<String> additionalReleaseBranchPatterns) {
        if (additionalReleaseBranchPatterns != null) {
            this.additionalReleaseBranchPatterns.clear();
            this.additionalReleaseBranchPatterns.addAll(additionalReleaseBranchPatterns);
        }
    }

    private BranchType getBranchType(String projectName, String shortBranchName) {
        if ("master".equals(shortBranchName)) {
            return BranchType.MASTER;
        } else if (checkReleasePatterns(projectName, shortBranchName)) {
            return BranchType.RELEASE_BRANCH;
        } else {
            return BranchType.DEV_BRANCH;
        }
    }

    private boolean checkReleasePatterns(String projectName, String shortBranchName) {
        return additionalReleaseBranchPatterns.stream().anyMatch(shortBranchName::matches)
                || staticReleaseBranchPatterns.stream().anyMatch(pattern -> pattern.matcher(shortBranchName).matches())
                || shortBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+)\\.[XYZ]")
                || shortBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+)\\.[XYZ]")
                || shortBranchName.matches("(.*)" + projectName + "-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)");
    }

    private String parseStdOutAsString(ExecuteResult er) {
        return er.getStdout().trim().replaceAll("\n", "");
    }

    private <T> T exec(Project project, List<String> command, Function<ExecuteResult, T> executeResultHandler, T valueOnError) {
        final ExecuteResult er = cmdExec.executeCommands(project, command, project.getProjectDir());
        if (er.getExitVal() == 0 && er.getStdout() != null) {
            return executeResultHandler.apply(er);
        } else {
            return valueOnError;
        }
    }
}
