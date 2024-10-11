package com.scwot.renamer.core.io.utils;

import com.scwot.renamer.core.scope.ReleaseScope;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;


public class ExportRenameUtils {

    private static final String VA_VALUE = "VA";

    public static String buildArtistDirName(ReleaseScope releaseScope) {
        if (releaseScope.isVA()) {
            return VA_VALUE;
        }

        final StringBuilder sb = new StringBuilder();

        final String resolveArtistLevelDirName = resolveArtistLevelDirName(releaseScope);
        sb.append(resolveArtistLevelDirName);

        final String artistCountry = releaseScope.getArtistCountry();
        final List<String> topArtistGenres = releaseScope.getTopArtistGenres();

        if (StringUtils.isNotEmpty(artistCountry) || CollectionUtils.isNotEmpty(topArtistGenres)) {
            sb.append(" [");
        }

        if (StringUtils.isNotEmpty(artistCountry)) {
            sb.append(artistCountry);
        }

        if (StringUtils.isNotEmpty(artistCountry) && CollectionUtils.isNotEmpty(topArtistGenres)) {
            sb.append(" ");
        }

        if (CollectionUtils.isNotEmpty(topArtistGenres)) {
            final String joinedTopArtistGenres = resolveGenreSubstring(topArtistGenres);
            sb.append(joinedTopArtistGenres);
        }

        if (StringUtils.isNotEmpty(artistCountry) || CollectionUtils.isNotEmpty(topArtistGenres)) {
            sb.append("]");
        }

        return sb.toString();
    }

    private static String resolveGenreSubstring(List<String> topArtistGenres) {
        final List<String> result = new ArrayList<>();
        final Map<String, String> allExcluded = new LinkedHashMap<>();

        for (String genreToSearch : topArtistGenres.stream().sorted().collect(Collectors.toList())) {
            boolean added = false;
            final List<String> filteredGenres =
                    topArtistGenres.stream()
                            //.filter(genre -> !genre.equals(genreToSearch))
                            .sorted()
                            .collect(Collectors.toList());

            for (String genre : filteredGenres) {
                if (genre.contains(genreToSearch) || genreToSearch.contains(genre)) {
                    if (genre.length() > genreToSearch.length()) {
                        allExcluded.put(genreToSearch, genre);
                    } else if (genre.length() < genreToSearch.length()) {
                        allExcluded.put(genre, genreToSearch);
                    }
                }
            }
        }

        for (String topArtistGenre : topArtistGenres) {
            if (allExcluded.containsKey(topArtistGenre)) {
                result.add(allExcluded.get(topArtistGenre));
            } else if (!allExcluded.containsValue(topArtistGenre)) {
                result.add(topArtistGenre);
            }
        }

        final String joinedTopArtistGenres = String.join(", ",
                result.stream().sorted().collect(Collectors.toList()));
        return normalizeName(joinedTopArtistGenres);
    }

    public static String buildAlbumDirName(ReleaseScope releaseScope) {
        final StringBuilder sb = new StringBuilder();
        if (releaseScope.isVA()) {
            sb.append("VA - ");
        }

        final String yearSubstring = resolveYearRecordedSubstring(releaseScope);
        sb.append(yearSubstring);

        final String normalizedAlbumTitle = trimTitle(normalizeName(releaseScope.getAlbumTitle()));
        sb.append(" - ").append(normalizedAlbumTitle);

        if (releaseScope.getCatalogues().isEmpty()) {
            return sb.toString();
        }

        final String yearReleasedSubstring = releaseScope.getYearReleased();
        sb.append(" [");
        sb.append(yearReleasedSubstring).append(", ");

        final boolean withoutLabel = releaseScope.getCatalogues().keySet().stream().anyMatch(s -> s.equals("[no label]"));
        if (withoutLabel) {
            sb.append("no label").append(", ");
        } else {
            final List<String> labelList = new ArrayList<>(releaseScope.getCatalogues().keySet());
            final String firstLabelSubstring = labelList.get(0);
            final String shortLabelSubstring = firstLabelSubstring
                    .replaceAll(" Records", EMPTY)
                    .replaceAll(" Recordings", EMPTY);
            sb.append(normalizeName(shortLabelSubstring)).append(", ");
        }

        final boolean withoutCatNum = releaseScope.getCatalogues().values().stream().anyMatch(s -> s.equals("[none]"));
        if (withoutCatNum) {
            sb.append("none");
        } else {
            final List<String> catNumList = new ArrayList<>(releaseScope.getCatalogues().values());
            final String catNumListSubstring = catNumList.get(0);
            sb.append(normalizeName(catNumListSubstring));
        }

        sb.append("]");
        sb.append(resolveReleaseType(releaseScope));
        sb.append(resolveTotalDisksSubstring(releaseScope));

        return sb.toString();
    }

    private static String resolveTotalDisksSubstring(ReleaseScope releaseScope) {
        final int totalDiskNumber = releaseScope.getTotalDiskNumber();
        if (totalDiskNumber > 1){
            return " [" + totalDiskNumber + "CD]";
        }
        return EMPTY;
    }

    private static String resolveReleaseType(ReleaseScope releaseScope) {
        if (releaseScope.isVA()) {
            return EMPTY;
        }

        final String releaseType = releaseScope.getReleaseType();
        if (StringUtils.isNotEmpty(releaseType) &&
                !releaseType.equalsIgnoreCase("album")) {
            final String[] multipleTypes = releaseType.split("/");
            if (multipleTypes.length > 1) {
                return " [" + multipleTypes[1] + "]";
            }

            return " [" + releaseType + "]";
        }
        return EMPTY;
    }

    private static String resolveYearRecordedSubstring(ReleaseScope releaseScope) {
        final String yearRecorded = releaseScope.getYearRecorded();
        if (StringUtils.isNotBlank(yearRecorded) && !yearRecorded.equalsIgnoreCase("xxxx")) {
            return yearRecorded;
        }

        return releaseScope.getYearReleased();
    }

    private static String resolveArtistLevelDirName(ReleaseScope releaseScope) {
        final String albumArtistSort = releaseScope.getAlbumArtistSort();
        if (albumArtistSort.startsWith("The ")) {
            return normalizeName(albumArtistSort).replaceFirst("The ", EMPTY) + ", The";
        }

        return normalizeName(albumArtistSort);
    }

    public static String normalizeName(String name) {
        return name.replaceAll("[$�`<>*\"?]", "")
                .replaceAll("\\\\", "")
                .replaceAll("\\\\", "")
                .replaceAll("/", "-")
                .replaceAll(":", " -");
    }

    public static String trimTitle(String title) {
        String[] trimmed = title.split(" ");

        if (title.length() > 70) {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (String string : trimmed) {
                stringBuilder.append(string);

                if (i < 4) {
                    stringBuilder.append(" ");
                } else {
                    return stringBuilder.toString();
                }

                i++;
            }
        }

        return title;
    }
}
