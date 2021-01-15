package com.scwot.renamer.core.tasks;

import com.scwot.renamer.core.utils.DirHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Iterator;

public class ScanDirTask extends Thread {

    private final static String MACOSX_FOLDER_NAME = "__MACOSX";

    private ObservableList<File> inputDirectoryList;
    private ObservableList<File> processedDirectoryList;

    public ScanDirTask(ObservableList<File> inputDirectoryList) {
        this.inputDirectoryList = inputDirectoryList;
        processedDirectoryList = FXCollections.observableArrayList(inputDirectoryList);
    }

    @Override
    public void run() {
        for (int i = 0; i < inputDirectoryList.size(); i++) {
            File dir = inputDirectoryList.get(i);
            if (dir != null && dir.exists()) {
                removeUnwantedDirs(dir);
            }
        }

        System.out.println(processedDirectoryList);
    }

    private void removeUnwantedDirs(File dir) {
        int cdSubfoldersCount = DirHelper.getCDFoldersCount(dir);

        if (dir.getName().equals(MACOSX_FOLDER_NAME)) {
            if (dir.exists()) {
                DirHelper.deleteDirectory(dir);
            }

            Iterator<File> it = processedDirectoryList.iterator();

            while (it.hasNext()) {
                File next = it.next();
                if (next.getAbsolutePath().contains(dir.getName())) {
                    it.remove();
                }
            }

        } else {
            DirHelper helper = new DirHelper();
            helper.hasInnerFolder(dir);
            helper.countFileTypes(dir);
            helper.hasAudio();

            if (helper.doesNotContainRelease(dir)) {
                for (int i = 0; i < processedDirectoryList.size(); i++) {
                    if (processedDirectoryList.get(i).compareTo(dir) == 0) {
                        processedDirectoryList.remove(i);
                    }
                }
            }

            int cdParentFolderCount = DirHelper.getCDFoldersCount(dir);

            if (helper.containsJustInnerFolders(dir)) {
                for (int i = 0; i < processedDirectoryList.size(); i++) {
                    if (processedDirectoryList.get(i).compareTo(dir) == 0) {
                        processedDirectoryList.remove(i);
                        break;
                    }
                }
            }

            if (helper.hasAudio()
                    && cdSubfoldersCount == 0
                    && cdParentFolderCount > 0) {
                for (int i = 0; i < processedDirectoryList.size(); i++) {
                    if (processedDirectoryList.get(i).compareTo(dir) == 0) {
                        processedDirectoryList.remove(i);
                    }
                }
            }
        }
    }

    public ObservableList<File> getProcessedDirectoryList() {
        return processedDirectoryList;
    }
}
