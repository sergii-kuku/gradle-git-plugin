package lazy.zoo.gradle.extension;

import lazy.zoo.gradle.DataUtils;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import static lazy.zoo.gradle.git.BranchType.DEV_BRANCH;

public class GradleGitPluginExtensionTest {
    @Test
    public void testCorrectVersion() {
        Project project = DataUtils.getProject();
        final GitDataPluginExtension ext = project.getExtensions().getByType(GitDataPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
        Assert.assertEquals("unidentified-git-branch", ext.getCurrentBranchName());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getProjectVersionWithBranch());
    }
}
