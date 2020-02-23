package lazy.zoo.gradle.utils;

import org.gradle.api.Project;

import java.io.File;
import java.util.List;

public interface ICmdExecutor {
    ExecuteResult executeCommands(Project project, List<String> command, File dir);
}
