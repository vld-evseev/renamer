package com.scwot.renamer.core.io.utils;

import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.utils.ExportFileHelper;
import org.jaudiotagger.audio.mp3.MP3File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExportFileHelperTest {

    private static final File DEST = new File("/dest");

    @Mock
    private Mp3FileScope commonMp3FileScope;
    @Mock
    private Mp3FileScope vaMp3FileScope;
    @Mock
    private File commonAudioFile;
    @Mock
    private File vaAudioFile;

    @BeforeEach
    public void setUp() {
        MP3File commonMp3FileMock = mock(MP3File.class);
        when(commonMp3FileScope.getAudioFile()).thenReturn(commonMp3FileMock);
        when(commonMp3FileMock.getFile()).thenReturn(commonAudioFile);

        MP3File vaMp3FileMock = mock(MP3File.class);
        when(vaMp3FileScope.getAudioFile()).thenReturn(vaMp3FileMock);
        when(vaMp3FileMock.getFile()).thenReturn(vaAudioFile);
    }

    @Test
    void updateCommon_ok() {
        when(commonAudioFile.getName()).thenReturn("/in/title.mp3");
        when(commonMp3FileScope.getTrackNumber()).thenReturn("1");
        when(commonMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(commonMp3FileScope, DEST, false);

        assertEquals("/dest/01 - title.mp3", file.getPath());
    }

    @Test
    void updateCommon_invalidPattern() {
        when(commonAudioFile.getName()).thenReturn("/in/1 - title - extra.mp3");
        when(commonMp3FileScope.getTrackNumber()).thenReturn("1");
        when(commonMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(commonMp3FileScope, DEST,false);

        assertEquals("/dest/01 - title.mp3", file.getPath());
    }

    @Test
    void updateVAMissingPattern() {
        when(vaAudioFile.getName()).thenReturn("/in/1 - title.mp3");
        when(vaMp3FileScope.getTrackNumber()).thenReturn("1");
        when(vaMp3FileScope.getAlbumArtistTitle()).thenReturn("Artist");
        when(vaMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(vaMp3FileScope, DEST,true);

        assertEquals("/dest/01 - Artist - title.mp3", file.getPath());
    }

    @Test
    void updateVA_invalidPattern() {
        when(vaAudioFile.getName()).thenReturn("/in/1 - title - extra.mp3");
        when(vaMp3FileScope.getTrackNumber()).thenReturn("1");
        when(vaMp3FileScope.getAlbumArtistTitle()).thenReturn("Artist");
        when(vaMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(vaMp3FileScope, DEST,true);

        assertEquals("/dest/01 - Artist - title.mp3", file.getPath());
    }

    @Test
    void updateNoChangesForValidPattern() {
        when(commonAudioFile.getName()).thenReturn("/in/01 - title.mp3");
        when(commonMp3FileScope.getTrackNumber()).thenReturn("1");
        when(commonMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(commonMp3FileScope, DEST,false);

        assertEquals("/dest/01 - title.mp3", file.getPath());
    }

    @Test
    void updateVANoChangesForValidPattern() {
        when(vaAudioFile.getName()).thenReturn("/in/01 - Artist - title.mp3");
        when(vaMp3FileScope.getTrackNumber()).thenReturn("1");
        when(vaMp3FileScope.getAlbumArtistTitle()).thenReturn("Artist");
        when(vaMp3FileScope.getTrackTitle()).thenReturn("title");

        File file = ExportFileHelper.updateNameIfNeeded(vaMp3FileScope, DEST,true);

        assertEquals("/dest/01 - Artist - title.mp3", file.getPath());
    }
}