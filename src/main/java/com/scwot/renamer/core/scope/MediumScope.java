package com.scwot.renamer.core.scope;

import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

@Value
@Builder
public class MediumScope {

    DirectoryScope directoryScope;

    List<Mp3FileScope> audioList;
    SortedSet<String> artistSet;
    SortedSet<String> albumSet;
    SortedSet<String> genreSet;
    SortedSet<String> yearSet;
    HashMap<String, String> albumYearMap;

    int diskNumber;
    String albumTitle;
    String artistTitle;
    String originalYear;
    String releasedYear;
    List<String> labelList;
    List<String> catNumList;
    File artwork;
    boolean isVA;

    public boolean hasArtwork() {
        return !directoryScope.getListOfImages().isEmpty();
    }

}
