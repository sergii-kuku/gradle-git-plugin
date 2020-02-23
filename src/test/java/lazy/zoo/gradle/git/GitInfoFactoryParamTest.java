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

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return asList(new Object[][]{
                {"feature-branch", DEV_BRANCH, "feature-branch"},
                {"origin/feature-branch", DEV_BRANCH, "feature-branch"},
                {"kind/of/long/path/to/feature-branch", DEV_BRANCH, "feature-branch"},
                {"master", MASTER, "master"},
                {"origin/master", MASTER, "master"},
                {PROJECT_NAME + "-1.2.X", RELEASE_BRANCH, PROJECT_NAME + "-1.2.X"},
                {PROJECT_NAME + "-1.2.0.X", RELEASE_BRANCH, PROJECT_NAME + "-1.2.0.X"},
                {PROJECT_NAME + "-1.2.0.0", RELEASE_BRANCH, PROJECT_NAME + "-1.2.0.0"},
                {"release/" + PROJECT_NAME + "-1.2.0.0", RELEASE_BRANCH, PROJECT_NAME + "-1.2.0.0"},
                {"release/beta/" + PROJECT_NAME + "-1.2.0.0", RELEASE_BRANCH, PROJECT_NAME + "-1.2.0.0"},
        });
    }

    private final String rev;
    private final BranchType expectedType;
    private final String shortName;

    public GitInfoFactoryParamTest(String rev, BranchType expectedType, String shortName) {
        this.rev = rev;
        this.expectedType = expectedType;
        this.shortName = shortName;
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
        Assert.assertEquals(rev, gitInfo.getFullBranchName());
    }
}
