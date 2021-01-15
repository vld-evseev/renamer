package com.scwot.renamer.core.utils;

import com.scwot.renamer.core.utils.enums.AudioTypes;
import com.scwot.renamer.core.utils.enums.ImageTypes;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class FileHelper {

    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static boolean isAudioFile(File file) {
        final String mimeType = getMimeType(file);
        return Arrays.stream(AudioTypes.values())
                .anyMatch(audioType -> audioType.toString().equals(mimeType));
    }

    public static boolean isImageFile(File file) {
        final String mimeType = getMimeType(file);
        return Arrays.stream(ImageTypes.values())
                .anyMatch(imageType -> imageType.toString().equals(mimeType));
    }

    public static boolean isMP3(File file) {
        final String mimeType = getMimeType(file);
        return AudioTypes.MP3.toString().equals(mimeType);
    }

    @SneakyThrows(IOException.class)
    public static String getMimeType(File file) {
        return Files.probeContentType(file.toPath());
    }
}
