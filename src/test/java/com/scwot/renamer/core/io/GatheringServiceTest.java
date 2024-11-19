package com.scwot.renamer.core.io;

import com.scwot.renamer.ResourceProvider;
import com.scwot.renamer.core.converter.DirectoryToMediumConverter;
import com.scwot.renamer.core.converter.SystemAudioFileToMp3WrapperConverter;
import com.scwot.renamer.core.converter.SystemDirToDirectoryWrapperConverter;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.service.GatheringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GatheringServiceTest {

    private GatheringService underTest;

    @BeforeEach
    void setUp() {
        underTest = new GatheringService(
                new DirectoryToMediumConverter(),
                new SystemDirToDirectoryWrapperConverter(
                        new SystemAudioFileToMp3WrapperConverter()
                ));
    }


    @Test
    void testWalkWithEmptyDirectory() throws IOException {
        var tempDir = Files.createTempDirectory("testEmptyDir");
        var result = underTest.execute(tempDir.toFile());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Files.deleteIfExists(tempDir);
    }

    @Test
    void testWalkWithSingleRelease() throws IOException {
        List<MediumScope> result = underTest.execute(ResourceProvider.getRegularSimple().getFile());

        // Assert that the result contains one MediumScope
        assertNotNull(result);
        assertEquals(2, result.getFirst().getAudioList().size());
        assertEquals(1, result.getFirst().getDirectoryScope().getListOfImages().size());
        assertEquals(0, result.getFirst().getDirectoryScope().getListOfOthers().size());
    }

}