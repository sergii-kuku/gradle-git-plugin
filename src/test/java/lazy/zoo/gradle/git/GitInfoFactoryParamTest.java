package lazy.zoo.gradle.git;

import lazy.zoo.gradle.DataUtils;
import lazy.zoo.gradle.utils.ExecuteResult;
import lazy.zoo.gradle.utils.ICmdExecutor;
import org.easymock.EasyMock;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static lazy.zoo.gradle.DataUtils.PROJECT_NAME;
import static lazy.zoo.gradle.git.BranchType.DEV_BRANCH;
import static lazy.zoo.gradle.git.BranchType.MASTER;
import static lazy.zoo.gradle.git.BranchType.RELEASE_BRANCH;

@RunWith(Parameterized.class)
public class GitInfoFactoryParamTest {
    private final String SOME_TAG = "some_tag";
    private final String SOME_HASH = "some_hash";
    private final String NB_COMMITS = "128";

    private Project project = DataUtils.getProject();
    private ICmdExecutor cmd = EasyMock.strictMock(ICmdExecutor.class);
    private GitInfoFactory gIF = new GitInfoFactory(cmd);
    ;

    private void okGitExecutionResults(String rev) {
        EasyMock.reset(cmd);
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--verify", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, rev, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-parse", "--short", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, SOME_HASH, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "tag", "-l", "--points-at", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, SOME_TAG, null));
        EasyMock.expect(cmd.executeCommands(project, Arrays.asList("git", "rev-list", "--count", "origin/" + rev), project.getProjectDir()))
                .andReturn(new ExecuteResult(0, NB_COMMITS, null));
        EasyMock.replay(cmd);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return asList(new Object[][]{
                {"feature-branch", DEV_BRANCH, "feature-branch", "origin/feature-branch"},
                {"master", MASTER, "master", "origin/master"},
                {PROJECT_NAME + "-1.2.X", RELEASE_BRANCH, PROJECT_NAME + "-1.2.X", "origin/" + PROJECT_NAME + "-1.2.X"},
        });
    }

    private final String rev;
    private final BranchType expectedType;
    private final String shortName;
    private final String fullName;

    public GitInfoFactoryParamTest(String rev, BranchType expectedType, String shortName, String fullName) {
        this.rev = rev;
        this.expectedType = expectedType;
        this.shortName = shortName;
        this.fullName = fullName;
    }

    /**
     * Mock git cmd execution results to check correct branch parsing
     */
    @Test
    public void testGetGitInfoCorrectBranchType() {
        okGitExecutionResults(rev);
        GitInfo gitInfo = gIF.getGitInfo(project, rev);
        Assert.assertEquals(expectedType, gitInfo.getBranchType());
        Assert.assertEquals(shortName, gitInfo.getShortBranchName());
        Assert.assertEquals(fullName, gitInfo.getFullBranchName());
        Assert.assertEquals(SOME_HASH, gitInfo.getLastCommitHash());
        Assert.assertEquals(SOME_TAG, gitInfo.getTags().get(0));
        Assert.assertEquals(Integer.valueOf(NB_COMMITS), gitInfo.getNumberOfCommits());
    }
}
