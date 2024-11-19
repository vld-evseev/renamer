package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.DirectoryScope;
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


    public void organizeData(DirectoryScope dirScope, File dest, boolean isVA) {
        organizeAudio(dirScope, dest, isVA);
        organizeImages(dirScope, dest);
        organizeOthers(dirScope, dest);
    }

    public void batchOrganize(List<DirectoryScope> directoryScopes, File destination) {
        directoryScopes.forEach(scope -> {
            organizeImages(scope, destination);
            organizeOthers(scope, destination);
        });
    }

    public void organizeAudio(DirectoryScope dirScope, File destination, boolean isVA) {
        if (dirScope.hasAudio()) {
            dirScope.getListOfAudios().forEach(audio -> {
                File target = ExportFileHelper.updateNameIfNeeded(audio, destination, isVA);
                ExportFileHelper.move(audio.getAudioFile().getFile(), target);
            });
        }
    }

    public void organizeImages(DirectoryScope dirScope, File destination) {
        if (!dirScope.hasImages()) {
            return;
        }

        List<File> images = dirScope.getListOfImages();

        if (hasUnrenamedFolderImage(dirScope, images)) {
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

        if (images.size() > 1 && dirScope.hasAudio()) {
            File coversFolder = new File(destination, COVERS_NAME);
            if (coversFolder.mkdir()) {
                images.forEach(image -> {
                    String baseName = FilenameUtils.getBaseName(image.getName());
                    File target = baseName.equalsIgnoreCase("cover") || baseName.equalsIgnoreCase("folder")
                            ? new File(coversFolder.getParent(), FOLDER_NAME + EXTENSION_SEPARATOR + getExtension(image.getName()))
                            : new File(coversFolder, image.getName());
                    image.renameTo(target);
                });
            }
        } else {
            images.forEach(image -> ExportFileHelper.move(image, destination));
        }
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
            if (coversDir.mkdir()) {
                imageFiles.forEach(image -> ExportFileHelper.move(image, coversDir));
            }
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
