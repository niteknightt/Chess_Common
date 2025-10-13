package niteknightt.chess.common;

import org.junit.jupiter.api.Test;

public class AppLoggerTest {
    @Test
    public void testAppLogger() {
        Settings.createInstance(Enums.SettingsType.COMMON);
        AppLogger.createInstance(Enums.SettingsType.COMMON, Enums.LogLevel.DEBUG, false);
        AppLogger appLogger = AppLogger.getInstance();
    }
}
