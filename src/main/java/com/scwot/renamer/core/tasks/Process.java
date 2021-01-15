package com.scwot.renamer.core.tasks;

import com.scwot.renamer.core.DefaultExportStrategy;
import com.scwot.renamer.core.DefaultInputStrategy;
import com.scwot.renamer.core.ExportStrategy;
import com.scwot.renamer.core.LocalRelease;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class Process {

    private ObservableList<File> inputList = FXCollections.observableArrayList();
    private File dir;
    private boolean includeInArtistFolder;

    public Process(File dir, boolean includeInArtistFolder) {
        this.dir = dir;
        inputList.add(dir);
        this.includeInArtistFolder = includeInArtistFolder;
    }

    public void start() throws InterruptedException {
        ScanDirTask scanTask = new ScanDirTask(inputList);

        ImportThread importThread = new ImportThread(scanTask, dir);
        scanTask.start();
        importThread.start();

        importThread.join();

        System.out.println(importThread.getRelease().toString());
    }

    private class ImportThread extends Thread {

        private ScanDirTask scanTask;
        private LocalRelease release;
        private File dir;

        public ImportThread(ScanDirTask scanTask, File dir) {
            this.scanTask = scanTask;
            this.dir = dir;
        }

        public void run() {
            if (scanTask != null && scanTask.isAlive()) {
                try {
                    scanTask.join();
                } catch (InterruptedException e) {
                }
            }

            File dir = scanTask.getProcessedDirectoryList().get(0);

            release = new LocalRelease();
            release.build(new DefaultInputStrategy(), dir);

            ExportStrategy exportStrategy = new DefaultExportStrategy(release, dir.getParentFile(), includeInArtistFolder);
            exportStrategy.execute();
        }

        public LocalRelease getRelease() {
            return release;
        }
    }
}
