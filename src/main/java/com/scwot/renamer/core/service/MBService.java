package com.scwot.renamer.core.service;

import com.scwot.renamer.core.enums.Country;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.controller.Artist;
import org.musicbrainz.controller.Release;
import org.musicbrainz.model.entity.ArtistWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MBService {

    public String findArtistCountry(String artistName) {
        Artist artist = new Artist();
        artist.search(artistName);
        artist.getSearchFilter().setLimit(5L);
        artist.getSearchFilter().setMinScore(99L);

        return artist.getFirstSearchResultPage().stream()
                .map(result -> result.getArtist().getCountry())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .map(country -> EnumUtils.isValidEnum(Country.class, country)
                        ? Country.valueOf(country).getFullName()
                        : country)
                .orElse(StringUtils.EMPTY);
    }

    @SneakyThrows
    public String findArtistCountry(String artistMBID, String albumArtist) {
        Artist artist = new Artist();

        if (StringUtils.isBlank(artistMBID)) {
            return findArtistCountry(albumArtist);
        }

        ArtistWs2 artistWs2 = artist.lookUp(artistMBID);
        String country = artistWs2.getCountry();

        if (StringUtils.isBlank(country)) {
            return findArtistCountry(albumArtist);
        }

        return EnumUtils.isValidEnum(Country.class, country)
                ? Country.valueOf(country).getFullName()
                : country;
    }

    @SneakyThrows
    public List<ReleaseResultWs2> searchByArtistAndAlbum(String artistName, String albumName) {
        Release release = new Release();

        release.search(albumName + " AND artist:" + artistName);
        release.getSearchFilter().setLimit((long) 1);
        release.getSearchFilter().setMinScore((long) 99);
        final List<ReleaseResultWs2> results = release.getFirstSearchResultPage();
        return results;
    }

}
