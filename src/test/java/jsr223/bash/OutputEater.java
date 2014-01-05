package jsr223.bash;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class OutputEater extends ExternalResource {

    private File outputFile;
    private File errFile;
    private TemporaryFolder tmpFolder = new TemporaryFolder();

    @Override
    protected void before() throws Throwable {
        tmpFolder.create();
        outputFile = tmpFolder.newFile();
        errFile = tmpFolder.newFile();
        Bash.redirectOutput = ProcessBuilder.Redirect.appendTo(outputFile);
        Bash.redirectErr = ProcessBuilder.Redirect.appendTo(errFile);
    }

    @Override
    protected void after() {
        Bash.redirectOutput = ProcessBuilder.Redirect.INHERIT;
        Bash.redirectErr = ProcessBuilder.Redirect.INHERIT;
        tmpFolder.delete();
    }

    public String getOut() throws IOException {
        return FileUtils.readFileToString(outputFile);
    }

    public String getErr() throws IOException {
        return FileUtils.readFileToString(errFile);
    }
}
