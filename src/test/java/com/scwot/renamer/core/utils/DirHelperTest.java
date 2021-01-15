package com.scwot.renamer.core.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.scwot.renamer.ResourceProvider.*;
import static org.junit.jupiter.api.Assertions.*;

class DirHelperTest {

    @BeforeEach
    void setUp() throws IOException {
    }

    @Test
    void ifRegularSimple_thenCountFiles() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getRegularSimple().getFile());
        assertEquals(2, dirInfo.getAudioCount());
        assertEquals(1, dirInfo.getImagesCount());
        assertEquals(0, dirInfo.getOthersCount());
    }

    @Test
    void ifRegularMultiDiskWithCovers_thenCountFiles() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getRegularMultiDiskWithCovers().getFile());
        assertEquals(4, dirInfo.getAudioCount());
        assertEquals(4, dirInfo.getImagesCount());
        assertEquals(0, dirInfo.getOthersCount());
    }

    @Test
    void ifRegularSimpleWithCovers_thenCountFiles() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getRegularSimpleWithCovers().getFile());
        assertEquals(2, dirInfo.getAudioCount());
        assertEquals(3, dirInfo.getImagesCount());
        assertEquals(0, dirInfo.getOthersCount());
    }

    @Test
    void ifRegularSimpleWithOthers_thenCountFiles() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getRegularSimpleWithOthers().getFile());
        assertEquals(2, dirInfo.getAudioCount());
        assertEquals(0, dirInfo.getImagesCount());
        assertEquals(3, dirInfo.getOthersCount());
    }

    @Test
    void countMultiDiskFolders_present() throws IOException {
        final int result = DirHelper.countMultiDiskFolders(getRegularMultiDiskWithCovers().getFile());
        assertEquals(2, result);
    }

    @Test
    void countMultiDiskFolders_notPresent() throws IOException {
        final int result = DirHelper.countMultiDiskFolders(getRegularSimple().getFile());
        assertEquals(0, result);
    }

    @Test
    void deleteDirectory() {
    }

    @Test
    void hasInnerFolder_true() throws IOException {
        final boolean hasInnerFolder = DirHelper.hasInnerFolder(getRegularMultiDiskWithCovers().getFile());
        assertTrue(hasInnerFolder);
    }

    @Test
    void hasInnerFolder_false() throws IOException {
        final boolean hasInnerFolder = DirHelper.hasInnerFolder(getRegularSimple().getFile());
        assertFalse(hasInnerFolder);
    }

    @Test
    void releaseNotPresent_true() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getNonRelease().getFile());
        final boolean releaseNotPresent = DirHelper.releaseNotPresent(dirInfo);
        assertTrue(releaseNotPresent);
    }

    @Test
    void releaseNotPresent_false() throws IOException {
        final DirInfo dirInfo = DirHelper.countFileTypes(getRegularSimple().getFile());
        final boolean releaseNotPresent = DirHelper.releaseNotPresent(dirInfo);
        assertFalse(releaseNotPresent);
    }

    @Test
    void containsJustInnerFolders() {
    }


}