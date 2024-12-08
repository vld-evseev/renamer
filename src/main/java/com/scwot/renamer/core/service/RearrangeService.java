package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.Artwork;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import com.scwot.renamer.core.utils.ExportRenameUtils;
import com.scwot.renamer.core.utils.ImageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RearrangeService {

    private final OrganizeService organizeData;

    public RearrangeService(OrganizeService organizeData) {
        this.organizeData = organizeData;
    }


    public void execute(ReleaseScope releaseScope, File destination, boolean includeInArtistFolder) {
        if (!destination.exists()) {
            log.info("Destination does not exist");
            return;
        }

        boolean va = releaseScope.isVA();
        boolean multiCD = releaseScope.isMultiCD();
        final File newAlbumDir = createTopLevelDir(releaseScope, destination, includeInArtistFolder);

        final List<MediumScope> mediumScopeList = releaseScope.getMediumScopeList();
        for (MediumScope mediumScope : mediumScopeList) {

            Artwork embeddedArtwork = releaseScope.getEmbeddedArtwork();
            DirectoryScope from = mediumScope.getDirectoryScope();
            if (releaseScope.getTotalDiskNumber() > 1) {
                var discSubtitle = mediumScope.getDiscSubtitle();
                var subtitle = discSubtitle.isEmpty() ? "" : " - " +
                        ExportRenameUtils.trimTitle(ExportRenameUtils.normalizeName(discSubtitle), 60);
                var cdDir = new File(
                        newAlbumDir + File.separator +
                                "CD" + mediumScope.getDiskNumber() + subtitle
                );
                cdDir.mkdir();
                organizeData.organizeData(releaseScope, mediumScope, mediumScope.getDirectoryScope(), cdDir, va, multiCD);

                ImageHelper.saveImage(embeddedArtwork, cdDir);
            } else {
                organizeData.organizeData(releaseScope, mediumScope, from, newAlbumDir, va, multiCD);
                ImageHelper.saveImage(embeddedArtwork, newAlbumDir);

                for (DirectoryScope child : from.getChildren()) {
                    organizeData.organizeData(releaseScope, mediumScope, child, newAlbumDir, va, multiCD);
                }
            }
        }

        final DirectoryScope root = releaseScope.getRoot();
        if (root != null) {
            final List<DirectoryScope> remaining =
                    root.getChildren().stream()
                            .filter(Predicate.not(DirectoryScope::hasAudio))
                            .collect(Collectors.toList());
            organizeData.batchOrganize(releaseScope, null, remaining, newAlbumDir);
        }
    }

    private File createTopLevelDir(ReleaseScope releaseScope, File destination, boolean includeInArtistFolder) {
        final File newAlbumDirectory;

        if (includeInArtistFolder && !releaseScope.isVA()) {
            final String artistPath =
                    destination.getAbsolutePath() +
                            File.separator +
                            ExportRenameUtils.buildArtistDirName(releaseScope);

            final File newArtistDirectory = new File(artistPath);
            newArtistDirectory.mkdir();

            final String albumPath = artistPath + File.separator + ExportRenameUtils.buildAlbumDirName(releaseScope);
            newAlbumDirectory = new File(albumPath);
        } else {
            final String albumPath = destination + File.separator + ExportRenameUtils.buildAlbumDirName(releaseScope);
            newAlbumDirectory = new File(albumPath);
        }

        newAlbumDirectory.mkdir();
        return newAlbumDirectory;
    }

}
