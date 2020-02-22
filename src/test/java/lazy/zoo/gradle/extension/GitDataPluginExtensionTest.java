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
        Assert.assertEquals(DEV_BRANCH.name(), ext.getBranchType());
        Assert.assertEquals("unidentified-git-branch", ext.getShortBranchName());
        Assert.assertEquals("unidentified-git-branch", ext.getFullBranchName());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getVersionWithShortBranchName());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getVersionWithFullBranchName());

        project = DataUtils.getProject("1.1.0");
        ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getBranchType());
        Assert.assertEquals("1.1.0-unidentified-git-branch", ext.getVersionWithShortBranchName());
        Assert.assertEquals("1.1.0-unidentified-git-branch", ext.getVersionWithFullBranchName());

        project = DataUtils.getProject("123FOO");
        ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getBranchType());
        Assert.assertEquals("123FOO-unidentified-git-branch", ext.getVersionWithShortBranchName());
        Assert.assertEquals("123FOO-unidentified-git-branch", ext.getVersionWithFullBranchName());
    }
}
