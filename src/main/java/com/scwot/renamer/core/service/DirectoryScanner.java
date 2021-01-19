package com.scwot.renamer.core.service;

import com.scwot.renamer.core.utils.DirHelper;
import com.scwot.renamer.core.utils.FileHelper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectoryScanner {

    private final static String MACOSX_FOLDER_NAME = "__MACOSX";

    @SneakyThrows
    public List<Path> scan(File inputDir) {
        if (inputDir == null) {
            throw new RuntimeException("Input inputDir should not be null");
        }

        final List<Path> candidates = Files.walk(inputDir.toPath())
                .filter(Files::isDirectory)
                .filter(path -> {
                    final long audioCount = Arrays.stream(path.toFile().listFiles()).filter(FileHelper::isMP3).count();
                    return audioCount > 0;
                })
                .collect(Collectors.toList());

        final List<Path> flattenCandidates = candidates.stream()
                .map(path -> {
                    final String[] split = path.toString().split("\\\\");
                    final String lastSubDirName = Arrays.stream(split).reduce((f, l) -> l).get();
                    if (StringUtils.startsWithIgnoreCase(lastSubDirName, "CD")) {
                        return path.getParent();
                    } else {
                        return path;
                    }
                }).distinct().collect(Collectors.toList());


        int cdSubDirsCount = DirHelper.countMultiDiskFolders(inputDir);

       /* if (inputDir.getName().equalsIgnoreCase(MACOSX_FOLDER_NAME)) {
            if (inputDir.exists()) {
                DirHelper.deleteDirectory(inputDir);
            }

            *//*Iterator<File> it = processedDirectoryList.iterator();

            while (it.hasNext()) {
                File next = it.next();
                if (next.getAbsolutePath().contains(inputDir.getName())) {
                    it.remove();
                }
            }*//*

        } else {
            final DirInfo dirInfo = DirHelper.countFileTypes(inputDir);

            if (DirHelper.releaseNotPresent(dirInfo)) {
                for (int i = 0; i < candidates.size(); i++) {
                    if (candidates.get(i).compareTo(inputDir) == 0) {
                        candidates.remove(i);
                    }
                }
            }

            int cdParentFolderCount = DirHelper.countMultiDiskFolders(inputDir);

            if (DirHelper.containsJustInnerFolders(dirInfo)) {
                for (int i = 0; i < candidates.size(); i++) {
                    if (candidates.get(i).compareTo(inputDir) == 0) {
                        candidates.remove(i);
                        break;
                    }
                }
            }

            if (dirInfo.hasAudio()
                    && cdSubDirsCount == 0
                    && cdParentFolderCount > 0) {
                for (int i = 0; i < candidates.size(); i++) {
                    if (candidates.get(i).compareTo(inputDir) == 0) {
                        candidates.remove(i);
                    }
                }
            }
        }*/

        return flattenCandidates;
    }


}
