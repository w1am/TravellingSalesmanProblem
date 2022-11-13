package logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom logger class to log outputs to a file.
 */
public class Logger {
    private final String outputFilePath;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    public Logger(String relativePathToFolder, float version, String description) throws IOException {
        this.outputFilePath = System.getProperty("user.dir") + relativePathToFolder + String.format("output-%.1f.log", version);

        RandomAccessFile f = new RandomAccessFile(new File(this.outputFilePath), "rw");
        f.seek(0);
        f.write(
            String.format("Version: %.2f\nDescription: %s\nTimes are displayed in: yyyy-MM-dd HH:mm:ss",
            version,
            description
        ).getBytes(StandardCharsets.UTF_8));
        f.seek(f.length());
        f.write(String.format("\n\n===============[ %s ]=================\n", this.dtf.format(this.now)).getBytes(StandardCharsets.UTF_8));
        f.close();
    }

    public void log(String message, Object value) throws IOException {
        String logMessage = "[" + this.dtf.format(this.now) + "] " + message + ": " + value + "\n";

        Files.writeString(Paths.get(this.outputFilePath), logMessage, StandardOpenOption.APPEND);

        System.out.println(message + ": " + value);
    }

    public void logWithoutMessage(String message, Object value) throws IOException {
        String logMessage = "[" + this.dtf.format(this.now) + "] " + message + ": " + value + "\n";

        Files.writeString(Paths.get(this.outputFilePath), logMessage, StandardOpenOption.APPEND);
    }
}