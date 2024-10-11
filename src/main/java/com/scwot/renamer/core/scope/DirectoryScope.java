package com.scwot.renamer.core.scope;

import com.scwot.renamer.core.utils.FileHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
public class DirectoryScope {

    private DirectoryScope root;
    private List<DirectoryScope> children;
    private List<File> listOfSubFolders;
    private List<Mp3FileScope> listOfAudios;
    private List<File> listOfImages;
    private List<File> listOfOthers;

    private File currentDir;
    private int isMultiCD;
    private boolean hasMultipleArtists;
    private boolean isMedium;
    private int diskNumber;
    private boolean copied;


    public DirectoryScope(List<Mp3FileScope> listOfAudios,
                          List<File> listOfImages,
                          List<File> listOfOthers) {
        this.listOfAudios = listOfAudios;
        this.listOfImages = listOfImages;
        this.listOfOthers = listOfOthers;
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

    public boolean hasSubFolders() {
        return !listOfSubFolders.isEmpty();
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

    private String printList(List<?> list) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : list) {
            builder.append("\t");
            builder.append(obj.toString());
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

}
