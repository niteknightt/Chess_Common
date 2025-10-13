package niteknightt.chess.common;

public class Constants {
    public static String ENV_VAR_PREFIX = "NN_";
    public static String ENV_VAR_RUNTIME_FILE_PATH_SUFFIX = "RUNTIME_FILE_PATH";
    public static String ENV_VAR_APP_NAME_SUFFIX = "_APP_NAME";
    public static String ENV_VAR_ACCESS_TOKEN_SUFFIX = "_ACCESS_TOKEN";

    public static String SETTINGS_SUBDIR = "settings";
    public static String GAME_LOGS_SUBDIR = "gameLogs";
    public static String UCI_LOGS_SUBDIR = "ucilogs";
    public static String APP_LOGS_SUBDIR = "applogs";
    public static String PERSISTENCE_SUBDIR = "persistence";
    public static String SETTINGS_FILENAME_COMMON = "common.conf";
    public static String SETTINGS_FILENAME_NITEKIGHTTBOT = "niteknighttbot.conf";
    public static String SETTINGS_FILENAME_BOTTERBOT = "botterbot.conf";
    public static String SETTINGS_FILENAME_USERSTATS = "userstats.conf";
    public static String LAST_LOGID_FILENAME = "lastLogId.txt";
    public static String OPPONENTS_FILENAME = "opponents.json";
    public static Enums.LogLevel APP_DEFAULT_LOG_LEVEL = Enums.LogLevel.DEBUG;

    public static double MIN_VALUE_FOR_WINNING = 3.0;
    public static double MIN_VALUE_FOR_WELL_AHEAD = 2.0;
    public static double MIN_VALUE_FOR_LEADING = 1.0;
    public static double MAX_VALUE_FOR_LAGGING = -1.0;
    public static double MAX_VALUE_FOR_WELL_BEHIND = -2.0;
    public static double MAX_VALUE_FOR_LOSING = -3.0;
}
