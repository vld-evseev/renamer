package com.scwot.renamer.core.utils;

import com.scwot.renamer.core.scope.ReleaseScope;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ExportRenameUtils {

    private static final String VA_VALUE = "VA";
    private static final int DEFAULT_MAX_TITLE_LENGTH = 70;

    public static String buildArtistDirName(ReleaseScope releaseScope) {
        if (releaseScope.isVA()) {
            return VA_VALUE;
        }

        StringBuilder sb = new StringBuilder(resolveArtistLevelDirName(releaseScope));
        String artistCountry = releaseScope.getArtistCountry();
        List<String> topArtistGenres = releaseScope.getTopArtistGenres();

        boolean hasCountry = StringUtils.isNotEmpty(artistCountry);
        boolean hasGenres = CollectionUtils.isNotEmpty(topArtistGenres);

        if (hasCountry || hasGenres) {
            sb.append(" [");
            if (hasCountry) {
                sb.append(artistCountry);
            }
            if (hasCountry && hasGenres) {
                sb.append(" ");
            }
            if (hasGenres) {
                sb.append(resolveGenreSubstring(topArtistGenres));
            }
            sb.append("]");
        }

        return sb.toString();
    }

    public static String buildAlbumDirName(ReleaseScope releaseScope) {
        StringBuilder sb = new StringBuilder();

        if (releaseScope.isVA()) {
            sb.append("VA - ");
        }

        sb.append(resolveYearRecordedSubstring(releaseScope))
                .append(" - ")
                .append(trimTitle(normalizeName(releaseScope.getAlbumTitle()), DEFAULT_MAX_TITLE_LENGTH));

        if (!releaseScope.getCatalogues().isEmpty()) {
            sb.append(" [")
                    .append(releaseScope.getYearReleased())
                    .append(", ");

            String label = releaseScope.getCatalogues().keySet().stream()
                    .filter(key -> !key.equals("[no label]"))
                    .findFirst()
                    .map(l -> normalizeName(l.replaceAll(" Records| Recordings", EMPTY)))
                    .orElse("no label");

            sb.append(label).append(", ");

            String catNum = releaseScope.getCatalogues().values().stream()
                    .filter(val -> !val.equals("[none]"))
                    .findFirst()
                    .map(ExportRenameUtils::normalizeName)
                    .orElse("none");

            sb.append(catNum).append("]");
        }

        return sb.append(resolveReleaseType(releaseScope))
                .append(resolveTotalDisksSubstring(releaseScope))
                .toString();
    }


    public static String normalizeName(String name) {
        return name.replaceAll("[$�`<>*\"?]", "")
                .replaceAll("\\\\", "")
                .replaceAll("/", "-")
                .replaceAll(":", " -");
    }

    public static String trimTitle(String title, int maxLength) {
        if (title.length() > maxLength) {
            String[] words = title.split(" ");
            return String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 5)));
        }
        return title;
    }

    private static String resolveGenreSubstring(List<String> topArtistGenres) {
        List<String> sortedGenres = topArtistGenres.stream().sorted().toList();
        Map<String, String> excludedGenres = new LinkedHashMap<>();

        for (String genreToSearch : sortedGenres) {
            for (String genre : sortedGenres) {
                if (!genre.equals(genreToSearch) &&
                        (genre.contains(genreToSearch) || genreToSearch.contains(genre))) {
                    String shorter = genre.length() < genreToSearch.length() ? genre : genreToSearch;
                    String longer = genre.equals(shorter) ? genreToSearch : genre;
                    excludedGenres.put(shorter, longer);
                }
            }
        }

        List<String> result = sortedGenres.stream()
                .filter(genre -> !excludedGenres.containsKey(genre) ||
                        !excludedGenres.containsValue(genre))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return normalizeName(String.join(", ", result));
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

        if (albumArtistSort == null) {
            return releaseScope.getAlbumArtist();
        }

        if (albumArtistSort.startsWith("The ")) {
            return normalizeName(albumArtistSort).replaceFirst("The ", EMPTY) + ", The";
        }

        return normalizeName(albumArtistSort);
    }
}
