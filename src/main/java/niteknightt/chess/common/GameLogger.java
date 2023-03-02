package niteknightt.chess.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameLogger {
    public static String fileNameStartText = "gamelog_";
    public static String fileNameExtension = ".log";
    public static String fileNameFull = "";

    protected BufferedWriter _fileWriter;
    protected long _currentLogId;
    protected long _currentInputLogId;
    protected long _currentIsReadyLogId;
    protected Enums.LogLevel _logLevel;
    protected Lock _writeLock = new ReentrantLock(true);

    public GameLogger(Enums.LogLevel logLevel) {
        _logLevel = logLevel;
        init();
    }

    protected void init() {
        fileNameFull = new StringBuilder()
                .append(Common.RESOURCE_PATH)
                .append(fileNameStartText)
                .append(Helpers.formatDateForFilename(Helpers.appStartDate()))
                .append(fileNameExtension)
                .toString();
        try {
            _fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open game log file for writing: " + fileNameFull);
        }

        _currentLogId = Helpers.getNextLogId();
        _currentInputLogId = 0;
    }

    public void debug(String gameId, String category, String text) {
        write(Enums.LogLevel.DEBUG, gameId, category, text);
    }

    public void info(String gameId, String category, String text) {
        write(Enums.LogLevel.INFO, gameId, category, text);
    }

    public void warning(String gameId, String category, String text) {
        write(Enums.LogLevel.WARNING, gameId, category, text);
    }

    public void error(String gameId, String category, String text) {
        write(Enums.LogLevel.ERROR, gameId, category, text);
    }

    public void write(Enums.LogLevel level, String gameId, String category, String text) {
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
                    .append(gameId)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(category)
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
                throw new RuntimeException("Failed to write to game log file: " + fileNameFull);
            }

            ++_currentLogId;
        }
        catch (Exception ex) {
            System.out.println("ERROR: Exception while writing to log: " + ex.toString());
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
