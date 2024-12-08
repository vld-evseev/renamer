package com.scwot.renamer.core.converter;

import com.scwot.renamer.core.scope.Mp3FileScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Component
public class SystemAudioFileToMp3WrapperConverter implements Converter<File, Mp3FileScope> {

    private static final String CUSTOM_FIELD = "TXXX";
    private static final String CATALOGNUMBER_TAG_NAME = "CATALOGNUMBER";
    private static final String ORIGINALYEAR_TAG_NAME = "originalyear";
    private static final String ARTISTS_TAG_NAME = "ARTISTS";
    private static final String MEDIATYPE_TAG_NAME = "MEDIATYPE";
    private static final String RELEASE_COUNTRY = "MusicBrainz Album Release Country";
    private static final String UNKNOWN_VALUE = "[unknown]";

    private static final String[] DELIMITERS = {", ", ";", "\\\\", "/"};
    private static final Pattern TRACK_PATTERN = Pattern.compile("^\\d{1,2}");

    @Override
    public Mp3FileScope convert(File input) {
        AudioFile read = read(input);
        MP3File audioFile = (MP3File) read;
        ensureID3v24TagExists(audioFile);
        return readTags(audioFile);
    }

    private Mp3FileScope readTags(MP3File audioFile) {
        var audioDataLength = audioFile.getMP3AudioHeader().getAudioDataLength();
        var artistTitle = fromTag(audioFile, FieldKey.ARTIST, UNKNOWN_VALUE);
        var albumTitle = fromTag(audioFile, FieldKey.ALBUM, UNKNOWN_VALUE);
        var albumArtistTitle = fromTag(audioFile, FieldKey.ALBUM_ARTIST, UNKNOWN_VALUE);
        var albumArtistSortTitle = fromTag(audioFile, FieldKey.ALBUM_ARTIST_SORT, UNKNOWN_VALUE);

        var releaseMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASEID, EMPTY);
        var releaseGroupMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID, EMPTY);
        var artistMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_ARTISTID, EMPTY);
        var trackMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_TRACK_ID, EMPTY);

        var releaseCountry = fromCustomTag(audioFile, RELEASE_COUNTRY, EMPTY);
        var releaseStatus = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_STATUS, EMPTY);
        var releaseType = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_TYPE, EMPTY);

        var labels = Arrays.asList(splitString(fromTag(audioFile, FieldKey.RECORD_LABEL, EMPTY)));

        var trackTitle = fromTag(audioFile, FieldKey.TITLE, UNKNOWN_VALUE);
        var trackNumber = fromTag(audioFile, FieldKey.TRACK, "000");
        var discNumber = discNumberValue(audioFile);
        var discSubtitle = fromTag(audioFile, FieldKey.DISC_SUBTITLE, EMPTY);

        var format = fromCustomTag(audioFile, MEDIATYPE_TAG_NAME, EMPTY);

        var year = StringUtils.substring(boundedFromTag(audioFile, FieldKey.YEAR, EMPTY), 0, 4);
        var origYear = StringUtils.substring(origYearValue(audioFile, FieldKey.ORIGINAL_YEAR, year), 0, 4);

        var genres = listFromTag(audioFile, FieldKey.GENRE);
        var artists = Arrays.asList(splitString(fromCustomTag(audioFile, ARTISTS_TAG_NAME, EMPTY)));
        var catNums = Arrays.asList(splitString(fromCustomTag(audioFile, CATALOGNUMBER_TAG_NAME, "none")));
        var barcode = fromTag(audioFile, FieldKey.BARCODE, EMPTY);

        var hasArtwork = !audioFile.getTag().getArtworkList().isEmpty();
        var artwork = fetchArtwork(audioFile);

        return Mp3FileScope.builder()
                .audioFile(audioFile)
                .fileName(FilenameUtils.getBaseName(audioFile.getFile().getName()))
                .length(audioDataLength)
                .artistTitle(artistTitle)
                .albumTitle(albumTitle)
                .albumArtistTitle(albumArtistTitle)
                .albumArtistSortTitle(albumArtistSortTitle)
                .releaseMBID(releaseMBID)
                .releaseGroupMBID(releaseGroupMBID)
                .artistMBID(artistMBID)
                .trackMBID(trackMBID)
                .releaseCountry(releaseCountry)
                .releaseStatus(releaseStatus)
                .releaseType(releaseType)
                .labels(labels)
                .trackTitle(trackTitle)
                .trackNumber(trackNumber)
                .format(format)
                .year(year)
                .origYear(origYear)
                .discNumber(discNumber)
                .discSubtitle(discSubtitle)
                .genres(genres)
                .artists(artists)
                .hasArtwork(hasArtwork)
                .catNums(catNums)
                .barcode(barcode)
                .image(artwork)
                .build();
    }

    private static byte[] fetchArtwork(MP3File audioFile) {
        return audioFile.getTag().getArtworkList().stream()
                .findFirst()
                .map(Artwork::getBinaryData)
                .orElse(null);
    }

    private static List<String> listFromTag(MP3File audioFile, FieldKey fieldKey) {
        List<String> genres = audioFile.getTag().getAll(fieldKey);
        if (genres.isEmpty()) {
            return List.of();
        }
        if (genres.size() > 1) {
            return genres;
        }

        List<String> resultList = new ArrayList<>();
        String[] resultSplitArray = splitString(genres.getFirst());
        if (ArrayUtils.isEmpty(resultSplitArray)) {
            return Collections.emptyList();
        }

        Collections.addAll(resultList, resultSplitArray);

        return resultList;
    }

    private static String fromCustomTag(MP3File audioFile, String customTagName, String defaultValue) {
        final AbstractID3v2Tag id3v2Tag = audioFile.getID3v2Tag();
        if (id3v2Tag == null || id3v2Tag.isEmpty()) {
            return EMPTY;
        }

        final List<TagField> tags = id3v2Tag.getFields(CUSTOM_FIELD);
        return tags.stream()
                .map(tag -> tagValue(tag.toString(), customTagName))
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(defaultValue);
    }

    private static String tagValue(String tag, String key) {
        String value = EMPTY;
        if (tag.contains(key)) {
            value = tag.split("; ")[1];
            value = value
                    .replaceAll("Text=", EMPTY)
                    .replaceAll("\"", EMPTY)
                    .replaceAll(";", EMPTY)
                    .replaceAll("\u0000", EMPTY);
        }
        return value;
    }

    private static String boundedFromTag(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        try {
            return fromTag(audioFile, fieldKey, defaultValue);
        } catch (StringIndexOutOfBoundsException ex) {
            throw new RuntimeException("Error while parsing bounded value from tag: "
                    + audioFile.getFile().getAbsolutePath() + "\n" + ex.getMessage(), ex);
        }
    }

    private static int discNumberValue(MP3File audioFile) {
        int value = 0;
        String discNumberTag = audioFile.getTag().getFirst(FieldKey.DISC_NO);
        if (StringUtils.isNotEmpty(discNumberTag)) {
            discNumberTag = discNumberTag
                    .replaceFirst("^0", EMPTY)
                    .replaceAll("/.+", EMPTY)
                    .replaceAll("\\D", EMPTY);
            if (StringUtils.isNotEmpty(discNumberTag)) {
                value = Integer.parseInt(discNumberTag);
            }
        }
        return value;
    }

    private static String origYearValue(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        try {
            final String value = boundedFromTag(audioFile, fieldKey, defaultValue);
            if (isBlank(value)) {
                return defaultValue;
            }

            final String yearValue = value.substring(0, 4);
            final String origYear = fromCustomTag(audioFile, ORIGINALYEAR_TAG_NAME, defaultValue);

            if (StringUtils.isBlank(origYear)) {
                return defaultValue;
            }

            if (StringUtils.isNotEmpty(yearValue) && (Integer.parseInt(yearValue) > Integer.parseInt(origYear))) {
                return origYear;
            }

            return yearValue;
        } catch (Exception ex) {
            log.error("Exception for file - " + audioFile.getFile().getName());
            throw new RuntimeException(ex);
        }
    }

    private static String fromTag(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        try {
            final String tagValue = fromAudio(audioFile, fieldKey);
            if (isNotEmpty(tagValue)) {
                return tagValue;
            }

            return defaultValue;
        } catch (Exception ex) {
            log.error("Exception for file - " + audioFile.getFile().getName());
            throw new RuntimeException(ex);
        }

    }

    private static String fromAudio(MP3File audioFile, FieldKey fieldKey) {
        return audioFile.getTag().getFirst(fieldKey);
    }

    private static String[] splitString(String tagString) {
        if (StringUtils.isEmpty(tagString)) {
            return new String[]{};
        }

        String[] splitString = {};
        for (String delimiter : DELIMITERS) {
            if (tagString.contains(delimiter)) {
                splitString = tagString.split(delimiter);
                for (int i = 0; i < splitString.length; i++) {
                    splitString[i] = splitString[i].trim();
                }

                break;
            }
        }

        if (splitString.length == 0) {
            return new String[]{tagString};
        }

        return splitString;
    }

    private static void ensureID3v24TagExists(MP3File audioFile) {
        if (audioFile.getTag() == null) {
            audioFile.setTag(new ID3v24Tag());
            try {
                audioFile.commit();
            } catch (CannotWriteException ex) {
                throw new RuntimeException("Error while processing null-tag audio file: "
                        + audioFile.getFile().getAbsolutePath() + "\n" + ex.getMessage(), ex);
            }
        }
    }


    private AudioFile read(File file) {
        try {
            return AudioFileIO.read(file);
        } catch (Exception ex) {
            log.error(ex.getMessage() + " " + file.getAbsolutePath());
            throw new RuntimeException("Error while trying to read audio file: "
                    + file.getAbsolutePath() + "\n" + ex.getMessage(), ex);
        }
    }
}
