package com.scwot.renamer.core;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

import static java.lang.Boolean.FALSE;
import static javafx.collections.FXCollections.observableArrayList;

public class FileLevelProperties {

    private ObservableList<FileLevelProperties> childs;

    private ObservableList<File> listOfInnerFolders;
    private ObservableList<Mp3FileWrapper> listOfAudios;
    private ObservableList<File> listOfImages;
    private ObservableList<File> listOfOthers;

    private ObjectProperty<File> currentDir;
    private SimpleStringProperty dirName;
    private SimpleIntegerProperty isMultiCD;
    private SimpleBooleanProperty hasMoreThanOneArtists;
    private boolean isMedium;

    public FileLevelProperties(File inputDir) {
        setDirName(new SimpleStringProperty(this, "dirName"));
        setIsMultiCD(new SimpleIntegerProperty(this, "isMultiCD"));
        setHasMoreThanOneArtists(new SimpleBooleanProperty(this, "hasMoreThanOneArtists"));
        getHasMoreThanOneArtists().setValue(FALSE);
        currentDir = new SimpleObjectProperty(this, "currentDir");
        currentDir.setValue(inputDir);
        isMedium = false;

        setListOfAudios(observableArrayList());
        setListOfImages(observableArrayList());
        setListOfOthers(observableArrayList());
        setListOfInnerFolders(observableArrayList());
        childs = FXCollections.observableArrayList();

        dirName.setValue(inputDir.getName());
    }

    public void addAudio(Mp3FileWrapper audio) {
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


    public ObservableList<FileLevelProperties> getChilds() {
        return childs;
    }

    public ObservableList<File> getListOfInnerFolders() {
        return listOfInnerFolders;
    }

    public void setListOfInnerFolders(ObservableList<File> listOfInnerFolders) {
        this.listOfInnerFolders = listOfInnerFolders;
    }

    public ObservableList<Mp3FileWrapper> getListOfAudios() {
        return listOfAudios;
    }

    public ObservableList<File> getListOfImages() {
        return listOfImages;
    }

    public ObservableList<File> getListOfOthers() {
        return listOfOthers;
    }

    public SimpleStringProperty getDirName() {
        return dirName;
    }

    public SimpleIntegerProperty getIsMultiCD() {
        return isMultiCD;
    }

    public SimpleBooleanProperty getHasMoreThanOneArtists() {
        return hasMoreThanOneArtists;
    }

    public ObjectProperty<File> getCurrentDir() {
        return currentDir;
    }

    public void setListOfAudios(ObservableList<Mp3FileWrapper> listOfAudios) {
        this.listOfAudios = listOfAudios;
    }

    public void setListOfImages(ObservableList<File> listOfImages) {
        this.listOfImages = listOfImages;
    }

    public void setListOfOthers(ObservableList<File> listOfOthers) {
        this.listOfOthers = listOfOthers;
    }

    public void setDirName(SimpleStringProperty dirName) {
        this.dirName = dirName;
    }

    public void setIsMultiCD(SimpleIntegerProperty isMultiCD) {
        this.isMultiCD = isMultiCD;
    }

    public void setHasMoreThanOneArtists(SimpleBooleanProperty hasMoreThanOneArtists) {
        this.hasMoreThanOneArtists = hasMoreThanOneArtists;
    }

    public void addChild(FileLevelProperties child) {
        childs.add(child);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current dir: ");
        builder.append(currentDir.getValue().getAbsolutePath());
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

    public String printList(ObservableList<? extends Object> list) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : list) {
            builder.append("\t");
            builder.append(obj.toString());
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    public boolean isMedium() {
        return isMedium;
    }

    public void setIsMedium(boolean isMedium) {
        this.isMedium = isMedium;
    }
}
