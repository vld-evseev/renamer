package com.scwot.renamer.core.io.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
public class ExportFileHelper {

    private static final List<String> EXTENSIONS_RESTRICTED =
            List.of("log", "cue", "db", "m3u", "m3u8", "md5", "sfv", "url", "flp", "fpl", "nfo", "ds_store", "auCDtect", "accurip");

    private static final List<String> FILENAMES_RESTRICTED =
            List.of(".ds_store", "folder.aucdtect", "foo_dr");

    public static void move(File from, File to) {
        try {
            if (from.exists()) {
                if (from.isDirectory() && to.isDirectory()) {
                    FileUtils.copyDirectory(from, to);
                } else if (from.isFile() && to.isDirectory()) {
                    FileUtils.copyFileToDirectory(from, to, true);
                } else {
                    FileUtils.copyFile(from, to);
                }
                log.info("--- Moved: " + from.getAbsolutePath() + " -> " + to.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Can't move from " + from + " To " + to);
        }
    }

    public static void removeJunkFiles(List<File> listOfOtherFiles) {
        Iterator<File> it = listOfOtherFiles.iterator();

        while (it.hasNext()) {
            File next = it.next();
            String extension = getExtension(next.getName()).toLowerCase();
            String basename = FilenameUtils.getBaseName(next.getName()).toLowerCase();
            if (EXTENSIONS_RESTRICTED.contains(extension)) {
                next.delete();
                it.remove();
            } else if (FILENAMES_RESTRICTED.contains(basename)) {
                next.delete();
                it.remove();
            }
        }
    }
}
