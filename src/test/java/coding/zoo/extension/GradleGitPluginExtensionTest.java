package coding.zoo.extension;

import coding.zoo.DataUtils;
import org.gradle.api.Project;
import org.junit.Assert;
import org.junit.Test;

import static coding.zoo.git.BranchType.DEV_BRANCH;

public class GradleGitPluginExtensionTest {
    @Test
    public void testCorrectVersion() {
        Project project = DataUtils.getProject();
        final GradleGitPluginExtension ext = project.getExtensions().getByType(GradleGitPluginExtension.class);
        Assert.assertEquals(DEV_BRANCH.name(), ext.getCurrentBranchType());
        Assert.assertEquals("unidentified-git-branch", ext.getCurrentBranchName());
        Assert.assertEquals("1.0.0-unidentified-git-branch-SNAPSHOT", ext.getProjectVersionWithBranch());
    }
}
