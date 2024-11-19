package com.scwot.renamer.core.scope;

import lombok.Builder;
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
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
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
@Builder
public class Mp3FileScope {

    private static final String CUSTOM_FIELD = "TXXX";
    private static final String CATALOGNUMBER_TAG_NAME = "CATALOGNUMBER";
    private static final String ORIGINALYEAR_TAG_NAME = "originalyear";
    private static final String ARTISTS_TAG_NAME = "ARTISTS";
    private static final String MEDIATYPE_TAG_NAME = "MEDIATYPE";
    private static final String RELEASE_COUNTRY = "MusicBrainz Album Release Country";
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
    private String discSubtitle;
    private int fileNum;
    private Long length;
    private String format;
    private String barcode;

    private byte[] image;

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
                .append("discSubtitle", discSubtitle)
                .append("fileNum", fileNum)
                .append("length", length)
                .append("format", format)
                .append("barcode", barcode)
                .append("has image", image != null)
                .toString();
    }
}

