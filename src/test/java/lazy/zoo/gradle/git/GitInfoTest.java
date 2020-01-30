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
        Assert.assertNotNull(ext.getCurrentBranchType());
        Assert.assertNotNull(ext.getCurrentBranchName());
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
    }

    @Test
    public void testGetGitInfoCorrectBranchType() {
        Project project = DataUtils.getProject();
        String pName = project.getName();

        GitInfo gitInfo = GitInfo.getGitInfo(project, project.getName(), "feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getCurrentBranchName());
        Assert.assertEquals("feature-branch", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "origin/feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getCurrentBranchName());
        Assert.assertEquals("origin/feature-branch", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "kind/of/long/path/to/feature-branch");
        Assert.assertEquals(DEV_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals("feature-branch", gitInfo.getCurrentBranchName());
        Assert.assertEquals("kind/of/long/path/to/feature-branch", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "master");
        Assert.assertEquals(MASTER, gitInfo.getCurrentBranchType());
        Assert.assertEquals("master", gitInfo.getCurrentBranchName());
        Assert.assertEquals("master", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "origin/master");
        Assert.assertEquals(MASTER, gitInfo.getCurrentBranchType());
        Assert.assertEquals("master", gitInfo.getCurrentBranchName());
        Assert.assertEquals("origin/master", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), pName + "-1.2.X");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals(pName + "-1.2.X", gitInfo.getCurrentBranchName());
        Assert.assertEquals(pName + "-1.2.X", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), pName + "-1.2.0.X");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals(pName + "-1.2.0.X", gitInfo.getCurrentBranchName());
        Assert.assertEquals(pName + "-1.2.0.X", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getCurrentBranchName());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "release/" + pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getCurrentBranchName());
        Assert.assertEquals("release/" + pName + "-1.2.0.0", gitInfo.getCurrentBranchFullName());

        gitInfo = GitInfo.getGitInfo(project, project.getName(), "release/beta/" + pName + "-1.2.0.0");
        Assert.assertEquals(RELEASE_BRANCH, gitInfo.getCurrentBranchType());
        Assert.assertEquals(pName + "-1.2.0.0", gitInfo.getCurrentBranchName());
        Assert.assertEquals("release/beta/" + pName + "-1.2.0.0", gitInfo.getCurrentBranchFullName());
    }
}
