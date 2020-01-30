package lazy.zoo.gradle.extension;

import lazy.zoo.gradle.DataUtils;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import static lazy.zoo.gradle.git.BranchType.DEV_BRANCH;

public class GitDataPluginExtensionTest {
    @Test
    public void testCorrectVersion() {
        Project project = DataUtils.getProject();
        GitDataPluginExtension ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
        Assert.assertEquals("unidentified-git-branch", ext.getCurrentBranchName());
        Assert.assertEquals("unidentified-git-branch", ext.getCurrentBranchFullName());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getProjectVersionWithBranch());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getProjectVersionWithFullBranch());

        project = DataUtils.getProject("1.1.0");
        ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
        Assert.assertEquals("1.1.0-unidentified-git-branch", ext.getProjectVersionWithBranch());
        Assert.assertEquals("1.1.0-unidentified-git-branch", ext.getProjectVersionWithFullBranch());

        project = DataUtils.getProject("123FOO");
        ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
        Assert.assertEquals("123FOO-unidentified-git-branch", ext.getProjectVersionWithBranch());
        Assert.assertEquals("123FOO-unidentified-git-branch", ext.getProjectVersionWithFullBranch());
    }
}
