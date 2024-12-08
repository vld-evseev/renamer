package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.Artwork;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import com.scwot.renamer.core.utils.ExportFileHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class OrganizeService {

    private static final String FOLDER_NAME = "folder";
    private static final String COVERS_NAME = "Covers";


    public void organizeData(ReleaseScope releaseScope,
                             MediumScope mediumScope,
                             DirectoryScope dirScope,
                             File dest,
                             boolean va,
                             boolean multiCD) {
        organizeAudio(dirScope, dest, va, multiCD);
        organizeImages(releaseScope, mediumScope, dirScope, dest);
        organizeOthers(dirScope, dest);
    }

    public void batchOrganize(ReleaseScope releaseScope,
                              MediumScope mediumScope,
                              List<DirectoryScope> directoryScopes, File destination) {
        directoryScopes.forEach(scope -> {
            organizeImages(releaseScope, mediumScope, scope, destination);
            organizeOthers(scope, destination);
        });
    }

    public void organizeAudio(DirectoryScope dirScope, File destination, boolean va, boolean multiCD) {
        if (dirScope.hasAudio()) {
            dirScope.getListOfAudios().forEach(audio -> {
                File target = ExportFileHelper.updateNameIfNeeded(audio, destination, va, multiCD);
                ExportFileHelper.move(audio.getAudioFile().getFile(), target);
            });
        }
    }

    public void organizeImages(ReleaseScope releaseScope,
                               MediumScope mediumScope,
                               DirectoryScope dirScope,
                               File destination) {
        if (!dirScope.hasImages()) {
            return;
        }

        List<File> images = dirScope.getListOfImages();

        Artwork embeddedImage = releaseScope.getEmbeddedArtwork();
        Artwork folderArtwork = mediumScope == null ? null : mediumScope.getFolderArtwork();

        if (hasUnrenamedFolderImage(dirScope, images) && fileBasedArtworkIsSmaller(folderArtwork, embeddedImage)) {
            moveAndRenameFolderImage(destination, images);
            return;
        }

        if (hasRenamedFolderImage(dirScope, images)) {
            moveFolderImage(destination, images);
            return;
        }

        boolean isCoversDir = (images.size() > 1 && !dirScope.hasAudio()) ||
                (images.size() == 1 && !dirScope.hasAudio() && !dirScope.hasSubFolders());

        if (isCoversDir) {
            moveToCoversDir(destination, images);
            return;
        }

        // folder contains audio files and a bunch of images together
        if (images.size() > 1 && dirScope.hasAudio()) {
            File coversFolder = new File(destination, COVERS_NAME);
            if (coversFolder.mkdir()) {
                images.forEach(image -> {
                    String baseName = FilenameUtils.getBaseName(image.getName());
                    File target = baseName.equalsIgnoreCase("cover") || baseName.equalsIgnoreCase("folder")
                            ? new File(coversFolder.getParent(), FOLDER_NAME + EXTENSION_SEPARATOR + getExtension(image.getName()))
                            : new File(coversFolder, image.getName());
                    ExportFileHelper.move(image, target);
                });
            }
        // folder contains audio files and a single image
        } else if (images.size() == 1 && dirScope.hasAudio()) {
            // if image in folder has greater resolution than image embedded in file then
            // move the first image to a separate folder
            if (hasSmallerResolution(embeddedImage, folderArtwork)) {
                File coversFolder = new File(destination, COVERS_NAME);
                File target = new File(
                        coversFolder,
                        images.getFirst().getName());
                ExportFileHelper.move(images.getFirst(), target);
            }
        }
        // folder contains only images
        else {
            images.forEach(image -> ExportFileHelper.move(image, destination));
        }
    }

    private boolean fileBasedArtworkIsSmaller(Artwork folderArtwork, Artwork embeddedImage) {
        if (folderArtwork == null) {
            return false;
        }

        if (embeddedImage == null) {
            return true;
        }

        if (hasSmallerResolution(folderArtwork, embeddedImage)) return true;

        return folderArtwork.raw().length <= embeddedImage.raw().length;
    }

    private static boolean hasSmallerResolution(Artwork first, Artwork second) {
        if (first == null) return false;
        if (second == null) return false;

        return first.height() <= second.height() &&
                first.width() <= second.width();
    }

    public void organizeOthers(DirectoryScope directoryScope, File destination) {
        List<File> others = directoryScope.getListOfOthers();

        if (others.isEmpty()) {
            return;
        }

        ExportFileHelper.removeJunkFiles(others);
        others.forEach(other -> ExportFileHelper.move(other, destination));
    }

    private void moveFolderImage(File destination, List<File> imageFiles) {
        File folderImage = imageFiles.stream()
                .filter(file -> StringUtils.containsIgnoreCase(file.getName(), FOLDER_NAME))
                .findFirst()
                .orElseThrow();

        File renamedImage = new File(
                destination,
                FOLDER_NAME + EXTENSION_SEPARATOR + getExtension(folderImage.getName())
        );
        ExportFileHelper.move(folderImage, renamedImage);

        List<File> remainingImages = imageFiles.stream()
                .filter(file -> !StringUtils.equalsIgnoreCase(file.getName(), folderImage.getName()))
                .filter(file -> !StringUtils.containsIgnoreCase(file.getName(), "cover"))
                .filter(file -> !StringUtils.containsIgnoreCase(file.getName(), "folder"))
                .toList();

        moveToCoversDir(destination, remainingImages);
    }

    private void moveAndRenameFolderImage(File destination, List<File> imageFiles) {
        File oldImage = imageFiles.getFirst();
        File renamedImage = new File(
                destination,
                FOLDER_NAME + EXTENSION_SEPARATOR + getExtension(oldImage.getName())
        );
        ExportFileHelper.move(oldImage, renamedImage);
    }

    private void moveToCoversDir(File dest, List<File> imageFiles) {
        if (!imageFiles.isEmpty()) {
            var coversDir = new File(dest, COVERS_NAME);
            coversDir.mkdir();
            imageFiles.forEach(image -> ExportFileHelper.move(image, coversDir));
        }
    }

    private boolean hasRenamedFolderImage(DirectoryScope dirScope, List<File> imageFiles) {
        final boolean containsFolderImage = imageFiles.stream()
                .anyMatch(file -> StringUtils.containsIgnoreCase(file.getName(), FOLDER_NAME));
        return containsFolderImage && (dirScope.hasAudio() || dirScope.getDiskNumber() > 0);
    }

    private boolean hasUnrenamedFolderImage(DirectoryScope dirScope, List<File> imageFiles) {
        return imageFiles.size() == 1 && (dirScope.hasAudio() || dirScope.getDiskNumber() > 0);
    }

}
