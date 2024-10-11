package com.scwot.renamer.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirectoryScannerTest {

    @Test
    void testScan_nullInputDir() {
        var scanner = new DirectoryScanner();
        assertThrows(IllegalArgumentException.class, () -> scanner.scan(null));
    }

    @Test
    void testScan_emptyDirectory(@TempDir Path tempDir) {
        var scanner = new DirectoryScanner();
        List<Path> result = scanner.scan(tempDir.toFile());
        assertEquals(0, result.size());
    }

    @Test
    void testScan_directoryWithMP3(@TempDir Path tempDir) throws IOException {
        // Create a directory with an MP3 file
        var mp3Dir = tempDir.resolve("mp3dir");
        Files.createDirectory(mp3Dir);
        Files.createFile(mp3Dir.resolve("song.mp3"));

        DirectoryScanner scanner = new DirectoryScanner();
        List<Path> result = scanner.scan(tempDir.toFile());
        assertEquals(1, result.size());
        assertEquals(mp3Dir, result.get(0));
    }

    @Test
    void testScan_directoryWithSubdirWithMP3(@TempDir Path tempDir) throws IOException {
        // Create a directory with a subdirectory containing an MP3 file
        Path mp3Dir = tempDir.resolve("mp3dir");
        Files.createDirectory(mp3Dir);
        Path subDir = mp3Dir.resolve("subDir");
        Files.createDirectory(subDir);
        Files.createFile(subDir.resolve("song.mp3"));

        DirectoryScanner scanner = new DirectoryScanner();
        List<Path> result = scanner.scan(tempDir.toFile());
        assertEquals(1, result.size());
        assertEquals(subDir, result.get(0));
    }

    @Test
    void testScan_directoryWithMACOSXFolder(@TempDir Path tempDir) throws IOException {
        // Create a directory with a __MACOSX folder
        Path macosxDir = tempDir.resolve("__MACOSX");
        Files.createDirectory(macosxDir);

        DirectoryScanner scanner = new DirectoryScanner();
        List<Path> result = scanner.scan(tempDir.toFile());
        assertEquals(0, result.size());
    }
}