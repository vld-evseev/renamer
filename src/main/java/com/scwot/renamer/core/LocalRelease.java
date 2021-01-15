package com.scwot.renamer.core;


import javafx.collections.ObservableList;

import java.io.File;

public class LocalRelease {

    private InputStrategy strategy;

    private FileLevelProperties root;
    private ObservableList<Medium> mediums;

    private String artistTitle;
    private String albumTitle;
    private String type;
    private String year;
    private String origYear;
    private String MBReleaseID;
    private int cdCount;
    private String label;
    private String catNumber;

    public LocalRelease() {
        this.artistTitle = "";
        this.albumTitle = "";
        this.type = "";
        this.year = "";
        this.cdCount = 0;
        this.label = "";
        this.catNumber = "";
    }

    public void build(InputStrategy strategy, File inputDir) {
        this.strategy = strategy;

        try {
            this.strategy.execute(inputDir);
            this.root = strategy.getRoot();
            this.mediums = strategy.getMediums();
            this.artistTitle = strategy.getArtist();
            this.albumTitle = strategy.getAlbum();
            this.year = strategy.getYear();
            this.origYear = strategy.getOrigYear();
            this.MBReleaseID = strategy.getMBReleaseID();
            this.cdCount = strategy.getCdCount();
            this.label = strategy.getLabel();
            this.catNumber = strategy.getCatNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LocalRelease{" +
                "artistTitle='" + artistTitle + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", type='" + type + '\'' +
                ", year='" + year + '\'' +
                ", origYear='" + origYear + '\'' +
                ", cdCount=" + cdCount +
                '}';
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArtistTitle() {
        return this.artistTitle;
    }

    public String getAlbumTitle() {
        return this.albumTitle;
    }

    public String getYear() {
        return this.year;
    }

    public int getCdCount() {
        return this.cdCount;
    }

    public ObservableList<Medium> getMediums() {
        return this.mediums;
    }

    public void setArtistTitle(String artistTitle) {
        this.artistTitle = artistTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public FileLevelProperties getRoot() {
        return this.root;
    }

    public boolean hasArtwork() {
        return this.root.getListOfImages().size() == 1;
    }

    public File getArtwork() {
        return this.root.getListOfImages().get(0);
    }

    public boolean isVA() {
        boolean va = false;
        for (Medium medium : this.mediums) {
            if (medium.isVA()) {
                va = true;
                break;
            }
        }
        return va;
    }

    public String getOrigYear() {
        return this.origYear;
    }

    public String getMBReleaseID() {
        return this.MBReleaseID;
    }

    public String getLabel() {
        return label;
    }

    public String getCatNumber() {
        return catNumber;
    }
}
