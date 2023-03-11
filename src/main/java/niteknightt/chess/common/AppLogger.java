package niteknightt.chess.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Every app should use this as their main logger. Singleton.
 */
public class AppLogger {
    private static AppLogger _instance = null;

    public static String fileNameStartText = "app_";
    public static String fileNameExtension = ".log";
    public static String fileNameFull = "";

    public static Enums.LogLevel setLogLevel = Constants.APP_DEFAULT_LOG_LEVEL;
    public static boolean setAlsoToStdout = true;

    protected BufferedWriter _fileWriter;
    protected long _currentLogId;
    protected long _currentInputLogId;
    protected long _currentIsReadyLogId;
    protected Enums.LogLevel _logLevel;
    protected Lock _writeLock = new ReentrantLock(true);
    protected boolean _alsoToStdout = false;

    public static synchronized AppLogger getInstance() {
        if (_instance == null) {
            _instance = new AppLogger(setLogLevel, setAlsoToStdout);
        }
        return _instance;
    }

    private AppLogger(Enums.LogLevel logLevel, boolean alsoToStdout) {
        _logLevel = logLevel;
        _alsoToStdout = alsoToStdout;
        init();
    }

    protected void init() {
        fileNameFull = System.getenv(Constants.ENV_VAR_RUNTIME_FILE_PATH)
                + File.separator
                + Constants.APP_LOGS_SUBDIR
                + File.separator
                + fileNameStartText
                + Helpers.formatDateForFilename(Helpers.appStartDate())
                + fileNameExtension;
        try {
            _fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open bot log file for writing: " + fileNameFull);
        }

        _currentLogId = Helpers.getNextLogId();
        _currentInputLogId = 0;
    }

    public void debug(String text) {
        write(Enums.LogLevel.DEBUG, text);
    }

    public void info(String text) {
        write(Enums.LogLevel.INFO, text);
    }

    public void warning(String text) {
        write(Enums.LogLevel.WARNING, text);
    }

    public void error(String text) {
        write(Enums.LogLevel.ERROR, text);
    }

    public void write(Enums.LogLevel level, String text) {
        try {
            _writeLock.lock();

            if (level.ordinal() > _logLevel.ordinal()) {
                return;
            }

            StringBuilder lineSb = new StringBuilder()
                    .append(Helpers.formatDateForLog(new Date()))
                    .append(Common.Log.DELIMITER)
                    .append(_currentLogId)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(level.toString())
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(text)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.NEWLINE);

            try {
                _fileWriter.write(lineSb.toString());
                _fileWriter.flush();
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to write to bot log file: " + fileNameFull);
            }

            if (_alsoToStdout) {
                System.out.println(level + ": " + text);
            }

            ++_currentLogId;
        }
        catch (Exception ex) {
            System.out.println("ERROR: Exception while writing to bot log: " + ex.toString());
        }
        finally {
            _writeLock.unlock();
        }
    }

    public void close() {
        try {
            _fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to close game log file: " + fileNameFull);
        }
    }
}
