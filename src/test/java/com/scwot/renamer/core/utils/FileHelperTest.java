package com.scwot.renamer.core.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.scwot.renamer.ResourceProvider.getRegularSimpleWithCovers;

class FileHelperTest {

    private Collection<File> files;

    @BeforeEach
    public void setUp() throws IOException {
        files = FileUtils.listFiles(getRegularSimpleWithCovers().getFile(), TrueFileFilter.INSTANCE, FalseFileFilter.INSTANCE);
    }

    @Test
    void isAudioFile() {
        final List<File> audioFiles =
                files.stream()
                        .filter(file -> FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("mp3"))
                        .collect(Collectors.toList());
        audioFiles.stream().map(FileHelper::isAudioFile).forEach(Assertions::assertTrue);
    }

    @Test
    void isImageFile() {
        final List<File> audioFiles =
                files.stream()
                        .filter(file -> FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpg"))
                        .collect(Collectors.toList());
        audioFiles.stream().map(FileHelper::isImageFile).forEach(Assertions::assertTrue);
    }

    @Test
    void isMP3() {
        final List<File> audioFiles =
                files.stream()
                        .filter(file -> FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("mp3"))
                        .collect(Collectors.toList());
        audioFiles.stream().map(FileHelper::isMP3).forEach(Assertions::assertTrue);
    }

}