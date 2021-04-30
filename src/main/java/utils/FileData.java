package utils;

import java.nio.file.Paths;

public enum FileData {
    ABSOLUT_INPUT_FILEPATH(Paths.get(System.getProperty("user.dir"), "src", "main","resources", "data").toString()),
    ABSOLUT_OUTPUT_FILEPATH(Paths.get(System.getProperty("user.dir"), "src", "main","resources", "output").toString()),
    INPUT_FILEPATH(Paths.get("src", "main","resources", "data").toString()),
    OUTPUT_FILEPATH(Paths.get("src", "main","resources", "output").toString()),
    FILE_EN(INPUT_FILEPATH.getFilePath() + "/alice-en.txt"),
    FILE_RU(INPUT_FILEPATH.getFilePath() + "/alice-ru.txt"),
    FILE_UA(INPUT_FILEPATH.getFilePath() + "/alice-ua.txt"),
    FILE_CH(INPUT_FILEPATH.getFilePath() + "/alice-ch.txt"),
    FILE_AR(INPUT_FILEPATH.getFilePath() + "/alice-ar.txt"),
    FILE_MY_NAME(INPUT_FILEPATH.getFilePath() + "/MyName.txt"),
    FILE_MY_NAME_AND_INFO(INPUT_FILEPATH.getFilePath() + "/MyNameAndInfo.txt");

    private final String filePath;

    public String getFilePath() {
        return filePath;
    }

    FileData(String filePath) {
        this.filePath = filePath;
    }
}
