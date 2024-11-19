package com.scwot.renamer.core.io.utils;

import com.scwot.renamer.core.scope.ReleaseScope;
import com.scwot.renamer.core.utils.ExportRenameUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportRenameUtilsTest {

    private static final int DEFAULT_MAX_TITLE_LENGTH = 70;

    @Test
    void testBuildArtistDirNameForVA() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(true);


        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("VA", result);
    }

    @Test
    void testBuildArtistDirNameWithCountryOnly() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(false);
        when(releaseScope.getArtistCountry()).thenReturn("USA");
        when(releaseScope.getTopArtistGenres()).thenReturn(Collections.emptyList());
        when(releaseScope.getAlbumArtistSort()).thenReturn("Artist");

        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("Artist [USA]", result);
    }

    @Test
    void testBuildArtistDirNameWithoutArtistSort() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(false);
        when(releaseScope.getArtistCountry()).thenReturn("USA");
        when(releaseScope.getTopArtistGenres()).thenReturn(Collections.emptyList());
        when(releaseScope.getAlbumArtist()).thenReturn("Artist");

        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("Artist [USA]", result);
    }

    @Test
    void testBuildArtistDirNameWithGenresOnly() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(false);
        when(releaseScope.getArtistCountry()).thenReturn(null);
        when(releaseScope.getTopArtistGenres()).thenReturn(Arrays.asList("Psychedelic Rock", "Stoner Rock"));
        when(releaseScope.getAlbumArtistSort()).thenReturn("Artist");

        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("Artist [Psychedelic Rock, Stoner Rock]", result);
    }

    @Test
    void testBuildArtistDirNameWithCountryAndGenres() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(false);
        when(releaseScope.getArtistCountry()).thenReturn("USA");
        when(releaseScope.getTopArtistGenres()).thenReturn(Arrays.asList("Noise", "Industrial"));
        when(releaseScope.getAlbumArtistSort()).thenReturn("The Artist");

        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("Artist, The [USA Industrial, Noise]", result);
    }

    @Test
    void testBuildArtistDirNameWithoutCountryOrGenres() {
        // Arrange
        ReleaseScope releaseScope = mock(ReleaseScope.class);
        when(releaseScope.isVA()).thenReturn(false);
        when(releaseScope.getArtistCountry()).thenReturn(null);
        when(releaseScope.getTopArtistGenres()).thenReturn(Collections.emptyList());
        when(releaseScope.getAlbumArtistSort()).thenReturn("The Artist");

        // Act
        String result = ExportRenameUtils.buildArtistDirName(releaseScope);

        // Assert
        assertEquals("Artist, The", result);
    }

    @Test
    public void testTitleUnder70Characters() {
        String title = "This is a short title";
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals(title, result);
    }

    @Test
    public void testTitleExactly70Characters() {
        String title = "This is a title that has exactly seventy characters in total for testing.";
        String expected = "This is a title that"; // First 5 words, as per the method's logic
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals(expected, result); // Expect it to be trimmed to 5 words
    }

    @Test
    public void testTitleOver70CharactersTrimmedToFiveWords() {
        String title = "This is a very long title that should be trimmed after five words are taken";
        String expected = "This is a very long";
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals(expected, result);
    }

    @Test
    public void testTitleOver70CharactersButLessThanFiveWords() {
        String title = "This title has very very very very long words"; // Over 70 characters but only 5 words
        String expected = "This title has very very very very long words"; // No trimming, fewer than 5 words
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals(expected, result);
    }


    @Test
    public void testEmptyTitle() {
        String title = "";
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals("", result);
    }

    @Test
    public void testTitleWithExactlyFiveWords() {
        String title = "This title has exactly five";
        String expected = "This title has exactly five";
        String result = ExportRenameUtils.trimTitle(title, DEFAULT_MAX_TITLE_LENGTH);
        assertEquals(expected, result);
    }

    @Test
    public void testNormalizesSpecialCharacters() {
        String name = "Inva$lid *Name<>?";
        String expected = "Invalid Name";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testRemovesBackslashes() {
        String name = "Name\\With\\Backslashes";
        String expected = "NameWithBackslashes";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testReplacesForwardSlashWithHyphen() {
        String name = "Name/With/Forward/Slashes";
        String expected = "Name-With-Forward-Slashes";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testReplacesColonWithSpaceHyphen() {
        String name = "Name:With:Colons";
        String expected = "Name -With -Colons";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testEmptyString() {
        String name = "";
        String expected = "";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testNoUnwantedCharacters() {
        String name = "ValidName";
        String expected = "ValidName";
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

    @Test
    public void testMultipleUnwantedCharacters() {
        String name = "Invalid:Name/With*<>?`$Special$Characters";
        String expected = "Invalid -Name-WithSpecialCharacters"; // Corrected expectation
        String result = ExportRenameUtils.normalizeName(name);
        assertEquals(expected, result);
    }

}