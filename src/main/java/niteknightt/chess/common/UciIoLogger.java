package niteknightt.chess.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UciIoLogger {
    public static String fileNameStartText = "ucilog_";
    public static String fileNameExtension = ".log";
    public static String fileNameFull = "";

    public static final String INPUT_MARKER = "\"in\"";
    public static final String OUTPUT_MARKER = "\"out\"";

    protected BufferedWriter _fileWriter;
    protected long _currentLogId;
    protected long _currentInputLogId;
    protected long _currentIsReadyLogId;
    protected Enums.LogLevel _logLevel;
    protected Lock _writeLock = new ReentrantLock(true);

    public UciIoLogger(Enums.LogLevel logLevel) {
        _logLevel = logLevel;
        init();
    }

    protected void init() {
        String dateGameStarted = Helpers.getDateForUciLog();
        fileNameFull = Settings.getInstance().getRuntimeDirectory()
                + File.separator
                + Constants.UCI_LOGS_SUBDIR
                + File.separator
                + fileNameStartText
                + dateGameStarted
                + fileNameExtension;
        try {
            _fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open uci log file for writing: " + fileNameFull);
        }

        _currentLogId = Helpers.getNextLogId();
        _currentInputLogId = 0;
    }

    protected void write(Enums.LogLevel level, String gameId, String text, String ioMarker, long referenceLogId) {
        try {
            _writeLock.lock();

            if (level.ordinal() > _logLevel.ordinal()) {
                return;
            }

            StringBuilder sb = new StringBuilder(text.trim());
            for (int i = sb.length() - 1; i >= 0; --i) {
                if (sb.charAt(i) == Common.Log.NEWLINE) {
                    sb.deleteCharAt(i);
                    if (i != sb.length() && i != 0) {
                        sb.insert(i, Common.Log.REPLACEMENT_TEXT_FOR_NEWLINE);
                    }
                }
            }
            if (sb.length() == 0) {
                return;
            }

            StringBuilder lineSb = new StringBuilder()
                    .append(Helpers.formatDateForLog(new Date()))
                    .append(Common.Log.DELIMITER)
                    .append(_currentLogId)
                    .append(Common.Log.DELIMITER)
                    .append(referenceLogId)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(level.toString())
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(gameId)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.DELIMITER)
                    .append(ioMarker)
                    .append(Common.Log.DELIMITER)
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(sb.toString())
                    .append(Common.Log.DOUBLEQUOTE)
                    .append(Common.Log.NEWLINE);

            try {
                _fileWriter.write(lineSb.toString());
                _fileWriter.flush();
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to write to uci log file: " + fileNameFull);
            }

            ++_currentLogId;
        }
        catch (Exception ex) {
            AppLogger.getInstance().error("Exception while writing to log: " + ex.toString());
        }
        finally {
            _writeLock.unlock();
        }
    }

    public void debugInput(String gameId, String text) {
        writeInput(Enums.LogLevel.DEBUG, gameId, text);
    }

    public void infoInput(String gameId, String text) {
        writeInput(Enums.LogLevel.INFO, gameId, text);
    }

    public void warningInput(String gameId, String text) {
        writeInput(Enums.LogLevel.WARNING, gameId, text);
    }

    public void errorInput(String gameId, String text) {
        writeInput(Enums.LogLevel.ERROR, gameId, text);
    }

    public void debugOutput(String gameId, String text) {
        writeOutput(Enums.LogLevel.DEBUG, gameId, text);
    }

    public void infoOutput(String gameId, String text) {
        writeOutput(Enums.LogLevel.INFO, gameId, text);
    }

    public void warningOutput(String gameId, String text) {
        writeOutput(Enums.LogLevel.WARNING, gameId, text);
    }

    public void errorOutput(String gameId, String text) {
        writeOutput(Enums.LogLevel.ERROR, gameId, text);
    }

    public void writeInput(Enums.LogLevel level, String gameId, String text) {
        if (text.equals("isready")) {
            _currentIsReadyLogId = _currentLogId;
        }
        else {
            _currentInputLogId = _currentLogId;
        }
        write(level, gameId, text, INPUT_MARKER, 0);
    }

    public void writeOutput(Enums.LogLevel level, String gameId, String text) {
        long referenceId = 0;
        if (text.equals("readyok")) {
            referenceId = _currentIsReadyLogId;
        }
        else {
            referenceId = _currentInputLogId;
        }
        write(level, gameId, text, OUTPUT_MARKER, referenceId);
    }

    public void close() {
        try {
            _fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to close uci log file: " + fileNameFull);
        }
    }
}
