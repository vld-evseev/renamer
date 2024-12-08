package com.scwot.renamer.core.utils;

import com.scwot.renamer.core.scope.Mp3FileScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExportFileHelper {

    private static final Pattern VA_PATTERN = Pattern.compile("^[A-D]?\\d{1,2} - [^-]+ - .+\\..*$");
    private static final Pattern COMMON_PATTERN = Pattern.compile("^[A-D]?\\d{1,2} - .+\\..*$");

    private static final List<String> EXTENSIONS_RESTRICTED =
            List.of(
                    "log",
                    "cue",
                    "db",
                    "m3u",
                    "m3u8",
                    "md5",
                    "sfv",
                    "url",
                    "flp",
                    "fpl",
                    "nfo",
                    "ds_store",
                    "aucdtect",
                    "accurip",
                    "summary"
            );

    private static final List<String> FILE_BASE_NAMES_RESTRICTED =
            List.of(
                    "folder.aucdtect",
                    ".ds_store",
                    "foo_dr"
            );

    private static final List<String> FILE_FULL_NAMES_RESTRICTED =
            List.of("sacd_log.txt");

    public static void move(File from, File to) {
        try {
            if (!from.exists()) {
                log.warn("Source file does not exist: " + from.getAbsolutePath());
                return;
            }

            if (from.isDirectory() && to.isDirectory()) {
                FileUtils.copyDirectory(from, to);
            } else if (from.isFile() && to.isDirectory()) {
                FileUtils.copyFileToDirectory(from, to, true);
            } else {
                FileUtils.copyFile(from, to);
            }

            log.info("Moved: " + from.getAbsolutePath() + " -> " + to.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to move " + from.getAbsolutePath() + " to " + to.getAbsolutePath(), e);
        }
    }

    public static File updateNameIfNeeded(Mp3FileScope audio, File dest, boolean isVA, boolean multiCD) {
        String baseName = FilenameUtils.getBaseName(audio.getAudioFile().getFile().getName());
        Pattern pattern = isVA ? VA_PATTERN : COMMON_PATTERN;
        Matcher matcher = pattern.matcher(baseName);

        if (matcher.matches()) {
            return new File(dest, audio.getAudioFile().getFile().getName());
        }

        String trackNumber = String.format("%02d", Integer.parseInt(audio.getTrackNumber()));
        String cdNumber = multiCD ? audio.getDiscNumber() + "-" : "";

        StringBuilder modifiedName = new StringBuilder(cdNumber);
        modifiedName.append(trackNumber).append(" - ");

        if (isVA) {
            modifiedName.append(ExportRenameUtils.normalizeName(audio.getArtistTitle())).append(" - ");
        }

        modifiedName.append(ExportRenameUtils.normalizeName(audio.getTrackTitle()))
                .append(".")
                .append(FilenameUtils.getExtension(audio.getAudioFile().getFile().getName()));

        return new File(dest, modifiedName.toString());
    }


    public static void removeJunkFiles(List<File> listOfOtherFiles) {
        listOfOtherFiles.removeIf(file -> {
            String name = file.getName().toLowerCase();
            String extension = FilenameUtils.getExtension(name).toLowerCase();
            String basename = FilenameUtils.getBaseName(name).toLowerCase();

            if (EXTENSIONS_RESTRICTED.contains(extension)
                    || FILE_BASE_NAMES_RESTRICTED.contains(basename)
                    || FILE_FULL_NAMES_RESTRICTED.contains(name)) {
                //file.delete();
                return true;
            }
            return false;
        });
    }

}
