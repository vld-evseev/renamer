package com.scwot.renamer.core;

import com.scwot.renamer.core.utils.DirHelper;
import com.scwot.renamer.core.utils.FileHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DefaultInputStrategy implements InputStrategy {

    private FileLevelProperties root;

    private String artist;
    private String album;
    private String year;
    private String origYear;
    private String MBReleaseID;
    private ObservableList<Medium> mediumList;
    private String label;
    private String catNumber;

    private int cdCount = 0;
    private int entryCount = 0;
    private int cdNotProcessed = 0;

    public DefaultInputStrategy() {
        mediumList = FXCollections.observableArrayList();
    }

    @Override
    public void execute(File inpitDir) {
        try {
            root = new FileLevelProperties(inpitDir);
            walk(inpitDir);

            String rootStr = root.toString();
            System.out.println(rootStr);

            for (FileLevelProperties prop : root.getChilds()) {
                String propStr = prop.toString();
                System.out.println(propStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mediumList.isEmpty() && mediumList.get(0) != null) {
            artist = mediumList.get(0).getArtist();
            album = mediumList.get(0).getAlbum();
            year = mediumList.get(0).getYear();
            origYear = mediumList.get(0).getOrigYear();
            MBReleaseID = mediumList.get(0).getMBReleaseID();
            label = mediumList.get(0).getLabel();
            catNumber = mediumList.get(0).getCatNum();
        }
    }

    private void walk(File parent) {
        try {
            Path startPath = parent.toPath();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         BasicFileAttributes attrs) {
                    int trackCount = 0;
                    File[] list = dir.toFile().listFiles();
                    FileLevelProperties currentEntry = new FileLevelProperties(dir.toFile());

                    System.out.println("-----> Curr dir: " + dir.toFile().toString());
                    for (File f : list) {
                        if (!f.isDirectory()) {
                            if (FileHelper.isAudioFile(f)) {
                                Audio audio = determineAudio(f);
                                audio.setTrackCount(++trackCount);
                                audio.initialize();
                                currentEntry.addAudio(audio);
                            } else if (FileHelper.isImageFile(f)) {
                                currentEntry.addImage(f);
                            } else {
                                currentEntry.addOther(f);
                            }
                        }
                    }

                    if (entryCount == 0) {
                        root = currentEntry;
                        cdCount = DirHelper.getCDFoldersCount(dir.toFile());
                    } else {
                        root.addChild(currentEntry);
                        if (cdNotProcessed == 0)
                            cdNotProcessed = cdCount;
                    }

                    if (currentEntry.hasAudio()) {
                        addMedium(currentEntry);
                    }

                    entryCount++;

                    System.out.println("--------------");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Audio determineAudio(File file) {
        Audio audio;
        if (FileHelper.isMP3(file))
            audio = new Mp3AudioImpl(file);
        else {
            audio = new Mp3AudioImpl(file);
        }
        return audio;
    }

    private void addMedium(FileLevelProperties properties) {
        Medium medium = new Medium(properties);
        if (medium.getDiscNubmer() == 0)
            medium.setDiscNubmer(discNumber(properties));
        medium.getProperties().setIsMedium(true);
        mediumList.add(medium);
    }

    private int discNumber(FileLevelProperties currentEntry) {
        int cdN = 0;
        if (cdCount > 0 && currentEntry.hasAudio()) {
            cdN = cdCount - --cdNotProcessed;
        }
        return cdN;
    }

    @Override
    public FileLevelProperties getRoot() {
        return root;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    public String getOrigYear() {
        return origYear;
    }

    @Override
    public int getCdCount() {
        return cdCount;
    }

    @Override
    public ObservableList<Medium> getMediums() {
        return mediumList;
    }

    @Override
    public String getMBReleaseID() {
        return MBReleaseID;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getCatNumber() {
        return catNumber;
    }
}
