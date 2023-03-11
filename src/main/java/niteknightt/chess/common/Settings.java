package niteknightt.chess.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Settings {
    private static Settings _instance = null;

    private Enums.SettingsType _settingsType = Enums.SettingsType.NONE;
    private HashMap<String, String> _settings;

    public static synchronized Settings getInstance(Enums.SettingsType settingsType) {
        if (_instance == null) {
            _instance = new Settings(settingsType);
        }
        return _instance;
    }

    private Settings(Enums.SettingsType settingsType) {
        _settingsType = settingsType;
        loadFromFile();
    }

    private void loadFromFile() {
        String fileName = "";
        if (_settingsType == Enums.SettingsType.NITEKNIGHTTBOT) {
            fileName = Constants.SETTINGS_FILENAME_NITEKIGHTTBOT;
        }

        if (fileName.length() == 0) {
            throw new RuntimeException("No settings file defined for app: " + _settingsType);
        }

        String fullFileName = System.getenv(Constants.ENV_VAR_RUNTIME_FILE_PATH)
                                + File.separator
                                + Constants.SETTINGS_SUBDIR
                                + File.separator
                                + fileName;
        Path filePath = Paths.get(fullFileName);
        File file = filePath.toFile();
        if (!file.exists()) {
            throw new RuntimeException("Settings file does not exist: " + fullFileName);
        }

        if (!file.isFile()) {
            throw new RuntimeException("Settings file is not a file: " + fullFileName);
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            int lineNo = 1;
            for (String line : lines) {
                int pos = line.indexOf("=");
                if (pos == -1) {
                    throw new RuntimeException("Failed to parse line " + lineNo + " of setting file " + fullFileName + ": No equals sign found");
                }
                String key = line.substring(0, pos).trim();
                String val = line.substring(pos + 1).trim();
                addSetting(key, val);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading settings file: " + fullFileName);
        }
    }

    public void addSetting(String key, String val) {
        _settings.put(key, val);
    }

    public String getSetting(String key) {
        if (!_settings.containsKey(key)) {
            return null;
        }
        return _settings.get(key);
    }

}
