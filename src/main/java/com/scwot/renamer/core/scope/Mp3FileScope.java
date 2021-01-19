package com.scwot.renamer.core.scope;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Data
public class Mp3FileScope {

    public static final String CUSTOM_FIELD = "TXXX";
    public static final String CATALOGNUMBER_TAG_NAME = "CATALOGNUMBER";
    public static final String ORIGINALYEAR_TAG_NAME = "originalyear";
    public static final String ARTISTS_TAG_NAME = "ARTISTS";
    public static final String MEDIATYPE_TAG_NAME = "MEDIATYPE";
    public static final String RELEASE_COUNTRY = "MusicBrainz Album Release Country";
    public static final String UNKNOWN_VALUE = "[unknown]";

    private static final String[] DELIMITERS = {", ", ";", "\\\\", "/"};
    private static final Pattern TRACK_PATTERN = Pattern.compile("^\\d{1,2}");

    private MP3File audioFile;
    private String fileName;
    private String artistTitle;
    private String albumTitle;
    private String albumArtistTitle;
    private String albumArtistSortTitle;
    private String trackTitle;
    private String year;
    private String origYear;
    private List<String> labels;
    private List<String> catNums;
    private String trackNumber;
    private String releaseMBID;
    private String releaseGroupMBID;
    private String artistMBID;
    private String trackMBID;
    private String releaseCountry;
    private String releaseStatus;
    private String releaseType;
    private List<String> genres;
    private List<String> artists;
    private Boolean hasArtwork;
    private Integer discNumber;
    private int trackCount;
    private int fileNum;
    private Long length;
    private String format;
    private String barcode;

    private byte[] image;

    public void read(File file) {
        audioFile = (MP3File) readAudio(file);
        processNullTag(audioFile);

        length = audioFile.getMP3AudioHeader().getAudioDataLength();
        artistTitle = fromTag(audioFile, FieldKey.ARTIST, UNKNOWN_VALUE);
        albumTitle = fromTag(audioFile, FieldKey.ALBUM, UNKNOWN_VALUE);
        albumArtistTitle = fromTag(audioFile, FieldKey.ALBUM_ARTIST, UNKNOWN_VALUE);
        albumArtistSortTitle = fromTag(audioFile, FieldKey.ALBUM_ARTIST_SORT, UNKNOWN_VALUE);
        releaseMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASEID, EMPTY);
        releaseGroupMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID, EMPTY);
        artistMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_ARTISTID, EMPTY);
        trackMBID = fromTag(audioFile, FieldKey.MUSICBRAINZ_TRACK_ID, EMPTY);
        releaseCountry = fromCustomTag(audioFile, RELEASE_COUNTRY, EMPTY); //TODO: check if it works
        releaseStatus = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_STATUS, EMPTY);
        releaseType = fromTag(audioFile, FieldKey.MUSICBRAINZ_RELEASE_TYPE, EMPTY);
        labels = Arrays.asList(splitString(fromTag(audioFile, FieldKey.RECORD_LABEL, EMPTY)));
        trackTitle = fromTag(audioFile, FieldKey.TITLE, UNKNOWN_VALUE);
        trackNumber = fromTag(audioFile, FieldKey.TRACK, String.valueOf(trackCount));
        format = fromCustomTag(audioFile, MEDIATYPE_TAG_NAME, EMPTY);

        year = StringUtils.substring(boundedFromTag(audioFile, FieldKey.YEAR, EMPTY), 0, 4);
        origYear = StringUtils.substring(origYearValue(audioFile, FieldKey.ORIGINAL_YEAR, EMPTY), 0, 4);
        discNumber = discNumberValue(audioFile);
        genres = listFromTag(audioFile, FieldKey.GENRE);
        artists = Arrays.asList(splitString(fromCustomTag(audioFile, ARTISTS_TAG_NAME, EMPTY)));
        hasArtwork = hasArtwork(audioFile);
        catNums = Arrays.asList(splitString(fromCustomTag(audioFile, CATALOGNUMBER_TAG_NAME, "none")));
        barcode = fromTag(audioFile, FieldKey.BARCODE, EMPTY);

        final List<Artwork> artworkList = audioFile.getTag().getArtworkList();
        final Artwork artwork = artworkList.stream().findFirst().get();
        image = artwork.getBinaryData();
    }

    public AudioFile readAudio(File file) {
        try {
            return AudioFileIO.read(file);
        } catch (Exception ex) {
            log.error(ex.getMessage() + " " + file.getAbsolutePath());
            throw new RuntimeException("Error while trying to read audio file: "
                    + file.getAbsolutePath() + "\n" + ex.getMessage(), ex);
        }
    }

    private static String boundedFromTag(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        try {
            return fromTag(audioFile, fieldKey, defaultValue);
        } catch (StringIndexOutOfBoundsException ex) {
            throw new RuntimeException("Error while parsing bounded value from tag: "
                    + audioFile.getFile().getAbsolutePath() + "\n" + ex.getMessage(), ex);
        }
    }

    private static String origYearValue(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        final String value = boundedFromTag(audioFile, fieldKey, defaultValue);
        final String origYear = fromCustomTag(audioFile, ORIGINALYEAR_TAG_NAME, defaultValue);

        if (StringUtils.isNotEmpty(value) && (Integer.parseInt(value) > Integer.parseInt(origYear))) {
            return origYear;
        }

        return value;
    }

    private static String fromCustomTag(MP3File audioFile, String customTagName, String defaultValue) {
        final List<TagField> tags = audioFile.getID3v2Tag().getFields(CUSTOM_FIELD);
        return tags.stream()
                .map(tag -> tagValue(tag.toString(), customTagName))
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(defaultValue);
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

    private static List<String> listFromTag(MP3File audioFile, FieldKey fieldKey) {
        List<String> resultList = new ArrayList<>();
        String[] resultSplitArray = splitString(audioFile.getTag().getFirst(fieldKey));
        if (ArrayUtils.isEmpty(resultSplitArray)) {
            return Collections.emptyList();
        }

        Collections.addAll(resultList, resultSplitArray);

        return resultList;
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

    private static boolean hasArtwork(MP3File audioFile) {
        return !audioFile.getTag().getArtworkList().isEmpty();
    }

    public int getTrack() {
        int value = 0;
        String trackTag = audioFile.getTag().getFirst(FieldKey.TRACK);

        if (trackTag != null && !trackTag.isEmpty()) {
            Matcher matcher = TRACK_PATTERN.matcher(trackTag);
            if (matcher.find()) {
                value = Integer.parseInt(matcher.group(0));
            }
        }

        return value;
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

    private static void processNullTag(MP3File audioFile) {
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

    private static String fromTag(MP3File audioFile, FieldKey fieldKey, String defaultValue) {
        final String tagValue = fromAudio(audioFile, fieldKey);
        if (isNotEmpty(tagValue)) {
            return tagValue;
        }

        return defaultValue;
    }

    private static String fromAudio(MP3File audioFile, FieldKey fieldKey) {
        return audioFile.getTag().getFirst(fieldKey);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", audioFile.getFile().getAbsolutePath())
                .append("artistTitle", artistTitle)
                .append("albumTitle", albumTitle)
                .append("albumArtistTitle", albumArtistTitle)
                .append("albumArtistSortTitle", albumArtistSortTitle)
                .append("trackTitle", trackTitle)
                .append("year", year)
                .append("origYear", origYear)
                .append("labels", labels)
                .append("catNums", catNums)
                .append("trackNumber", trackNumber)
                .append("releaseMBID", releaseMBID)
                .append("releaseGroupMBID", releaseGroupMBID)
                .append("artistMBID", artistMBID)
                .append("trackMBID", trackMBID)
                .append("releaseCountry", releaseCountry)
                .append("releaseStatus", releaseStatus)
                .append("releaseType", releaseType)
                .append("genres", genres)
                .append("artists", artists)
                .append("hasArtwork", hasArtwork)
                .append("discNumber", discNumber)
                .append("trackCount", trackCount)
                .append("fileNum", fileNum)
                .append("length", length)
                .append("format", format)
                .append("barcode", barcode)
                .append("has image", image != null)
                .toString();
    }
}

