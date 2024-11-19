package com.scwot.renamer.core.service;

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
        final File newAlbumDirectory = createTopLevelDir(releaseScope, destination, includeInArtistFolder);

        final List<MediumScope> mediumScopeList = releaseScope.getMediumScopeList();
        for (MediumScope mediumScope : mediumScopeList) {

            if (releaseScope.getTotalDiskNumber() > 1) {
                var discSubtitle = mediumScope.getDiscSubtitle();
                var subtitle = discSubtitle.isEmpty() ? "" : " - " +
                        ExportRenameUtils.trimTitle(ExportRenameUtils.normalizeName(discSubtitle), 60);
                var cdDirectory = new File(
                        newAlbumDirectory + File.separator +
                                "CD" + mediumScope.getDiskNumber() + subtitle
                );
                cdDirectory.mkdir();
                organizeData.organizeData(mediumScope.getDirectoryScope(), cdDirectory, va);
                ImageHelper.saveImage(releaseScope.getImage(), cdDirectory);
            } else {
                organizeData.organizeData(mediumScope.getDirectoryScope(), newAlbumDirectory, va);
                ImageHelper.saveImage(releaseScope.getImage(), newAlbumDirectory);

                for (DirectoryScope child : mediumScope.getDirectoryScope().getChildren()) {
                    organizeData.organizeData(child, newAlbumDirectory, va);
                }
            }
        }

        final DirectoryScope root = releaseScope.getRoot();
        if (root != null) {
            final List<DirectoryScope> remaining =
                    root.getChildren().stream()
                            .filter(Predicate.not(DirectoryScope::hasAudio))
                            .collect(Collectors.toList());
            organizeData.batchOrganize(remaining, newAlbumDirectory);
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
