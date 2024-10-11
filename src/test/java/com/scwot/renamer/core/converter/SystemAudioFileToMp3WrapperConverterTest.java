package com.scwot.renamer.core.converter;

import com.scwot.renamer.core.scope.Mp3FileScope;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SystemAudioFileToMp3WrapperConverterTest {


    @Test
    public void testReadArtistTag_Success() throws Exception {
        // Setup
        File mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn("test.mp3");

        AudioFile mockAudioFile = mock(AudioFile.class);
        Tag mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ARTIST)).thenReturn("Test Artist");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        SystemAudioFileToMp3WrapperConverter converter = new SystemAudioFileToMp3WrapperConverter();
        Mp3FileScope result = converter.convert(mockFile);

        // Assert
        assertEquals("Test Artist", result.getArtistTitle());
    }

}