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
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, rev, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, rev, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, "some_hash", null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", rev), project.getProjectDir()))
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
        Assert.assertEquals("HEAD", ext.getInputBranchName());
        Assert.assertFalse(ext.isValidGitBranch());

        Assert.assertNull(ext.getBranchType());
        Assert.assertNull(ext.getShortBranchName());
        Assert.assertNull(ext.getFullBranchName());
        Assert.assertNull(ext.getLastCommitHash());
        Assert.assertNull(ext.getNumberOfCommits());
    }

    @Test
    public void testReleaseBranchPatterns() {
        final String defaultReleaseVersion = "release/beta/" + PROJECT_NAME + "-1.2.0.0";
        final String additionalReleasePattern = "(.*)foobar(.*)";
        final String additionalReleaseVersion = "rel/foobar-X.123";

        // default pattern should work
        okGitExecutionResults(defaultReleaseVersion);
        GitInfo gitInfo = gIF.getGitInfo(project, defaultReleaseVersion);
        Assert.assertEquals(BranchType.RELEASE_BRANCH, gitInfo.getBranchType());

        // new pattern does not work
        okGitExecutionResults(additionalReleaseVersion);
        gitInfo = gIF.getGitInfo(project, additionalReleaseVersion);
        Assert.assertEquals(BranchType.DEV_BRANCH, gitInfo.getBranchType());

        // set additional release pattern
        gIF.setReleaseBranchPatterns(Collections.singletonList(additionalReleasePattern));

        // default pattern still works
        okGitExecutionResults(defaultReleaseVersion);
        gitInfo = gIF.getGitInfo(project, defaultReleaseVersion);
        Assert.assertEquals(BranchType.RELEASE_BRANCH, gitInfo.getBranchType());

        // additional pattern works too
        okGitExecutionResults(additionalReleaseVersion);
        gitInfo = gIF.getGitInfo(project, additionalReleaseVersion);
        Assert.assertEquals(BranchType.RELEASE_BRANCH, gitInfo.getBranchType());
    }
}
