package lazy.zoo.gradle.utils;

public class ExecuteResult {
    private final int exitVal;
    private final String stdout;
    private final String stderr;

    ExecuteResult(int exitVal, String stdout, String stderr) {
        this.exitVal = exitVal;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int getExitVal() {
        return exitVal;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}
