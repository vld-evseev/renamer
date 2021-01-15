package com.scwot.renamer.core.utils;

import com.scwot.renamer.core.utils.enums.AudioTypes;
import com.scwot.renamer.core.utils.enums.ImageTypes;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirHelper {

    private static final Logger logger = LoggerFactory.getLogger(DirHelper.class);
    private static final Pattern multiDiskPattern =
            Pattern.compile("(cd |cd|cd_|cd-|disc|disc-|disc_|disc )\\d+.*", Pattern.CASE_INSENSITIVE);

    public static DirInfo countFileTypes(File dir) {
        int audioCount = 0;
        int imagesCount = 0;
        int othersCount = 0;

        LinkedList<File> list = (LinkedList<File>)
                FileUtils.listFiles(dir, null, false);

        for (File file : list) {
            String mimeType = FileHelper.getMimeType(file);

            for (AudioTypes audioType : AudioTypes.values()) {
                if (audioType.toString().equals(mimeType)) {
                    audioCount++;
                }
            }

            for (ImageTypes imageType : ImageTypes.values()) {
                if (imageType.toString().equals(mimeType)) {
                    imagesCount++;
                }
            }
        }

        othersCount = list.size() - audioCount - imagesCount;

        logger.debug(String.format("Count complete [%s]", dir.getAbsolutePath()));
        logger.debug(String.format("\taudio: %d", audioCount));
        logger.debug(String.format("\timages: %d", imagesCount));
        logger.debug(String.format("\tothers: %d", othersCount));

        return new DirInfo(dir, audioCount, imagesCount, othersCount);
    }

    /*
       Count subfolders which represents separate CDs (CD1, CD2, etc.)
    */
    public static int getCDFoldersCount(File dir) {
        int cdCount = 0;

        File[] directories = dir.listFiles((current, name) -> new File(current, name).isDirectory());

        if (directories == null){
            final String msg = "Sub-directories not present: " + dir.getAbsolutePath();
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        if (directories.length > 1) {
            for (File directory : directories) {
                Matcher matcher = multiDiskPattern.matcher(directory.getName());
                if (matcher.find()) {
                    cdCount++;
                }
            }
        }

        int correction = directories.length - cdCount + 1;
        if (correction > cdCount) {
            cdCount = 0;
        }
        return cdCount;
    }

    @SneakyThrows(IOException.class)
    public static void deleteDirectory(final File dir) {
        if (!dir.isDirectory()){
            return;
        }

        File[] directories = dir.listFiles();
        if (directories == null){
            final String msg = "Sub-directories not present: " + dir.getAbsolutePath();
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        for (File currentDir : directories) {
            if (currentDir.isDirectory()) {
                deleteDirectory(currentDir);
            }
            FileUtils.deleteDirectory(currentDir);
        }
        if (!dir.delete()) {
            logger.info(String.format("Can't delete folder: %s", dir.getAbsolutePath()));
        }
    }

    public static boolean hasInnerFolder(File dir) {
        Collection<File> folderList = FileUtils.listFilesAndDirs(dir,
                new NotFileFilter(TrueFileFilter.INSTANCE),
                DirectoryFileFilter.DIRECTORY);
        logger.info(String.format("Inner folders: %s", folderList.size()));
        return folderList.size() > 1;
    }

    public  static boolean releaseIsPresent(DirInfo dirInfo) {
        final File dir = dirInfo.getDir();
        return !dirInfo.hasAudio() && DirHelper.getCDFoldersCount(dir) == 0 && !hasInnerFolder(dir);
    }

    public static boolean innerFoldersArePresent(DirInfo dirInfo) {
        final File dir = dirInfo.getDir();
        return !dirInfo.hasAudio() && DirHelper.getCDFoldersCount(dir) == 0 && hasInnerFolder(dir);
    }

}
