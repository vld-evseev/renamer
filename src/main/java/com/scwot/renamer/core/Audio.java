package com.scwot.renamer.core;

import javafx.beans.property.SimpleListProperty;
import org.jaudiotagger.audio.AudioFile;

import java.io.File;

public interface Audio {

    void initialize();

    AudioFile getAudioFile();

    String getFileName();

    String getArtistTitle();

    void setArtistTitle(String artistTitle);

    String getAlbumTitle();

    void setAlbumTitle(String albumTitle);

    String getTrackTitle();

    void setTrackTitle(String trackTitle);

    String getTrackNumber();

    void setTrackNumber(String trackNumber);

    String getYear();

    String getOrigYear();

    void setYear(String year);

    int getDiscNumber();

    void setDiscNumber(int discNumber);

    SimpleListProperty<String> getGenres();

    void setGenres(SimpleListProperty genres);

    void setTrackCount(int trackCount);

    boolean includesArtwork();

    String getMBReleaseID();

    File getFile();

    int getTrack();

    String getLabel();

    String getCatNum();
}
