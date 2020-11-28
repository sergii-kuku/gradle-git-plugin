package lazy.zoo.gradle.git;

import lazy.zoo.gradle.DataUtils;
import lazy.zoo.gradle.extension.GitDataPluginExtension;
import lazy.zoo.gradle.utils.ExecuteResult;
import lazy.zoo.gradle.utils.ICmdExecutor;
import org.easymock.EasyMock;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static lazy.zoo.gradle.DataUtils.PROJECT_NAME;

public class GitInfoFactoryTest {
    private Project project = DataUtils.getProject();
    private ICmdExecutor cmd = EasyMock.strictMock(ICmdExecutor.class);
    private GitInfoFactory gIF = new GitInfoFactory(cmd);

    private void okGitExecutionResults(String rev) {
        EasyMock.reset(cmd);
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, rev, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, "some_hash", null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "tag", "-l", "--points-at", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, "some_tag", null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, "128", null));
        EasyMock.replay(cmd);
    }

    /**
     * When not on a valid git branch, check isValidGitBranch=false and other fields set to null
     */
    @Test
    public void testInvalidGitInfo() {
        Project project = DataUtils.getProject();
        final GitDataPluginExtension ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertFalse(ext.isValidGitBranch());
        Assert.assertNull(ext.getInputBranchName());
        Assert.assertEquals(BranchType.DEV_BRANCH.name(), ext.getBranchType());
        Assert.assertEquals(GitInfoFactory.UNIDENTIFIED_BRANCH, ext.getShortBranchName());
        Assert.assertEquals(GitInfoFactory.UNIDENTIFIED_BRANCH, ext.getFullBranchName());
        Assert.assertNull(ext.getLastCommitHash());
        Assert.assertNull(ext.getNumberOfCommits());
        Assert.assertEquals(Collections.emptyList(), ext.getTags());
    }

    @Test
    public void testAdditionalReleasePatterns() {
        final String defaultReleaseVersion = PROJECT_NAME + "-1.2.0.0";
        final String additionalReleasePattern = "(.*)foobar(.*)";
        final String additionalReleaseVersion = "release-foobar-X.123";

        // default pattern should work
        assertReleaseBranch(defaultReleaseVersion);
        // new pattern does not work
        assertBranchType(BranchType.DEV_BRANCH, additionalReleaseVersion);
        // set additional release pattern
        gIF.setAdditionalReleaseBranchPatterns(Collections.singletonList(additionalReleasePattern));
        // default pattern still works
        assertReleaseBranch(defaultReleaseVersion);
        // additional pattern works too
        assertReleaseBranch(additionalReleaseVersion);
    }

    @Test
    public void testBranchTypes() {
        final String[] releaseBranches = new String[] {
                PROJECT_NAME + "-1.2.0.0",
                PROJECT_NAME + "-1.2.0.X",
                PROJECT_NAME + "-1.2.X",
                "release-" + PROJECT_NAME + "-1.2.0.0",
                "foo-" + PROJECT_NAME + "-1.2.0.X",
                "impossible_in_short_ver/" + PROJECT_NAME + "-1.2.X",
                "1.1.1", "1.1", "1",
                "v1.1.1", "v1.1", "v1",
        };
        for (String branchName : releaseBranches) {
            assertReleaseBranch(branchName);
        }

        assertBranchType(BranchType.MASTER, "master");
        assertBranchType(BranchType.DEV_BRANCH, "masterx");
    }

    private void assertReleaseBranch(String shortGitRev) {
        assertBranchType(BranchType.RELEASE_BRANCH, shortGitRev);
    }

    private void assertBranchType(BranchType branchType, String shortGitRev) {
        okGitExecutionResults(shortGitRev);
        GitInfo gitInfo = gIF.getGitInfo(project, shortGitRev);
        Assert.assertEquals(branchType, gitInfo.getBranchType());
        Assert.assertTrue(gitInfo.isValidGitBranch());
    }
}
