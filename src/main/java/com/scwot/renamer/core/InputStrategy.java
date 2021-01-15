package com.scwot.renamer.core;

import javafx.collections.ObservableList;

import java.io.File;

public interface InputStrategy {

    void execute(File inputDir);

    ObservableList<Medium> getMediums();

    FileLevelProperties getRoot();

    String getAlbum();

    String getArtist();

    String getYear();

    String getOrigYear();

    int getCdCount();

    String getMBReleaseID();

    String getLabel();

    String getCatNumber();


}
