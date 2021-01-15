package com.scwot.renamer.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;

public class ImportTask extends Task<ObservableList<LocalRelease>> {

    private ObservableList<File> processedDirectoryList;
    private ObservableList<LocalRelease> localReleaseList;

    public void initialize(ObservableList<File> cleanDirectoryList) {
        this.processedDirectoryList = cleanDirectoryList;
        this.localReleaseList = FXCollections.observableArrayList();
    }

    @Override
    protected ObservableList<LocalRelease> call() throws Exception {
        for (int i = 0; i < this.processedDirectoryList.size(); i++) {
            LocalRelease release = new LocalRelease();
            release.build(new DefaultInputStrategy(), this.processedDirectoryList.get(i));
            updateMessage(release.toString());
            this.localReleaseList.add(release);
            updateProgress(i + 1, this.processedDirectoryList.size());
        }


        return null;
    }
}
