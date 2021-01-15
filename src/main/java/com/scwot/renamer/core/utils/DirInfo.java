package com.scwot.renamer.core.utils;

import java.io.File;

public class DirInfo {

    private File dir;
    private int audioCount;
    private int imagesCount;
    private int othersCount;

    public DirInfo(File dir, int audioCount, int imagesCount, int othersCount) {
        this.dir = dir;
        this.audioCount = audioCount;
        this.imagesCount = imagesCount;
        this.othersCount = othersCount;
    }

    public File getDir() {
        return dir;
    }

    public int getAudioCount() {
        return audioCount;
    }

    public int getImagesCount() {
        return imagesCount;
    }

    public int getOthersCount() {
        return othersCount;
    }

    public boolean hasAudio() {
        return audioCount > 0;
    }

    public boolean hasImages() {
        return imagesCount > 0;
    }

    public boolean hasOthers() {
        return othersCount > 0;
    }
}
