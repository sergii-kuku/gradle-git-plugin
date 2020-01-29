package coding.zoo.utils;

import org.gradle.api.Project;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class Cmd {
    public static ExecuteResult executeCommands(Project project, List<String> command, File dir) {
        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        String cmdLine = String.join(" ", command);
        try {
            project.getLogger().info("executing " + cmdLine);
            ExecResult result = project.exec(es -> {
                es.commandLine(command);
                es.setStandardOutput(stdoutStream);
                es.setErrorOutput(stderrStream);
                es.setWorkingDir(dir);
            });

            if (result.getExitValue() != 0) {
                project.getLogger().error(cmdLine + " failed");
                project.getLogger().error(stdoutStream.toString());
                project.getLogger().error(stderrStream.toString());
                return new ExecuteResult(255, stdoutStream.toString(), stderrStream.toString());
            }

            return new ExecuteResult(result.getExitValue(), stdoutStream.toString(), stderrStream.toString());
        } catch (ExecException e) {
            project.getLogger().error(cmdLine + " failed");
            project.getLogger().error(stdoutStream.toString());
            project.getLogger().error(stderrStream.toString());
            return new ExecuteResult(255, stdoutStream.toString(), stderrStream.toString());
        }
    }
}
