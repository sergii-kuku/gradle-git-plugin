package lazy.zoo.gradle.git;

import lazy.zoo.gradle.DataUtils;
import lazy.zoo.gradle.utils.ExecuteResult;
import lazy.zoo.gradle.utils.ICmdExecutor;
import org.easymock.EasyMock;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class GitInfoFactoryHeadTest {
    private final String SOME_HASH = "some_hash";
    private final String SOME_TAG = "some_tag";
    private final String NB_COMMITS = "128";

    private Project project = DataUtils.getProject();
    private ICmdExecutor cmd = EasyMock.strictMock(ICmdExecutor.class);
    private GitInfoFactory gIF = new GitInfoFactory(cmd);

    private void okGitExecutionResults(String rev, boolean isDetached) {
        EasyMock.reset(cmd);
        if (isDetached) {
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", "HEAD"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, "HEAD", null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "tag", "-l", "--points-at", "HEAD"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, SOME_TAG, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", "origin/some_tag"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(1, null, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", "refs/tags/some_tag"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, rev, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", "refs/tags/some_tag"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, SOME_HASH, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "tag", "-l", "--points-at", "refs/tags/some_tag"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, SOME_TAG, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", "refs/tags/some_tag"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, NB_COMMITS, null));
        } else {
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--abbrev-ref", "HEAD"), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, rev, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", "origin/" + rev), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, rev, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", "origin/" + rev), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, SOME_HASH, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "tag", "-l", "--points-at", "origin/" + rev), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, SOME_TAG, null));
            EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", "origin/" + rev), project.getProjectDir()))
                    .andReturn(new ExecuteResult(0, NB_COMMITS, null));
        }
        EasyMock.replay(cmd);
    }

    @Test
    public void testParseHead() {
        // non-detached head
        final String shortGitRev = "some-branch";
        okGitExecutionResults(shortGitRev, false);
        GitInfo gitInfo = gIF.getGitInfo(project, null);
        Assert.assertTrue(gitInfo.isValidGitBranch());
        Assert.assertEquals(BranchType.DEV_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(shortGitRev, gitInfo.getShortBranchName());
        Assert.assertEquals("origin/" + shortGitRev, gitInfo.getFullBranchName());
        Assert.assertEquals(SOME_HASH, gitInfo.getLastCommitHash());
        Assert.assertEquals(SOME_TAG, gitInfo.getTags().get(0));
        Assert.assertEquals(Integer.valueOf(NB_COMMITS), gitInfo.getNumberOfCommits());

        // detached head with tags
        okGitExecutionResults("ignored", true);
        gitInfo = gIF.getGitInfo(project, null);
        Assert.assertTrue(gitInfo.isValidGitBranch());
        Assert.assertEquals(BranchType.DEV_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(SOME_TAG, gitInfo.getShortBranchName());
        Assert.assertEquals("refs/tags/" + SOME_TAG, gitInfo.getFullBranchName());
        Assert.assertEquals(SOME_HASH, gitInfo.getLastCommitHash());
        Assert.assertEquals(SOME_TAG, gitInfo.getTags().get(0));
        Assert.assertEquals(Integer.valueOf(NB_COMMITS), gitInfo.getNumberOfCommits());
    }
}
