package com.scwot.renamer.core.converter;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SystemAudioFileToMp3WrapperConverterTest {

    private static final String MP3_FILE_NAME = "test.mp3";
    private static final String UNKNOWN_VALUE = "[unknown]";

    @Test
    public void testReadAudioLength_Success() throws Exception {
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        var mockAudioHeader = mock(MP3AudioHeader.class);
        when(mockAudioFile.getMP3AudioHeader()).thenReturn(mockAudioHeader);
        var expectedAudioDataLength = 100L;
        when(mockAudioHeader.getAudioDataLength()).thenReturn(expectedAudioDataLength);

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals(100L, result.getLength());
    }

    @Test
    public void testReadArtistTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ARTIST)).thenReturn("Test Artist");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("Test Artist", result.getArtistTitle());
    }

    @Test
    public void testReadArtistTag_Unknown() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ARTIST)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals(UNKNOWN_VALUE, result.getArtistTitle());
    }

    @Test
    public void testReadAlbumTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM)).thenReturn("Test Album");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("Test Album", result.getAlbumTitle());
    }

    @Test
    public void testReadAlbumTag_Unknown() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals(UNKNOWN_VALUE, result.getAlbumTitle());
    }

    @Test
    public void testReadAlbumArtistTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM_ARTIST)).thenReturn("Test Album Artist");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("Test Album Artist", result.getAlbumArtistTitle());
    }

    @Test
    public void testReadAlbumArtistTag_Unknown() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM_ARTIST)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals(UNKNOWN_VALUE, result.getAlbumArtistTitle());
    }

    @Test
    public void testReadAlbumArtistSortTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM_ARTIST_SORT)).thenReturn("Test Album Artist Sort");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("Test Album Artist Sort", result.getAlbumArtistSortTitle());
    }

    @Test
    public void testReadAlbumArtistSortTag_Unknown() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ALBUM_ARTIST_SORT)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals(UNKNOWN_VALUE, result.getAlbumArtistSortTitle());
    }

    @Test
    public void testReleaseMBIDTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID)).thenReturn("12345");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("12345", result.getReleaseMBID());
    }

    @Test
    public void testReleaseMBIDTag_Empty() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getReleaseMBID());
    }

    @Test
    public void testReleaseGroupMBIDTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID)).thenReturn("12345");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("12345", result.getReleaseGroupMBID());
    }

    @Test
    public void testReleaseGroupMBIDTag_Empty() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getReleaseGroupMBID());
    }

    @Test
    public void testArtistMBIDTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_ARTISTID)).thenReturn("12345");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("12345", result.getArtistMBID());
    }

    @Test
    public void testArtistMBIDTag_Empty() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_ARTISTID)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getArtistMBID());
    }

    @Test
    public void testTrackMBIDTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID)).thenReturn("12345");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("12345", result.getTrackMBID());
    }

    @Test
    public void testTrackMBIDTag_Empty() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getTrackMBID());
    }

    @Test
    public void testReleaseCountryTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);

        var mockCustomOrigYearTag = mock(TagField.class);
        when(mockCustomOrigYearTag.toString()).thenReturn("TXXX:originalyear; 1999");
        var mockCustomCountryTag = mock(TagField.class);
        when(mockCustomCountryTag.toString()).thenReturn("TXXX:MusicBrainz Album Release Country; USA");

        var id3v2TagMock = mock(AbstractID3v2Tag.class);
        when(id3v2TagMock.isEmpty()).thenReturn(false);
        when(id3v2TagMock.getFields("TXXX")).thenReturn(List.of(mockCustomOrigYearTag, mockCustomCountryTag));
        when(mockAudioFile.getID3v2Tag()).thenReturn(id3v2TagMock);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("USA", result.getReleaseCountry());
    }

    @Test
    public void testReleaseCountryTag_Empty() throws Exception {
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getReleaseCountry());
    }

    @Test
    public void testReleaseStatusTag_Success() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_STATUS)).thenReturn("EP");
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("EP", result.getReleaseStatus());
    }

    @Test
    public void testReleaseStatusTag_Empty() throws Exception {
        // Setup
        var mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn(MP3_FILE_NAME);

        var mockAudioFile = mock(MP3File.class);
        mockAudioHeader(mockAudioFile);
        mockCustomOrigYear(mockAudioFile);

        var mockTag = mock(Tag.class);
        when(mockAudioFile.getTag()).thenReturn(mockTag);
        when(mockTag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_STATUS)).thenReturn(null);
        when(mockTag.getFirst(FieldKey.ORIGINAL_YEAR)).thenReturn("1999");

        // Mock AudioFileIO to return the mock AudioFile
        Mockito.mockStatic(AudioFileIO.class); // Mock static method
        when(AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Test converter
        var converter = new SystemAudioFileToMp3WrapperConverter();
        var result = converter.convert(mockFile);

        // Assert
        assertEquals("", result.getReleaseStatus());
    }


    @Test
    void convert_ShouldThrowException_WhenFileCannotBeRead() {
        File inputFile = new File("invalid.mp3");

        // Create a new instance of the converter
        var converter = new SystemAudioFileToMp3WrapperConverter();

        // Expect an exception when trying to convert an unreadable file
        assertThrows(RuntimeException.class, () -> converter.convert(inputFile));
    }






    private static void mockCustomOrigYear(MP3File mockAudioFile) {
        var mockCustomOrigYearTag = mock(TagField.class);
        when(mockCustomOrigYearTag.toString()).thenReturn("TXXX:originalyear; 1999");

        var id3v2TagMock = mock(AbstractID3v2Tag.class);
        when(id3v2TagMock.isEmpty()).thenReturn(false);
        when(id3v2TagMock.getFields("TXXX")).thenReturn(List.of(mockCustomOrigYearTag));
        when(mockAudioFile.getID3v2Tag()).thenReturn(id3v2TagMock);
    }

    private static void mockAudioHeader(MP3File mockAudioFile) {
        var mockAudioHeader = mock(MP3AudioHeader.class);
        when(mockAudioFile.getMP3AudioHeader()).thenReturn(mockAudioHeader);

        var expectedAudioDataLength = 100L;
        when(mockAudioHeader.getAudioDataLength()).thenReturn(expectedAudioDataLength);
    }

}