package com.scwot.renamer.core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mp3AudioImpl implements Audio {

    private File file;
    private MP3File audioFile;
    private int fileNum;
    private SimpleStringProperty fileName;
    private SimpleStringProperty artistTitle;
    private SimpleStringProperty albumTitle;
    private SimpleStringProperty trackTitle;
    private SimpleStringProperty year;
    private SimpleStringProperty origYear;
    private SimpleStringProperty label;
    private SimpleStringProperty catNum;
    private SimpleListProperty<String> genres;
    private SimpleBooleanProperty hasArtwork;
    private SimpleStringProperty trackNumber;
    private SimpleIntegerProperty discNumber;
    private String MBReleaseID = "";
    private int trackCount;

    private final static String[] genreDelimiters = {", ", ";", "\\", "/"};
    private final static String unknownValue = "[unknown]";
    private final static Pattern trackPattern = Pattern.compile("^\\d{1,2}");

    public Mp3AudioImpl(File file) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.file = file;
        fileName = new SimpleStringProperty(this, "fileName");
        artistTitle = new SimpleStringProperty(this, "artistTitle");
        albumTitle = new SimpleStringProperty(this, "albumTitle");
        trackNumber = new SimpleStringProperty(this, "trackNumber");
        trackTitle = new SimpleStringProperty(this, "trackTitle");
        year = new SimpleStringProperty(this, "year");
        origYear = new SimpleStringProperty(this, "origYear");
        label = new SimpleStringProperty(this, "label");
        catNum = new SimpleStringProperty(this, "catNum");
        discNumber = new SimpleIntegerProperty(this, "discNumber");
        genres = new SimpleListProperty(this, "genres");
        hasArtwork = new SimpleBooleanProperty(this, "hasArtwork");
        fileName.setValue(file.getName());
    }

    @Override
    public void initialize() {
        try {
            audioFile = (MP3File) AudioFileIO.read(file);

            if (audioFile.getTag() == null) {
                audioFile.setTag(new ID3v24Tag());
                try {
                    audioFile.commit();
                } catch (CannotWriteException ex) {
                    System.out.println(ex.getMessage());
                    System.out.println(file.getAbsolutePath());
                }
            }

            artistTitle.setValue(artistValue(audioFile));
            albumTitle.setValue(albumValue(audioFile));
            trackTitle.setValue(trackTitleValue(audioFile));
            year.setValue(yearValue(audioFile));
            origYear.setValue(origYearValue(audioFile));
            discNumber.set(discNumberValue(audioFile));
            genres.set(genresValue(audioFile));
            trackNumber.setValue(trackNumberValue(audioFile));
            hasArtwork.setValue(artworkValue(audioFile));
            MBReleaseID = MBReleaseIDValue(audioFile);
            label.setValue(labelValue(audioFile));
            catNum.setValue(catNumValue(audioFile));
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
            System.out.println(ex.getMessage() + " " + file.getAbsolutePath());
        }
    }

    private String artistValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.ARTIST))) {
            value = audioFile.getTag().getFirst(FieldKey.ARTIST);
        } else {
            value = unknownValue;
        }
        return value;
    }

    private String albumValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.ALBUM))) {
            value = audioFile.getTag().getFirst(FieldKey.ALBUM);
        } else {
            value = unknownValue;
        }
        return value;
    }

    private String MBReleaseIDValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.MUSICBRAINZ_RELEASEID))) {
            value = audioFile.getTag().getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
        } else {
            value = "";
        }
        return value;
    }

    private String labelValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.RECORD_LABEL))) {
            value = audioFile.getTag().getFirst(FieldKey.RECORD_LABEL);
        } else {
            value = "";
        }
        return value;
    }

    private String catNumValue(MP3File audioFile) {
        String value = "";
        List<TagField> tags = audioFile.getID3v2Tag().getFields("TXXX");
        for (TagField tag : tags) {
            value = tagValue(tag.toString(), "CATALOGNUMBER");
            if (!value.isEmpty()) {
                return value;
            }
        }

        /*if (!"".equals(audioFile.getTag().getFirst(FieldKey.valueOf("TXXX")))) {
            value = audioFile.getTag().getFirst(FieldKey.valueOf("TXXX"));
        } else {
            value = "";
        }*/
        return value;
    }

    private String trackTitleValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.TITLE))) {
            value = audioFile.getTag().getFirst(FieldKey.TITLE);
        } else {
            value = unknownValue;
        }
        return value;
    }

    private String trackNumberValue(MP3File audioFile) {
        String value;
        if (!"".equals(audioFile.getTag().getFirst(FieldKey.TRACK))) {
            value = audioFile.getTag().getFirst(FieldKey.TRACK);
        } else {
            value = String.valueOf(trackCount);
        }
        return value;
    }

    private String yearValue(MP3File audioFile) {
        String value;
        try {
            if (!"".equals(audioFile.getTag().getFirst(FieldKey.YEAR))) {
                value = audioFile.getTag().getFirst(FieldKey.YEAR);
            } else {
                value = "xxxx";
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("StringIndexOutOfBoundsException: " + e);
            System.out.println(audioFile.getFile().getAbsolutePath());
            value = "xxxx";
        }
        return value;
    }

    private String origYearValue(MP3File audioFile) {
        String value = "";
        try {
            if (!"".equals(audioFile.getTag().getFirst(FieldKey.ORIGINAL_YEAR))) {
                value = audioFile.getTag().getFirst(FieldKey.ORIGINAL_YEAR);
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("StringIndexOutOfBoundsException: " + e);
            System.out.println(audioFile.getFile().getAbsolutePath());
            value = "xxxx";
        }

        String origYear = "";
        List<TagField> tags = audioFile.getID3v2Tag().getFields("TXXX");
        for (TagField tag : tags) {
            origYear = tagValue(tag.toString(), "originalyear");
            if (!origYear.isEmpty()) {
                break;
            }
        }

        if (!value.isEmpty() && (Integer.valueOf(value) > Integer.valueOf(origYear))) {
            return origYear;
        }

        return value;
    }

    private int discNumberValue(MP3File audioFile) {
        int value = 0;
        String discNumberTag = audioFile.getTag().getFirst(FieldKey.DISC_NO);
        if (!"".equals(discNumberTag)) {
            discNumberTag = discNumberTag.replaceFirst("^0", "").replaceAll("\\/.+", "").replaceAll("\\D", "");
            if (!"".equals(discNumberTag)) {
                value = Integer.valueOf(discNumberTag);
            }
        }
        return value;
    }

    private ObservableList<String> genresValue(MP3File audioFile) {
        ObservableList<String> genresList = FXCollections.observableArrayList();
        String[] genres = splittedGenres(audioFile.getTag().getFirst(FieldKey.GENRE));
        if (genres.length > 0) {
            for (String genre : genres) {
                genresList.add(genre);
            }
        } else {
            genresList.add(audioFile.getTag().getFirst(FieldKey.GENRE));
        }
        return genresList;
    }

    private String[] splittedGenres(String genreString) {
        String[] splittedGenres = {};
        for (String delimiter : genreDelimiters) {
            if (genreString.contains(delimiter)) {
                splittedGenres = genreString.split(delimiter);
                break;
            }
        }
        return splittedGenres;
    }

    private boolean artworkValue(MP3File audioFile) {
        boolean value = false;
        if (!audioFile.getTag().getArtworkList().isEmpty()) {
            value = true;
        }
        return value;
    }

    public int getTrack() {
        int value = 0;
        String trackTag = audioFile.getTag().getFirst(FieldKey.TRACK);

        if (!trackTag.isEmpty()) {
            Matcher matcher = trackPattern.matcher(trackTag);
            if (matcher.find())
                value = Integer.valueOf(matcher.group(0));
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("File: ");
        builder.append(file);
        builder.append(System.lineSeparator());
        builder.append("Artist: ");
        builder.append(artistTitle.getValue());
        builder.append(System.lineSeparator());
        builder.append("Album: ");
        builder.append(albumTitle.getValue());
        builder.append(System.lineSeparator());
        builder.append("Track: ");
        builder.append(trackNumber.getValue());
        builder.append(System.lineSeparator());
        builder.append("Title: ");
        builder.append(trackTitle.getValue());
        builder.append(System.lineSeparator());
        builder.append("Year: ");
        builder.append(year.getValue());
        builder.append(System.lineSeparator());
        builder.append("Artwork: ");
        builder.append(hasArtwork.getValue());
        builder.append(System.lineSeparator());
        builder.append("Disc Number: ");
        builder.append(discNumber.getValue());
        builder.append(System.lineSeparator());
        builder.append("Genres: ");
        for (String genre : genres.getValue()) {
            builder.append(genre);
            builder.append(" / ");
        }
        builder.append(System.lineSeparator());
        builder.append("--------------------------");
        String res = builder.toString();
        return res;
    }

    private String tagValue(String tag, String key) {
        String value = "";
        if (tag.contains(key)) {
            value = tag.split("; ")[1];
            value = value.replaceAll("Text=", "").replaceAll("\"", "").replaceAll(";", "").replaceAll("\u0000", "");
        }
        return value;
    }

    @Override
    public AudioFile getAudioFile() {
        return audioFile;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getArtistTitle() {
        return artistTitle.getValue();
    }

    @Override
    public void setArtistTitle(String artistTitle) {
        this.artistTitle.setValue(artistTitle);
    }

    @Override
    public String getAlbumTitle() {
        return albumTitle.getValue();
    }

    @Override
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle.setValue(albumTitle);
    }

    @Override
    public String getTrackTitle() {
        return trackTitle.getValue();
    }

    @Override
    public void setTrackTitle(String trackTitle) {
        this.trackTitle.setValue(trackTitle);
    }

    @Override
    public String getTrackNumber() {
        return trackNumber.getValue();
    }

    @Override
    public void setTrackNumber(String trackNumber) {
        this.trackNumber.setValue(trackNumber);
    }

    @Override
    public String getYear() {
        return year.getValue();
    }

    @Override
    public String getOrigYear() {
        return origYear.getValue();
    }

    @Override
    public void setYear(String year) {
        this.year.setValue(year);
    }

    @Override
    public int getDiscNumber() {
        return discNumber.get();
    }

    @Override
    public void setDiscNumber(int discNumber) {
        this.discNumber.set(discNumber);
    }

    @Override
    public SimpleListProperty<String> getGenres() {
        return genres;
    }

    @Override
    public void setGenres(SimpleListProperty genres) {
        this.genres = genres;
    }

    @Override
    public boolean includesArtwork() {
        return hasArtwork.getValue();
    }

    @Override
    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }


    public File getFile() {
        return file;
    }

    public String getMBReleaseID() {
        return MBReleaseID;
    }

    public String getLabel() {
        return label.getValue();
    }

    public String getCatNum() {
        return catNum.getValue();
    }
}
