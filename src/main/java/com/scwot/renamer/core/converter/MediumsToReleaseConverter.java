package com.scwot.renamer.core.converter;


import com.scwot.renamer.core.musicbrainz.MBService;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
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

        byte[] thumbImage;

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

        final Map<String, String> catalogues = createCatalogues(labels, catNums);
        final boolean isVA = mediumScopeList.get(0).isVA();

        final String albumArtist = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getAlbumArtistTitle())).findFirst().orElse(String.join(" / ", artists));
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
        final String barcode = mergedAudioList.stream()
                .flatMap(a -> Stream.of(a.getBarcode())).findFirst().orElse(null);

        byte[] firstImage = getFirstCoverIfPresent(mergedAudioList);

        final String yearReleased = Collections.max(years);
        final String yearRecorded = Collections.min(years);
        final List<String> topArtistGenres = genres.stream().limit(3).collect(Collectors.toList());

        final DirectoryScope rootScope = mediumScopeList.get(0).getDirectoryScope().getRoot();
        final String artistCountry = mbService.findArtistCountry(albumArtist);

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
                .topArtistGenres(topArtistGenres) // we also have "genres" for remaining
                .years(years)
                .artistCountry(artistCountry)
                .build();
    }

    private byte[] getFirstCoverIfPresent(List<Mp3FileScope> mergedAudioList) {
        byte[] firstImage = null;
        for (Mp3FileScope mp3FileScope : mergedAudioList) {
            final byte[] image = mp3FileScope.getImage();
            if (image != null){
                firstImage = image;
                break;
            }
        }
        return firstImage;
    }

    private String resolveAlbumArtistSort(String albumArtist) {
        final String[] chunks = albumArtist.split(" ");
        if (chunks.length > 1 &&
                chunks[0].length() == 3 &&
                StringUtils.startsWithIgnoreCase(albumArtist, "the")) {
            return albumArtist.replaceFirst("^([Tt])he", EMPTY).trim() + ", The";
        }

        return albumArtist;
    }

    private Map<String, String> createCatalogues(final Set<String> labels,
                                                 final Set<String> catNums) {
        addCatalogueNumberIfNotPresent(labels, catNums);

        final ArrayList<String> labelsList = new ArrayList<>(labels);
        final ArrayList<String> catNumsList = new ArrayList<>(catNums);

        if (catNumsList.size() > labelsList.size() || catNumsList.size() < labelsList.size()) {
            final String firstCatNum = catNumsList.get(0);

            return labelsList.stream()
                    .collect(Collectors.toMap(Function.identity(), label -> firstCatNum, (a, b) -> b, LinkedHashMap::new));
        }

        return IntStream.range(0, labelsList.size())
                .boxed()
                .collect(Collectors.toMap(labelsList::get, catNumsList::get, (s, a) -> s + " / " + a, LinkedHashMap::new));
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
