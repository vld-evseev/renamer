package com.scwot.renamer.core.converter;


import com.scwot.renamer.core.service.MBService;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class MediumsToReleaseConverter implements Converter<List<MediumScope>, ReleaseScope>{

    private final MBService mbService;

    public MediumsToReleaseConverter(MBService mbService) {
        this.mbService = mbService;
    }

    public ReleaseScope convert(List<MediumScope> mediumScopeList) {
        SortedSet<String> artists = new TreeSet<>();
        SortedSet<String> albums = new TreeSet<>();
        SortedSet<String> genres = new TreeSet<>();
        SortedSet<String> years = new TreeSet<>();
        Map<String, String> yearAlbum = new HashMap<>();
        Set<String> labels = new LinkedHashSet<>();
        Set<String> catNums = new LinkedHashSet<>();

        final Stream<MediumScope> dirScopeStream = mediumScopeList.stream();

        final List<Mp3FileScope> mergedAudioList =
                dirScopeStream
                        .flatMap(mediumScope -> mediumScope.getAudioList().stream())
                        .collect(Collectors.toList());

        mergedAudioList.stream()
                .peek((audio) -> artists.addAll(Optional.ofNullable(audio.getArtists()).orElse(new ArrayList<>())))
                .peek((audio) -> albums.add(Optional.ofNullable(audio.getAlbumTitle()).orElse(EMPTY)))
                .peek((audio) -> {
                    final List<String> mergedYearList = new ArrayList<>();
                    mergedYearList.add(audio.getOrigYear());
                    mergedYearList.add(audio.getYear());
                    years.addAll(mergedYearList);
                })
                .peek((audio) -> genres.addAll(Optional.ofNullable(audio.getGenres()).orElse(new ArrayList<>())))
                .peek((audio) -> labels.addAll(Optional.ofNullable(audio.getLabels()).orElse(new ArrayList<>())))
                .peek((audio) -> catNums.addAll(Optional.ofNullable(audio.getCatNums()).orElse(new ArrayList<>())))
                .forEach((audio) -> yearAlbum.put(audio.getAlbumTitle(), audio.getYear()));

        final Map<String, String> catalogues = extractCatalogues(labels, catNums);
        final boolean isVA = mediumScopeList.getFirst().isVA();

        final String albumArtist = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getAlbumArtistTitle()))
                .filter(title -> title != null && !title.equalsIgnoreCase("[unknown]"))
                .findFirst()
                .or(() -> mergedAudioList.stream()
                        .flatMap(a -> Stream.of(a.getArtistTitle()))
                        .findFirst())
                .orElse(String.join(" / ", artists));

        final String albumArtistSort = mergedAudioList.stream()
                .flatMap(a -> Stream.of(resolveAlbumArtistSort(albumArtist))).findFirst().orElse(albumArtist);
        final String albumTitle = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getAlbumTitle())).findFirst().orElse(Mp3FileScope.UNKNOWN_VALUE);
        final String releaseCountry = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getReleaseCountry())).findFirst().orElse(null);
        final String releaseStatus = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getReleaseStatus())).findFirst().orElse(null);
        final String releaseType = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getReleaseType())).findFirst().orElse(null);
        final String releaseMBID = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getReleaseMBID())).findFirst().orElse(null);
        final String releaseGroupMBID = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getReleaseGroupMBID())).findFirst().orElse(null);
        final String artistMBID = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getArtistMBID())).findFirst().orElse(null);
        final String barcode = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getBarcode())).findFirst().orElse(null);

        byte[] firstImage = getFirstCoverIfPresent(mergedAudioList);

        final String yearReleased = Collections.max(years);
        final String yearRecorded = Collections.min(years);
        final List<String> topArtistGenres = genres.stream().limit(3).collect(Collectors.toList());

        final DirectoryScope rootScope = mediumScopeList.getFirst().getDirectoryScope().getRoot();
        final String artistCountry = mbService.findArtistCountry(artistMBID, albumArtist);

        return ReleaseScope.builder()
                .mediumScopeList(mediumScopeList)
                .root(rootScope)
                .catalogues(catalogues)
                .isVA(isVA)
                .albumArtist(albumArtist)
                .albumArtistSort(albumArtistSort)
                .albumTitle(albumTitle)
                .releaseCountry(releaseCountry)
                .releaseStatus(releaseStatus)
                .releaseType(releaseType)
                .releaseMBID(releaseMBID)
                .releaseGroupMBID(releaseGroupMBID)
                .barcode(barcode)
                .image(firstImage)
                .yearReleased(yearReleased)
                .yearRecorded(yearRecorded)
                .totalDiskNumber(mediumScopeList.size())
                .artists(artists)
                .albums(albums)
                .topArtistGenres(topArtistGenres)
                .years(years)
                .artistCountry(artistCountry)
                .build();
    }

    private byte[] getFirstCoverIfPresent(List<Mp3FileScope> mergedAudioList) {
        return mergedAudioList.stream()
                .map(Mp3FileScope::getImage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private String resolveAlbumArtistSort(String albumArtist) {
        if (albumArtist.length() > 3 && albumArtist.substring(0, 3).equalsIgnoreCase("the")) {
            return albumArtist.substring(3).trim() + ", The";
        }
        return albumArtist;
    }

    private Map<String, String> extractCatalogues(final Set<String> labels,
                                                  final Set<String> catNums) {
        addCatalogueNumberIfNotPresent(labels, catNums);

        final List<String> labelsList = new ArrayList<>(labels);
        final List<String> catNumsList = new ArrayList<>(catNums);

        if (catNumsList.size() != labelsList.size()) {
            final String firstCatNum = catNumsList.getFirst();

            return labelsList.stream()
                    .collect(
                            Collectors.toMap(
                                    Function.identity(),
                                    label -> firstCatNum,
                                    (existing, additional) -> additional,
                                    LinkedHashMap::new
                            ));
        }

        return IntStream.range(0, labelsList.size())
                .boxed()
                .collect(
                        Collectors.toMap(
                                labelsList::get,
                                catNumsList::get,
                                (existing, additional) -> existing + " / " + additional,
                                LinkedHashMap::new
                        )
                );
    }

    private void addCatalogueNumberIfNotPresent(final Set<String> labels,
                                                final Set<String> catNums) {
        final int labelsSize = labels.size();
        final int catNumsSize = catNums.size();
        final int diff = labelsSize - catNumsSize;
        if (diff > 0) {
            IntStream.range(0, diff).forEachOrdered(i -> catNums.add("none"));
        }
    }
}
