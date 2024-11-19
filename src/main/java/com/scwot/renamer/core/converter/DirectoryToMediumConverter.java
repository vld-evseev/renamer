package com.scwot.renamer.core.converter;

import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DirectoryToMediumConverter implements Converter<DirectoryScope, MediumScope> {

    private final String VA_VALUE = "Various Artists";

    public MediumScope convert(DirectoryScope directoryScope) {
        final List<Mp3FileScope> audioList = directoryScope.getListOfAudios();

        final SortedSet<String> artistSet = new TreeSet<>();
        final SortedSet<String> albumSet = new TreeSet<>();
        final SortedSet<String> genreSet = new TreeSet<>();
        final SortedSet<String> yearSet = new TreeSet<>();
        final Map<String, String> albumYearMap = new HashMap<>();
        final AtomicInteger diskNumber = new AtomicInteger();

        audioList.stream()
                .peek((audio) ->
                        artistSet.add(audio.getArtistTitle()))
                .peek((audio) ->
                        albumSet.add(audio.getAlbumTitle()))
                .peek((audio) ->
                        yearSet.add(audio.getOrigYear()))
                .peek((audio) ->
                        yearSet.add(audio.getYear()))
                .peek((audio) ->
                        genreSet.addAll(audio.getGenres()))
                .peek((audio) -> {
                    if (audio.getDiscNumber() != 0) {
                        diskNumber.set(audio.getDiscNumber());
                    }
                })
                .forEach((audio) ->
                        albumYearMap.put(audio.getAlbumTitle(), audio.getYear()));

        final String discSubtitle = audioList.stream()
                .flatMap(a -> Stream.of(a.getDiscSubtitle()))
                .findFirst()
                .orElse("");

        final List<String> labelList = audioList.stream()
                .flatMap(mp3FileScope ->
                        mp3FileScope.getLabels().stream())
                .distinct()
                .collect(Collectors.toList());

        final List<String> catNumList = audioList.stream()
                .flatMap(mp3FileScope ->
                        mp3FileScope.getCatNums().stream())
                .distinct()
                .collect(Collectors.toList());

        final String artistTitle = buildArtistTitle(artistSet);
        final String albumTitle = buildAlbumTitle(albumSet);

        final String releasedYear = Collections.max(yearSet);
        final String originalYear = Collections.min(yearSet);

        final boolean isVA = albumTitle.equalsIgnoreCase(VA_VALUE);

        final File firstArtwork = directoryScope.getListOfImages().stream().findFirst().orElse(null);

        sortByTrackNumbers(audioList);

        directoryScope.setDiskNumber(diskNumber.get());

        return MediumScope.builder()
                .directoryScope(directoryScope)
                .audioList(audioList)
                .artistTitle(artistTitle)
                .albumTitle(albumTitle)
                .artistSet(artistSet)
                .albumSet(albumSet)
                .yearSet(yearSet)
                .genreSet(genreSet)
                .originalYear(originalYear)
                .releasedYear(releasedYear)
                .albumYearMap(albumYearMap)
                .labelList(labelList)
                .catNumList(catNumList)
                .isVA(isVA)
                .artwork(firstArtwork)
                .diskNumber(diskNumber.get())
                .discSubtitle(discSubtitle)
                .build();
    }

    private String buildAlbumTitle(SortedSet<String> albums) {
        if (albums.size() > 1) {
            StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
            if (albums.size() == 2) {

                if (StringUtils.getLevenshteinDistance(albums.first(), albums.last()) < 2) {
                    sb = new StringBuilder(albums.first());
                } else {
                    log.info(String.format("Album size = 2: \"%s\", \"%s\"", albums.first(), albums.last()));
                    for (int i = 0; i < albums.size(); i++) {
                        sb.append(get(albums, i));
                        if (i < albums.size() - 1) {
                            sb.append(" / ");
                        }
                    }
                }
            }
            return sb.toString();
        }

        return albums.first();
    }

    private String buildArtistTitle(SortedSet<String> artists) {
        if (artists.size() > 1) {
            return VA_VALUE;
        }

        return artists.first();
    }

    private void sortByTrackNumbers(List<Mp3FileScope> audioList) {
        audioList.sort(Comparator.comparingInt(Mp3FileScope::getTrack));
    }

    private static <E> E get(Collection<E> collection, int index) {
        Iterator<E> i = collection.iterator();
        E element = null;
        while (i.hasNext() && index-- >= 0) {
            element = i.next();
        }
        return element;
    }

}
