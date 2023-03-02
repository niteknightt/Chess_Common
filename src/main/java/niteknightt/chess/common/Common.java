package niteknightt.chess.common;

public class Common {
    public static final String RESOURCE_PATH = "resources/";
    public static final Enums.LogLevel UCI_LOG_LEVEL = Enums.LogLevel.DEBUG;
    public static final Enums.LogLevel GAME_LOG_LEVEL = Enums.LogLevel.DEBUG;

    public class Log {
        public static final char DELIMITER = ';';
        public static final char NEWLINE = '\n';
        public static final char DOUBLEQUOTE = '"';
        public static final String REPLACEMENT_TEXT_FOR_NEWLINE = "[newline]";
    }
}
