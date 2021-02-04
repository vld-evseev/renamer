package com.scwot.renamer.core.strategy;

import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import com.scwot.renamer.core.strategy.utils.ExportRenameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
            log.info("Destination not exists");
            return;
        }

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

        if (releaseScope.getRoot() != null) {
            final List<DirectoryScope> remaining =
                    releaseScope.getRoot().getChildren().stream().filter(directoryScope -> !directoryScope.hasAudio()).collect(Collectors.toList());
            batchOrganize(remaining, newAlbumDirectory);
            organizeData(releaseScope.getRoot(), newAlbumDirectory);
        }
    }

    private void batchOrganize(List<DirectoryScope> dirScopeList, File dest) {
        for (DirectoryScope directoryScope : dirScopeList) {
            //organizeAudio(directoryScope, dest);
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
            move(audio.getAudioFile().getFile(), dest);
        }
    }

    private void organizeImages(DirectoryScope dirScope, File dest) {
        if (!dirScope.hasImages()) {
            return;
        }

        final List<File> listOfImages = dirScope.getListOfImages();

        if (isReleaseContainsNotRenamedFolderImage(dirScope, listOfImages)) {
            moveAndRenameSingleFolderImage(dest, listOfImages);
        } else if (isReleaseContainsRenamedFolderImage(dirScope, listOfImages)) {
            moveSingleFolderImage(dest, listOfImages);
        } else {
            final boolean isCoversDir = (listOfImages.size() > 1 && !dirScope.hasAudio())
                    || listOfImages.size() == 1 && !dirScope.hasAudio() && !dirScope.hasInnerFolders();
            if (isCoversDir) {
                moveToCoversDir(dest, listOfImages);
            } else {
                if (listOfImages.size() > 1 && dirScope.hasAudio()) {
                    File coversFolder = new File(dest + File.separator + COVERS_NAME);
                    coversFolder.mkdir();
                    for (File image : listOfImages) {
                        move(image, coversFolder);
                    }
                } else {
                    for (File image : listOfImages) {
                        move(image, dest);
                    }
                }
            }
        }
    }

    private void moveToCoversDir(File dest, List<File> listOfImages) {
        if (listOfImages.isEmpty()){
            return;
        }

        File coversDir = new File(dest + File.separator + COVERS_NAME);
        coversDir.mkdir();
        for (File image : listOfImages) {
            move(image, coversDir);
        }
    }

    private void moveSingleFolderImage(File dest, List<File> listOfImages) {
        final File oldFolderImage = listOfImages.stream()
                .filter(file -> StringUtils.containsIgnoreCase(file.getName(), FOLDER_NAME))
                .findFirst()
                .get();

        final File newFolderImage = new File(
                dest +
                        File.separator +
                        FOLDER_NAME +
                        EXTENSION_SEPARATOR +
                        getExtension(oldFolderImage.getName())
        );
        move(oldFolderImage, newFolderImage);

        final List<File> remainingImages =
                listOfImages.stream().filter(file -> !file.getName().equalsIgnoreCase(oldFolderImage.getName())).collect(Collectors.toList());
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
        move(oldFolderImage, newFolderImage);
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

        removeJunkFiles(directoryScope.getListOfOthers());

        final List<File> listOfOthers = directoryScope.getListOfOthers();
        for (File other : listOfOthers) {
            move(other, dest);
        }
    }

    private void move(File from, File to) {
        try {
            if (from.exists()) {
                if (from.isDirectory() && to.isDirectory()) {
                    FileUtils.moveDirectory(from, to);
                } else if (from.isFile() && to.isDirectory()) {
                    FileUtils.moveFileToDirectory(from, to, true);
                } else {
                    FileUtils.moveFile(from, to);
                }
            }
        } catch (IOException e) {
            System.out.println("Can't move from " + from + " To " + to);
            //e.printStackTrace();
        }
    }

    /*private void removeFoldersIfEmpty(Path path) {
        String rootPath = localRelease.getRoot().getCurrentDir().getValue().getAbsolutePath().charAt(0) + ":\\";
        if (path == null || path.endsWith(rootPath)) {
            return;
        }

        if (Files.isDirectory(path)) {
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                if (path.toFile().listFiles().length > 0) {
                    for (File file : path.toFile().listFiles()) {
                        if (file.isDirectory()) {
                            removeFoldersIfEmpty(file.toPath());
                        }
                    }
                    return;
                } else {
                    return;
                }
            }
        }

        removeFoldersIfEmpty(path.getParent());
    }*/

    private void removeJunkFiles(List<File> listOfOtherFiles) {
        Iterator<File> it = listOfOtherFiles.iterator();

        while (it.hasNext()) {
            File next = it.next();
            String extention = getExtension(next.getName()).toLowerCase();
            String basename = FilenameUtils.getBaseName(next.getName()).toLowerCase();
            if (extention.equals("log")
                    || extention.equals("cue")
                    || extention.equals("db")
                    || extention.equals("m3u")
                    || extention.equals("m3u8")
                    || extention.equals("md5")
                    || extention.equals("sfv")
                    || extention.equals("url")
                    || extention.equals("flp")
                    || extention.equals("fpl")
                    || extention.equals("nfo")
                    || extention.equals("ds_store")
                    || extention.equals("auCDtect")
                    || extention.equals("accurip")) {
                next.delete();
                it.remove();
            }
            if (".ds_store".equals(basename)
                    || "folder.aucdtect".equals(basename)
                    || "foo_dr".equals(basename)) {
                next.delete();
                it.remove();
            }
        }
    }

    /*private boolean attachArtworkFileIfExists(MediumScope mediumScope) {
        File artworkFile = null;

        if (mediumScope.hasArtwork()) {
            artworkFile = mediumScope.getArtwork();
        }

        if (artworkFile != null) {
            attachArtwork(artworkFile);
            return true;
        }

        return false;
    }

    private boolean audioArtworkExists(MP3File file) {
        return !file.getTag().getArtworkList().isEmpty();
    }

    private void attachArtwork(File artworkFile) {
        try {
            artwork.setFromFile(artworkFile);
        } catch (IOException e) {
            System.out.println("IOException while attaching artwork: " + e.getMessage());
        }
    }*/

    private String trackToString(String string, int count) {
        if (count < 10) {
            string = "0" + Integer.toString(count);
        } else {
            string = Integer.toString(count);
        }
        return string;
    }


}
