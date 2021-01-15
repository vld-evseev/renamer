package com.scwot.renamer.core.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirHelper {

    private int audioCount = 0;
    private int imagesCount = 0;
    private int othersCount = 0;

    private static final Logger logger = LoggerFactory.getLogger(DirHelper.class);

    /*
       File types counter
    */

    public void countFileTypes(File dir) {
        LinkedList<File> list = (LinkedList) FileUtils.listFiles(dir, null, false);
        try {
            for (File file : list) {
                String mimeType = Files.probeContentType(file.toPath());

                for (AudioTypes audioType : AudioTypes.values()) {
                    if (audioType.toString().equals(mimeType)) {
                        audioCount = getAudioCount() + 1;
                    }
                }

                for (ImageTypes imageType : ImageTypes.values()) {
                    if (imageType.toString().equals(mimeType)) {
                        imagesCount = getImagesCount() + 1;
                    }
                }

                //System.out.println(file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        othersCount = list.size() - getAudioCount() - getImagesCount();

        //System.out.println(dir.getAbsolutePath() + ": has ");
        //System.out.println("\t" + getAudioCount() + " audio");
        //System.out.println("\t" + getImagesCount() + " images");
        //System.out.println("\t" + getOthersCount() + " others");

    }

    /*
       Count subfolders which represents separate CDs (CD1, CD2, etc.)
    */
    public static int getCDFoldersCount(File dir) {
        int count = 0;

        Pattern p = Pattern.compile("(cd |cd|cd_|cd-|disc|disc-|disc_|disc )\\d+.*", Pattern.CASE_INSENSITIVE);

        File[] directories = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        if (directories.length > 1) {
            for (File directory : directories) {
                Matcher m = p.matcher(directory.getName());
                if (m.find()) {
                    count++;
                }
            }
        }

        int correction = directories.length - count + 1;
        if (correction > count) {
            count = 0;
        }
        return count;
    }

    /*
       deletes unnecessary junk folders
    */
    public static void deleteDirectory(final File dir) {
        // check if folder file is a real folder
        if (dir.isDirectory()) {
            File[] list = dir.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File currentDir = list[i];
                    if (currentDir.isDirectory()) {
                        deleteDirectory(currentDir);
                    }
                    currentDir.delete();
                }
            }
            if (!dir.delete()) {
                System.out.println("can't delete folder : " + dir);
            }
        }
    }

    /*
        If current folder has subfolders
     */
    public boolean hasInnerFolder(File dir) {
        Collection<File> folderList = FileUtils.listFilesAndDirs(dir,
                new NotFileFilter(TrueFileFilter.INSTANCE),
                DirectoryFileFilter.DIRECTORY);
        //System.out.println("Inner folders count: " + folderList.size());
        return folderList.size() > 1;
    }

    public boolean doesNotContainRelease(File dir) {
        return !hasAudio() && DirHelper.getCDFoldersCount(dir) == 0 && !hasInnerFolder(dir);
    }

    public boolean containsJustInnerFolders(File dir) {
        return !hasAudio() && DirHelper.getCDFoldersCount(dir) == 0 && hasInnerFolder(dir);
    }

    public static boolean isAudioFile(File file) {
        boolean value = false;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            for (AudioTypes audioType : AudioTypes.values()) {
                if (audioType.toString().equals(mimeType)) {
                    value = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean isImageFile(File file) {
        boolean value = false;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            for (ImageTypes imageType : ImageTypes.values()) {
                if (imageType.toString().equals(mimeType)) {
                    value = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean isMP3(File file) {
        boolean res = false;
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (AudioTypes.MP3.toString().equals(mimeType)) {
                res = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /*
        if folder contains some audio
     */
    public boolean hasAudio() {
        return audioCount > 0;
    }

    /*
        if folder contains images
     */
    public boolean hasImages() {
        return imagesCount > 0;
    }

    /*
        if folder contain other file types
    */
    public boolean hasOthers() {
        return othersCount > 0;
    }

    /*
        count of audio files in folder
    */
    public int getAudioCount() {
        return audioCount;
    }

    /*
        count of images in folder
    */
    public int getImagesCount() {
        return imagesCount;
    }

    /*
        count of other file types in folder
    */
    public int getOthersCount() {
        return othersCount;
    }

    /*
        represents audio Mime types
    */
    private enum AudioTypes {
        MP3 {
            public String toString() {
                return "audio/mpeg";
            }
        }
    }

    /*
        represents image Mime types
    */
    private enum ImageTypes {
        JPG {
            public String toString() {
                return "image/jpeg";
            }
        },

        PNG {
            public String toString() {
                return "image/png";
            }
        },

        GIF {
            public String toString() {
                return "image/gif";
            }
        }
    }

    /*
        represents other Mime types - maybe for future purposes
    */
    private enum OtherTypes {
        TXT {
            public String toString() {
                return "text/plain";
            }
        },

        M3U {
            public String toString() {
                return "audio/x-mpegurl";
            }
        },

        NULL {
            public String toString() {
                return "null";
            }
        }
    }

}
