package com.scwot.renamer.core.io;

import com.scwot.renamer.core.io.utils.ExportFileHelper;
import com.scwot.renamer.core.io.utils.ExportRenameUtils;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@Service
public class DefaultExportStrategy {

    public static final String FOLDER_NAME = "folder";
    public static final String COVERS_NAME = "Covers";


    public void execute(ReleaseScope releaseScope, File destination, boolean includeInArtistFolder) {
        if (!destination.exists()) {
            log.info("Destination does not exist");
            return;
        }


        final File newAlbumDirectory = createTopLevelDir(releaseScope, destination, includeInArtistFolder);

        final List<MediumScope> mediumScopeList = releaseScope.getMediumScopeList();
        for (MediumScope mediumScope : mediumScopeList) {

            if (releaseScope.getTotalDiskNumber() > 1) {
                final File cdDirectory = new File(newAlbumDirectory + File.separator + "CD" + mediumScope.getDiskNumber());
                cdDirectory.mkdir();
                organizeData(mediumScope.getDirectoryScope(), cdDirectory);
            } else {
                organizeData(mediumScope.getDirectoryScope(), newAlbumDirectory);

                for (DirectoryScope child : mediumScope.getDirectoryScope().getChildren()) {
                    organizeData(child, newAlbumDirectory);
                }
            }
        }

        final DirectoryScope root = releaseScope.getRoot();
        if (root != null) {
            final List<DirectoryScope> remaining =
                    root.getChildren().stream()
                            .filter(Predicate.not(DirectoryScope::hasAudio))
                            .collect(Collectors.toList());
            batchOrganize(remaining, newAlbumDirectory);
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

    private void batchOrganize(List<DirectoryScope> dirScopeList, File dest) {
        for (DirectoryScope directoryScope : dirScopeList) {
            organizeImages(directoryScope, dest);
            organizeOthers(directoryScope, dest);
        }
    }

    private void organizeData(DirectoryScope dirScope, File dest) {
        organizeAudio(dirScope, dest);
        organizeImages(dirScope, dest);
        organizeOthers(dirScope, dest);
    }

    private void organizeAudio(DirectoryScope dirScope, File dest) {
        if (!dirScope.hasAudio()) {
            return;
        }

        for (Mp3FileScope audio : dirScope.getListOfAudios()) {
            ExportFileHelper.move(audio.getAudioFile().getFile(), dest);
        }
    }

    private void organizeImages(DirectoryScope dirScope, File dest) {
        if (!dirScope.hasImages()) {
            return;
        }

        final List<File> listOfImages = dirScope.getListOfImages();

        if (isReleaseContainsNotRenamedFolderImage(dirScope, listOfImages)) {
            moveAndRenameSingleFolderImage(dest, listOfImages);
            return;
        }

        if (isReleaseContainsRenamedFolderImage(dirScope, listOfImages)) {
            moveSingleFolderImage(dest, listOfImages);
            return;
        }

        final boolean isCoversDir = (listOfImages.size() > 1 && !dirScope.hasAudio())
                || listOfImages.size() == 1 && !dirScope.hasAudio() && !dirScope.hasSubFolders();
        if (isCoversDir) {
            moveToCoversDir(dest, listOfImages);
        } else {
            if (listOfImages.size() > 1 && dirScope.hasAudio()) {
                File coversFolder = new File(dest + File.separator + COVERS_NAME);
                coversFolder.mkdir();
                for (File image : listOfImages) {
                    if (FilenameUtils.removeExtension(image.getName()).equalsIgnoreCase("cover") ||
                            FilenameUtils.removeExtension(image.getName()).equalsIgnoreCase("folder")) {
                        image.renameTo(new File(
                                coversFolder.getParentFile() + File.separator + FOLDER_NAME + EXTENSION_SEPARATOR +
                                        getExtension(image.getName()))
                        );
                    } else {
                        ExportFileHelper.move(image, coversFolder);
                    }
                }
            } else {
                for (File image : listOfImages) {
                    ExportFileHelper.move(image, dest);
                }
            }
        }
    }

    private void moveToCoversDir(File dest, List<File> listOfImages) {
        if (listOfImages.isEmpty()) {
            return;
        }

        File coversDir = new File(dest + File.separator + COVERS_NAME);
        coversDir.mkdir();
        for (File image : listOfImages) {
            ExportFileHelper.move(image, coversDir);
        }
    }

    private void moveSingleFolderImage(File dest, List<File> listOfImages) {
        final File oldFolderImage = listOfImages.stream()
                .filter(file -> StringUtils.containsIgnoreCase(file.getName(), FOLDER_NAME))
                .findFirst()
                .get();

        final File newFolderImage = new File(dest, FOLDER_NAME + EXTENSION_SEPARATOR + getExtension(oldFolderImage.getName()));
        ExportFileHelper.move(oldFolderImage, newFolderImage);

        final List<File> remainingImages = listOfImages.stream()
                .filter(file -> !StringUtils.equalsIgnoreCase(file.getName(), oldFolderImage.getName()))
                .filter(file -> !StringUtils.containsIgnoreCase(file.getName(), "cover"))
                .filter(file -> !StringUtils.containsIgnoreCase(file.getName(), "folder"))
                .collect(Collectors.toList());

        moveToCoversDir(dest, remainingImages);
    }

    private void moveAndRenameSingleFolderImage(File dest, List<File> listOfImages) {
        final File oldFolderImage = listOfImages.get(0);
        final File newFolderImage = new File(
                dest +
                        File.separator +
                        FOLDER_NAME +
                        EXTENSION_SEPARATOR +
                        getExtension(oldFolderImage.getName())
        );
        ExportFileHelper.move(oldFolderImage, newFolderImage);
    }

    private boolean isReleaseContainsRenamedFolderImage(DirectoryScope dirScope, List<File> listOfImages) {
        final boolean containsFolderImage = listOfImages.stream()
                .anyMatch(file -> StringUtils.containsIgnoreCase(file.getName(), FOLDER_NAME));
        return containsFolderImage && (dirScope.hasAudio() || dirScope.getDiskNumber() > 0);
    }

    private boolean isReleaseContainsNotRenamedFolderImage(DirectoryScope dirScope, List<File> listOfImages) {
        return listOfImages.size() == 1 && (dirScope.hasAudio() || dirScope.getDiskNumber() > 0);
    }

    private void organizeOthers(DirectoryScope directoryScope, File dest) {
        if (!directoryScope.hasOthers()) {
            return;
        }

        ExportFileHelper.removeJunkFiles(directoryScope.getListOfOthers());

        final List<File> listOfOthers = directoryScope.getListOfOthers();
        for (File other : listOfOthers) {
            ExportFileHelper.move(other, dest);
        }
    }

}
