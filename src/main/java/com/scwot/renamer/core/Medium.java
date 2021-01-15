package com.scwot.renamer.core;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class Medium {

    private final String VA_VALUE = "Various Artists";

    private FileLevelProperties properties;
    private ObservableList<Mp3FileWrapper> audioList;
    private final SimpleIntegerProperty discNumber;
    private final SimpleStringProperty album;
    private final SimpleStringProperty artist;
    private final SimpleStringProperty year;
    private final SimpleStringProperty origYear;
    private final SimpleStringProperty label;
    private final SimpleStringProperty catNum;
    private final SimpleStringProperty genres;
    private final HashMap<String, String> yearAlbum;
    private String MBReleaseID;

    public Medium(FileLevelProperties properties) {
        this.properties = properties;
        discNumber = new SimpleIntegerProperty(this, "discNumber");
        album = new SimpleStringProperty(this, "album");
        artist = new SimpleStringProperty(this, "artist");
        year = new SimpleStringProperty(this, "year");
        origYear = new SimpleStringProperty(this, "origYear");
        year.setValue("");
        genres = new SimpleStringProperty(this, "genres");
        label = new SimpleStringProperty(this, "label");
        catNum = new SimpleStringProperty(this, "catNum");
        audioList = properties.getListOfAudios();

        yearAlbum = new HashMap<>();
        initialize();
    }

    private void initialize() {
        SortedSet<String> artists = new TreeSet<>();
        SortedSet<String> albums = new TreeSet<>();
        SortedSet<String> genres = new TreeSet<>();
        SortedSet<String> years = new TreeSet<>();

        audioList.stream().map((audio) -> {
            artists.add(audio.getArtistTitle());
            return audio;
        }).map((audio) -> {
            albums.add(audio.getAlbumTitle());
            return audio;
        }).map((audio) -> {
            years.add(audio.getOrigYear());
            return audio;
        }).map((audio) -> {
            years.add(audio.getYear());
            return audio;
        }).map((audio) -> {
            audio.getGenres().stream().forEach((genre) -> {
                genres.add(genre);
            });
            return audio;
        }).map((audio) -> {
            if (audio.getDiscNumber() != 0) {
                discNumber.set(audio.getDiscNumber());
            }
            return audio;
        }).forEach((audio) -> {
            yearAlbum.put(audio.getAlbumTitle(), audio.getYear());
        });

        MBReleaseID = audioList.get(0).getMBReleaseID();
        label.setValue(audioList.get(0).getLabel());
        catNum.setValue(audioList.get(0).getCatNum());


        if (artists.size() > 1) {
            artist.set(VA_VALUE);
        } else {
            artist.set(artists.first());
        }

        year.setValue(Collections.max(years));
        origYear.setValue(Collections.min(years));

        if (albums.size() > 1) {
            String albStr = "";
            if (albums.size() == 2) {
                if (StringUtils.getLevenshteinDistance(albums.first(), albums.last()) < 2) {
                    albStr = albums.first();
                } else {
                    System.out.println("Album size = 2: \"" + albums.first() + "\", \"" + albums.last() + "\"");
                    for (int i = 0; i < albums.size(); i++) {
                        albStr += get(albums, i);
                        if (i < albums.size() - 1) {
                            albStr += " / ";
                        }
                    }
                }
            }
            album.set(albStr);
        } else {
            album.set(albums.first());
        }

        sortByTrackNumbers();

    }

    private void sortByTrackNumbers() {
        audioList.sort(new Comparator<Mp3FileWrapper>() {
            @Override
            public int compare(Mp3FileWrapper curr, Mp3FileWrapper next) {
                if (curr.getTrack() > next.getTrack()) {
                    return 1;
                } else if (curr.getTrack() < next.getTrack()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private static <E> E get(Collection<E> collection, int index) {
        Iterator<E> i = collection.iterator();
        E element = null;
        while (i.hasNext() && index-- >= 0) {
            element = i.next();
        }
        return element;
    }

    public ObservableList<Mp3FileWrapper> getAudioList() {
        return audioList;
    }

    public void setAudioList(ObservableList<Mp3FileWrapper> audioList) {
        this.audioList = audioList;
    }

    public int getDiscNubmer() {
        return discNumber.getValue();
    }

    public void setDiscNubmer(int cdN) {
        this.discNumber.setValue(cdN);
    }

    public String getAlbum() {
        return album.getValue();
    }

    public void setAlbum(String album) {
        this.album.setValue(album);
    }

    public String getArtist() {
        return artist.getValue();
    }

    public void setArtist(String artist) {
        this.artist.setValue(artist);
    }

    public String getYear() {
        return year.getValue();
    }

    public void setYear(String year) {
        this.year.setValue(year);
    }

    public String getGenres() {
        return genres.getValue();
    }

    public void setGenres(String genres) {
        this.genres.setValue(genres);
    }

    public FileLevelProperties getProperties() {
        return properties;
    }

    public HashMap<String, String> getYearAlbum() {
        return yearAlbum;
    }


    public boolean hasArtwork() {
        return properties.getListOfImages().size() == 1;
    }

    public File getArtwork() {
        return properties.getListOfImages().get(0);
    }

    public boolean isVA() {
        return artist.getValue().equals(VA_VALUE);
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

    public String getOrigYear() {
        return origYear.getValue();
    }
}
