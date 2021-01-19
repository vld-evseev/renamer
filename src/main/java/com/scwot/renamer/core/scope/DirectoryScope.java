package com.scwot.renamer.core.scope;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class DirectoryScope {

    private List<DirectoryScope> children;
    private List<File> listOfInnerFolders;
    private List<Mp3FileScope> listOfAudios;
    private List<File> listOfImages;
    private List<File> listOfOthers;

    private File currentDir;
    private String dirName;
    private int isMultiCD;
    private boolean hasMoreThanOneArtists;
    private boolean isMedium;
    private int diskNumber;

    public DirectoryScope(File inputDir) {
        currentDir = inputDir;

        children = new ArrayList<>();
        listOfInnerFolders = new ArrayList<>();
        listOfAudios = new ArrayList<>();
        listOfImages = new ArrayList<>();
        listOfOthers = new ArrayList<>();
    }

    public void addAudio(Mp3FileScope audio) {
        listOfAudios.add(audio);
    }

    public void addImage(File image) {
        listOfImages.add(image);
    }

    public void addOther(File other) {
        listOfOthers.add(other);
    }

    public boolean hasAudio() {
        return !listOfAudios.isEmpty();
    }

    public boolean hasImages() {
        return !listOfImages.isEmpty();
    }

    public boolean hasOthers() {
        return !listOfOthers.isEmpty();
    }

    public boolean hasInnerFolders() {
        return !listOfInnerFolders.isEmpty();
    }

    public void addChild(DirectoryScope child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current dir: ");
        builder.append(currentDir.getAbsolutePath());
        builder.append(System.lineSeparator());

        builder.append("hasAudio: ");
        builder.append(hasAudio());
        builder.append(", amount: ");
        builder.append(listOfAudios.size());
        builder.append(System.lineSeparator());
        builder.append(printList(listOfAudios));
        builder.append(System.lineSeparator());

        builder.append("hasImages: ");
        builder.append(hasImages());
        builder.append(", amount: ");
        builder.append(listOfImages.size());
        builder.append(System.lineSeparator());
        builder.append(printList(listOfImages));
        builder.append(System.lineSeparator());

        builder.append("hasOthers: ");
        builder.append(hasOthers());
        builder.append(", amount: ");
        builder.append(listOfOthers.size());
        builder.append(System.lineSeparator());
        builder.append(printList(listOfOthers));
        builder.append(System.lineSeparator());

        return builder.toString();
    }

    public String printList(List<?> list) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : list) {
            builder.append("\t");
            builder.append(obj.toString());
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    public boolean isPartOf(DirectoryScope directoryScope) {
        if (currentDir.getAbsolutePath().equalsIgnoreCase(directoryScope.getCurrentDir().getAbsolutePath())) {
            return false;
        }

        return StringUtils.containsIgnoreCase(
                directoryScope.getCurrentDir().getAbsolutePath(),
                currentDir.getAbsolutePath()
        );
    }

}
