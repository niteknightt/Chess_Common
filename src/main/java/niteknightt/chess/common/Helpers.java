package niteknightt.chess.common;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Helpers {
    private static long lastLogId;
    private static boolean lastLogIdLoaded = false;
    private static Lock logIdLock = new ReentrantLock(true);
    private static Date appStartDate = new Date();

    public static long LOG_ID_INCREMENT = 100000l;
    public static SimpleDateFormat formatForFilename = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat formatForLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static String fileNameFull = "";
    private static boolean logInitFlag = false;
    private static Stack<String> _gameDates = new Stack<>();
    private static Stack<String> _uciDates = new Stack<>();
    private static Lock gameDateLock = new ReentrantLock(true);
    private static Date gameStartDate = null;

    public static void initLog() {
        try {
            logIdLock.lock();
            if (!lastLogIdLoaded) {
                loadLastLogId();
                lastLogIdLoaded = true;
            }
        }
        finally {
            logInitFlag = true;
            logIdLock.unlock();
        }
    }

    public static Date appStartDate() { return appStartDate; }

    private static void loadLastLogId() {
        if (fileNameFull.length() == 0) {
            fileNameFull = Settings.getInstance().getRuntimeDirectory()
                    + File.separator
                    + Constants.PERSISTENCE_SUBDIR
                    + File.separator
                    + Constants.LAST_LOGID_FILENAME;
        }
        Path filePath = Paths.get(fileNameFull);
        File file = filePath.toFile();

        if (!file.exists()) {
            lastLogId = 1;
            saveLastLogId();
            return;
        }

        if (!file.isFile()) {
            throw new RuntimeException("Last log ID file is not a file: " + fileNameFull);
        }

        try {
            var fileReader = new BufferedReader(new FileReader(fileNameFull));
            lastLogId = Long.parseLong(fileReader.readLine());
            fileReader.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading last log ID: " + fileNameFull);
        }
    }

    private static void saveLastLogId() {
        try {
            var fileWriter = new BufferedWriter(new FileWriter(fileNameFull));
            fileWriter.write(Long.toString(lastLogId));
            fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while saving last log ID: " + fileNameFull);
        }
    }

    public static String formatDateForFilename(Date date) {
        return formatForFilename.format(date);
    }

    public static String formatDateForLog(Date date) {
        return formatForLog.format(date);
    }

    public static long getNextLogId() {
        try {
            if (!logInitFlag) {
                initLog();
            }
            logIdLock.lock();
            lastLogId += LOG_ID_INCREMENT;
            saveLastLogId();
            return lastLogId;
        }
        finally {
            logIdLock.unlock();
        }
    }

    public static Enums.PieceType letterToPieceType(char letter) {
        switch (letter) {
            case 'N':
                return Enums.PieceType.KNIGHT;
            case 'B':
                return Enums.PieceType.BISHOP;
            case 'R':
                return Enums.PieceType.ROOK;
            case 'Q':
                return Enums.PieceType.QUEEN;
            case 'K':
                return Enums.PieceType.KING;
            default:
                throw new RuntimeException("Failed to find piece type for letter " + letter);
        }
    }

    public static boolean gameDateAlreadyUsed(String proposedDateStr) {
        int pos = 0;
        while (pos < _gameDates.size()) {
            String curDate = _gameDates.elementAt(pos);
            int compareResult = curDate.compareTo(proposedDateStr);
            if (compareResult == 0) {
                return true;
            }
            else if (compareResult < 0) {
                break;
            }
            else {
                AppLogger.getInstance().error("Found game date later than proposed date (" + curDate + " > " + proposedDateStr + ")");
                ++pos;
            }
        }
        return false;
    }

    public static boolean uciDateAlreadyUsed(String proposedDateStr) {
        int pos = 0;
        while (pos < _uciDates.size()) {
            String curDate = _uciDates.elementAt(pos);
            int compareResult = curDate.compareTo(proposedDateStr);
            if (compareResult == 0) {
                return true;
            }
            else if (compareResult < 0) {
                break;
            }
            else {
                AppLogger.getInstance().error("Found uci date later than proposed date (" + curDate + " > " + proposedDateStr + ")");
                ++pos;
            }
        }
        return false;
    }

    public static void setGameStartDate() {
        gameStartDate = new Date();
    }

    public static String getDateForGameLog() {
        if (gameStartDate == null) {
            AppLogger.getInstance().error("Game start date was not set");
            gameStartDate = new Date();
        }
        Date proposedDate = gameStartDate;
        String proposedDateStr = formatDateForFilename(proposedDate);
        try {
            gameDateLock.lock();
            while (gameDateAlreadyUsed(proposedDateStr)) {
                proposedDate = new Date(proposedDate.getTime() + 1000);
                proposedDateStr = formatDateForFilename(proposedDate);
            }
            _gameDates.push(proposedDateStr);
        }
        finally {
            gameDateLock.unlock();
        }
        return proposedDateStr;
    }

    public static String getDateForUciLog() {
        if (gameStartDate == null) {
            AppLogger.getInstance().error("Uci start date was not set");
            gameStartDate = new Date();
        }
        Date proposedDate = gameStartDate;
        String proposedDateStr = formatDateForFilename(proposedDate);
        try {
            gameDateLock.lock();
            while (uciDateAlreadyUsed(proposedDateStr)) {
                proposedDate = new Date(proposedDate.getTime() + 1000);
                proposedDateStr = formatDateForFilename(proposedDate);
            }
            _uciDates.push(proposedDateStr);
        }
        finally {
            gameDateLock.unlock();
        }
        return proposedDateStr;
    }

    public static Enums.MoveEvalCategory categoryFromEval(double eval, Enums.Color playerColor) {
        double adjEval = (playerColor == Enums.Color.BLACK) ? eval * -1.0 : eval;

        if (adjEval >= Constants.MIN_VALUE_FOR_WINNING) {
            return Enums.MoveEvalCategory.WINNING;
        }
        else if (adjEval >= Constants.MIN_VALUE_FOR_WELL_AHEAD) {
            return Enums.MoveEvalCategory.WELL_AHEAD;
        }
        else if (adjEval >= Constants.MIN_VALUE_FOR_LEADING) {
            return Enums.MoveEvalCategory.LEADING;
        }
        else if (adjEval >= Constants.MAX_VALUE_FOR_LAGGING) {
            return Enums.MoveEvalCategory.EQUAL;
        }
        else if (adjEval >= Constants.MAX_VALUE_FOR_WELL_BEHIND) {
            return Enums.MoveEvalCategory.LAGGING;
        }
        else if (adjEval >= Constants.MAX_VALUE_FOR_LOSING) {
            return Enums.MoveEvalCategory.WELL_BEHIND;
        }
        else {
            return Enums.MoveEvalCategory.LOSING;
        }
    }
}
