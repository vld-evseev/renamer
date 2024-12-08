package com.scwot.renamer.core.service;

import com.scwot.renamer.core.utils.FileHelper;
import com.scwot.renamer.core.utils.Patterns;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: redesign logic to scan file tags at first.
//       If they don't have information about folder structure,
//       then look at the directories
@Service
public class DirectoryScanner {

    /**
     * Scans the input directory and returns a list of subdirectories containing MP3 files.
     *
     * @param inputDir the input directory to scan
     * @return a list of subdirectories containing MP3 files
     */
    @SneakyThrows
    public List<Path> scan(File inputDir) {
        if (inputDir == null) {
            throw new IllegalArgumentException("Directory should not be null");
        }

        try (Stream<Path> stream = Files.walk(inputDir.toPath())) {
            return stream
                    .filter(this::hasMP3s)
                    .map(this::flattenPath)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    private boolean hasMP3s(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.anyMatch(FileHelper::isMP3);
        } catch (IOException e) {
            return false;
        }
    }

    private Path flattenPath(Path path) {
        Path fileName = path.getFileName();
        if (Patterns.getMultiDiskPattern().matcher(fileName.toString()).find()) {
            return path.getParent();
        }

        return path;
    }

}
