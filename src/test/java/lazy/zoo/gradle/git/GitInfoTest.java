package lazy.zoo.gradle.git;

import lazy.zoo.gradle.DataUtils;
import lazy.zoo.gradle.extension.GitDataPluginExtension;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import static lazy.zoo.gradle.git.BranchType.DEV_BRANCH;
import static lazy.zoo.gradle.git.BranchType.MASTER;
import static lazy.zoo.gradle.git.BranchType.RELEASE_BRANCH;

public class GitInfoTest {
    @Test
    public void testGetGitInfo() {
        Project project = DataUtils.getProject();
        final GitDataPluginExtension ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertNotNull(ext.getBranchType());
        Assert.assertNotNull(ext.getShortBranchName());
        Assert.assertEquals(DEV_BRANCH.name(), ext.getBranchType());
    }

    @Test
    public void testGetGitInfoCorrectBranchType() {
        Project project = DataUtils.getProject();
        String pName = project.getName();

        GitInfo gitInfo = GitInfo.getGitInfo(project,"feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getShortBranchName());
        Assert.assertEquals("feature-branch", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"origin/feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getShortBranchName());
        Assert.assertEquals("origin/feature-branch", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"kind/of/long/path/to/feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getShortBranchName());
        Assert.assertEquals("kind/of/long/path/to/feature-branch", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"master");
        Assert.assertEquals(MASTER, gitInfo.getBranchType());
        Assert.assertEquals("master", gitInfo.getShortBranchName());
        Assert.assertEquals("master", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"origin/master");
        Assert.assertEquals(MASTER, gitInfo.getBranchType());
        Assert.assertEquals("master", gitInfo.getShortBranchName());
        Assert.assertEquals("origin/master", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,pName + "-1.2.X");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(pName + "-1.2.X", gitInfo.getShortBranchName());
        Assert.assertEquals(pName + "-1.2.X", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,pName + "-1.2.0.X");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(pName + "-1.2.0.X", gitInfo.getShortBranchName());
        Assert.assertEquals(pName + "-1.2.0.X", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getShortBranchName());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"release/" + pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getShortBranchName());
        Assert.assertEquals("release/" + pName + "-1.2.0.0", gitInfo.getFullBranchName());

        gitInfo = GitInfo.getGitInfo(project,"release/beta/" + pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getShortBranchName());
        Assert.assertEquals("release/beta/" + pName + "-1.2.0.0", gitInfo.getFullBranchName());
    }
}
