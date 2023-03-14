package niteknightt.chess.common;

public class Constants {
    public static String ENV_VAR_RUNTIME_FILE_PATH = "NN_RUNTIME_FILE_PATH";

    public static String SETTINGS_SUBDIR = "settings";
    public static String GAME_LOGS_SUBDIR = "gameLogs";
    public static String UCI_LOGS_SUBDIR = "uciLogs";
    public static String APP_LOGS_SUBDIR = "appLogs";
    public static String PERSISTENCE_SUBDIR = "persistence";
    public static String SETTINGS_FILENAME_NITEKIGHTTBOT = "niteknighttbot.conf";
    public static String LAST_LOGID_FILENAME = "lastLogId.txt";
    public static String OPPONENTS_FILENAME = "opponents.json";
    public static Enums.LogLevel APP_DEFAULT_LOG_LEVEL = Enums.LogLevel.DEBUG;

    public static double MIN_VALUE_FOR_VERY_MUCH_BETTER = 3.0;
    public static double MIN_VALUE_FOR_MUCH_BETTER = 2.0;
    public static double MIN_VALUE_FOR_SOMEWHAT_BETTER = 1.0;
    public static double MAX_VALUE_FOR_VERY_MUCH_WORSE = -3.0;
    public static double MAX_VALUE_FOR_MUCH_WORSE = -2.0;
    public static double MAX_VALUE_FOR_SOMEWHAT_WORSE = -1.0;
}
