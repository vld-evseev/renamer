package com.scwot.renamer.core.musicbrainz;

import com.scwot.renamer.core.utils.enums.Country;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.musicbrainz.controller.Artist;
import org.musicbrainz.controller.Release;
import org.musicbrainz.model.searchresult.ArtistResultWs2;
import org.musicbrainz.model.searchresult.ReleaseResultWs2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MBService {

    /*
    * results.get(0).getArtist().getTags().stream()
    * .sorted(Comparator.comparingLong(TagWs2::getCount).reversed())
    * .limit(5)
    * .collect(Collectors.toList())
    * */

    public String findArtistCountry(String artistName) {
        Artist artist = new Artist();
        artist.search(artistName);
        artist.getSearchFilter().setLimit((long) 5);
        artist.getSearchFilter().setMinScore((long) 99);
        final List<ArtistResultWs2> results = artist.getFirstSearchResultPage();
        if (!results.isEmpty()) {
            final String country = results.get(0).getArtist().getCountry();
            final boolean exists = EnumUtils.isValidEnum(Country.class, country);
            if (exists) {
                return Country.valueOf(country).getFullName();
            }

            return country;
        }

        return StringUtils.EMPTY;
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
