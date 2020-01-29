package coding.zoo;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GradleGitPluginFunctionalTest {
    @Test
    public void canRunTask() throws IOException {
        // Setup the test build
        File projectDir = new File("build/functionalTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "build.gradle"), getResourceFileAsString("test.build.gradle"));
        writeString(new File(projectDir, "settings.gradle"), getResourceFileAsString("test.settings.gradle"));

        // Run the build
        GradleRunner showGitInfo = GradleRunner.create();
        showGitInfo.forwardOutput();
        showGitInfo.withPluginClasspath();
        showGitInfo.withArguments("gitInfo");
        showGitInfo.withProjectDir(projectDir);
        BuildResult showPluginsResult = showGitInfo.build();
        assertTrue(showPluginsResult.getOutput().contains("currentBranchType"));
        assertTrue(showPluginsResult.getOutput().contains("currentBranchName"));
    }

    private static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    private static void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
