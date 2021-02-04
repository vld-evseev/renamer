package com.scwot.renamer.core.scope;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

@Data
@Builder
public class ReleaseScope {

    private DirectoryScope root;

    private List<MediumScope> mediumScopeList;
    private boolean isVA;
    private String artistCountry;
    private List<String> topArtistGenres;

    private SortedSet<String> artists;
    private SortedSet<String> albums;
    private SortedSet<String> genres;
    private SortedSet<String> years;
    /*private Set<String> labels;
    private Set<String> catNums;*/
    private Map<String, String> yearAlbum;

    private String yearRecorded;
    private String yearReleased;

    private String albumArtist;
    private String albumArtistSort;
    private String albumTitle;
    private String releaseMBID;
    private String releaseGroupMBID;
    private String releaseCountry;
    private String releaseStatus;
    private String releaseType;

    private String barcode;

    private byte[] image;
    private byte[] thumbImage;

    private int totalDiskNumber;

    private Map<String, String> catalogues;

    public boolean isVA() {
        return albumArtist.equalsIgnoreCase("Various Artists");
    }
}
