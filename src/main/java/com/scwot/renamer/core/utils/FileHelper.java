package com.scwot.renamer.core.utils;

import com.scwot.renamer.core.enums.AudioTypes;
import com.scwot.renamer.core.enums.ImageTypes;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileHelper {

    public static boolean isAudioFile(File file) {
        final String mimeType = getMimeType(file);
        return file.isFile() && Arrays.stream(AudioTypes.values())
                .anyMatch(audioType -> audioType.toString().equals(mimeType));
    }

    public static boolean isImageFile(File file) {
        final String mimeType = getMimeType(file);
        return file.isFile() && Arrays.stream(ImageTypes.values())
                .anyMatch(imageType -> imageType.toString().equals(mimeType));
    }

    public static boolean isMP3(File file) {
        final String mimeType = getMimeType(file);
        return file.isFile() && AudioTypes.MP3.toString().equals(mimeType);
    }

    public static boolean isMP3(Path path) {
        var file = path.toFile();
        final String mimeType = getMimeType(file);
        return file.isFile() && AudioTypes.MP3.toString().equals(mimeType);
    }

    @SneakyThrows(IOException.class)
    public static String getMimeType(File file) {
        return Files.probeContentType(file.toPath());
    }
}
