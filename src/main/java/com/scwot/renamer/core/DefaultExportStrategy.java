package com.scwot.renamer.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.jaudiotagger.tag.FieldKey.*;

public class DefaultExportStrategy implements ExportStrategy {

    private LocalRelease localRelease;
    private Artwork artwork;
    private File destination;

    private static final String VA_VALUE = "VA";
    private static final String UNKNOWN_VALUE = "[unknown]";
    private String outputCountryValue = "";
    private String outputArtistGenresValue = "";
    private String outputRecordGenresValue = "";
    private String outputLabelValue = "";
    private String outputCatNumValue = "";
    private String outputArtistValue = "";
    private String outputAlbumYearRecordedValue = "";
    private String outputAlbumYearReleasedValue = "";
    private String outputAlbumTypeValue = "";

    private boolean includeInArtistFolder;

    public DefaultExportStrategy(LocalRelease localRelease, File destination, boolean includeInArtistFolder) {
        this.localRelease = localRelease;
        this.destination = destination;
        artwork = new StandardArtwork();
        this.includeInArtistFolder = includeInArtistFolder;
    }

    public void execute() {
        fillOutputValues();

        for (Medium medium : localRelease.getMediums()) {
            renameAudio(medium);
        }

        if (destination.exists()) {
            moveData();
            removeFoldersIfEmpty(localRelease.getRoot().getCurrentDir().getValue().toPath());
        } else {
            System.out.println(destination.getAbsolutePath() + " not exists");
        }
    }


    private void renameAudio(Medium medium) {
        File newFile;
        ObservableList<Audio> renamedAudioList = FXCollections.observableArrayList();

        for (int i = 0; i < medium.getAudioList().size(); i++) {
            AudioFile oldFile = medium.getAudioList().get(i).getAudioFile();

            if (!oldFile.getTag().getFirst(TRACK).isEmpty()
                    && !oldFile.getTag().getFirst(TITLE).isEmpty()) {
                if (localRelease.isVA()) {
                    newFile = new File(oldFile.getFile().getParentFile()
                            .getAbsolutePath()
                            + "\\"
                            + buildTrackNumber(oldFile.getTag().getFirst(TRACK))
                            + " - "
                            + validateName(oldFile.getTag().getFirst(ARTIST))
                            + " - "
                            + trimTitle(validateName(oldFile.getTag().getFirst(TITLE)))
                            + EXTENSION_SEPARATOR
                            + getExtension(oldFile.getFile().getName()));
                } else {
                    newFile = new File(oldFile.getFile().getParentFile()
                            .getAbsolutePath()
                            + "\\"
                            + buildTrackNumber(oldFile.getTag().getFirst(TRACK))
                            + " - "
                            + trimTitle(validateName(oldFile.getTag().getFirst(TITLE)))
                            + EXTENSION_SEPARATOR
                            + getExtension(oldFile.getFile().getName()));
                }

                renamedAudioList.add(new Mp3AudioImpl(newFile));

                if (!oldFile.getFile().getName().equals(newFile.getName())) {
                    System.out.println("Renamed from \"" + oldFile.getFile().getName() + "\" to \"" + newFile.getName() + "\"");

                    try {
                        oldFile.getFile().renameTo(newFile);
                    } catch (Exception e) {
                        System.out.println("Exception while renaming file "
                                + oldFile.getFile().getName() + " to "
                                + newFile.getName() + ";\n"
                                + e.getMessage());
                    }
                }
            }
        }

        if (!renamedAudioList.isEmpty()) {
            medium.setAudioList(renamedAudioList);
            medium.getProperties().setListOfAudios(renamedAudioList);
        }
    }

    private String trimTitle(String title) {
        String[] trimmed = title.split(" ");

        if (title.length() > 30) {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (String string : trimmed) {
                stringBuilder.append(string);

                if (i < 4) {
                    stringBuilder.append(" ");
                } else {
                    return stringBuilder.toString();
                }

                i++;
            }
        }

        return title;
    }

    private String buildTrackNumber(String track) {
        if (track.length() == 1) {
            return "0" + track;
        }
        return track;
    }

    private void moveData() {
        File newAlbumDirectory;
        if (!localRelease.isVA()) {
            String dest = destination.getAbsolutePath();

            if (includeInArtistFolder) {
                dest += "\\" + buildArtistString();
                File newArtistDirectory = new File(dest);
                newArtistDirectory.mkdir();
            }

            newAlbumDirectory = new File(dest + "\\" + buildAlbumString());

        } else {
            newAlbumDirectory = new File(destination + "\\" + buildAlbumString());
        }

        newAlbumDirectory.mkdir();

        for (Medium medium : localRelease.getMediums()) {
            if (localRelease.getCdCount() > 0) {
                File cdDirectory = new File(newAlbumDirectory + "\\" + "CD" + medium.getDiscNubmer());
                cdDirectory.mkdir();
                organiseData(medium.getProperties(), cdDirectory);
            } else {
                organiseData(medium.getProperties(), newAlbumDirectory);
            }
        }

        for (FileLevelProperties flp : localRelease.getRoot().getChilds()) {
            organiseImages(flp, newAlbumDirectory);
            organiseOthers(flp, newAlbumDirectory);
            //organiseData(flp, newAlbumDirectory);
        }

        organiseImages(localRelease.getRoot(), newAlbumDirectory);
        organiseOthers(localRelease.getRoot(), newAlbumDirectory);
    }

    private void organiseData(FileLevelProperties props, File dest) {
        organiseAudio(props, dest);
        organiseImages(props, dest);
        organiseOthers(props, dest);
    }

    private void organiseAudio(FileLevelProperties props, File dest) {
        if (!props.getListOfAudios().isEmpty()) {
            for (Audio audio : props.getListOfAudios()) {
                move(audio.getFile(), dest);
            }
        }
    }

    private void organiseImages(FileLevelProperties props, File dest) {
        if (!props.getListOfImages().isEmpty()) {
            if (props.getListOfImages().size() == 1 && (props.hasAudio() || localRelease.getCdCount() > 0)) {
                File newFolderImage = new File(dest + "\\" + "folder"
                        + EXTENSION_SEPARATOR + getExtension(props.getListOfImages().get(0).getName()));
                props.getListOfImages().get(0).renameTo(newFolderImage);
            } else if ((props.getListOfImages().size() > 1 && !props.hasAudio())
                    || props.getListOfImages().size() == 1 && !props.hasAudio() && !props.hasInnerFolders()) {
                File coversFolder = new File(dest + "\\" + "Covers");
                if (!coversFolder.exists()) {
                    if (coversFolder.mkdir()) {
                        for (File image : props.getListOfImages()) {
                            move(image, coversFolder);
                        }
                    }
                } else {
                    /*for (File image : props.getListOfImages()) {
                        move(image, dest);
                    }*/
                }
            } else {
                if (props.getListOfImages().size() > 1 && props.hasAudio()) {
                    File coversFolder = new File(dest + "\\" + "Covers");
                    coversFolder.mkdir();
                    for (File image : props.getListOfImages()) {
                        move(image, coversFolder);
                    }
                } else {
                    for (File image : props.getListOfImages()) {
                        move(image, dest);
                    }
                }


            }
        }
    }

    private void organiseOthers(FileLevelProperties props, File dest) {
        if (!props.getListOfOthers().isEmpty()) {
            removeJunkFiles(props.getListOfOthers());
        }

        if (!props.getListOfOthers().isEmpty()) {
            if (!props.getListOfOthers().get(0).getParentFile().equals(dest)) {
                File[] cont = dest.listFiles();
                File destFolder = null;
                boolean exists = false;

                for (File d : cont) {
                    if (d.getName().equals(props.getListOfOthers().get(0).getParentFile().getName())) {
                        destFolder = d;
                        exists = true;
                        break;
                    }
                }

                if (exists && destFolder != null) {
                    for (File otherFile : props.getListOfOthers().get(0).getParentFile().listFiles()) {
                        move(otherFile, destFolder);
                    }
                } else {
                    for (File otherFile : props.getListOfOthers()) {
                        move(otherFile, dest);
                    }
                }
            } else {
                for (File otherFile : props.getListOfOthers()) {
                    move(otherFile, dest);
                }
            }
        }
    }

    private void move(File from, File to) {
        try {
            if (from.exists() && to.exists()) {
                if (from.isDirectory()) {
                    FileUtils.moveDirectoryToDirectory(from, to, false);
                } else {
                    FileUtils.moveFileToDirectory(from, to, false);
                }
            }
        } catch (IOException e) {
            System.out.println("move() exception\nFrom " + from + "\nTo " + to);
            //e.printStackTrace();
        }
    }

    private void fillOutputValues() {
        outputArtistValue = localRelease.getArtistTitle();
        outputAlbumYearRecordedValue = localRelease.getOrigYear();
        outputAlbumYearReleasedValue = localRelease.getYear();
        outputLabelValue = localRelease.getLabel();
        outputCatNumValue = localRelease.getCatNumber();
        String[] catNumArr = outputCatNumValue.split(" ");

        for (String s : catNumArr) {
            if (outputLabelValue.contains(s)) {
                outputCatNumValue = outputCatNumValue.replaceAll(s, "");
                outputCatNumValue = StringUtils.normalizeSpace(outputCatNumValue);
                break;
            }
        }
    }

    private void removeFoldersIfEmpty(Path path) {
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
    }

    private void removeJunkFiles(ObservableList<File> listOfOtherFiles) {
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

    private String buildArtistString() {
        if (localRelease.isVA()) {
            return VA_VALUE;
        } else {
            StringBuilder artistString = new StringBuilder();
            if (outputArtistValue.startsWith("The ")) {
                artistString.append(validateName(outputArtistValue).replaceFirst("The ", "")).append(", The");
            } else {
                artistString.append(validateName(outputArtistValue));
            }

            if (!outputCountryValue.isEmpty() || !outputArtistGenresValue.isEmpty()) {
                artistString.append(" [");
            }

            if (!outputCountryValue.isEmpty()) {
                artistString.append(outputCountryValue).append(" ");
            }

            if (!outputArtistGenresValue.isEmpty()) {
                artistString.append(validateName(outputArtistGenresValue));
            }

            if (!outputCountryValue.isEmpty() || !outputArtistGenresValue.isEmpty()) {
                artistString.append("]");
            }
            return artistString.toString();
        }
    }

    private String buildAlbumString() {
        StringBuilder albumString = new StringBuilder();

        for (Medium medium : localRelease.getMediums()) {
            fillAlbumString(albumString, medium);
            if (localRelease.getCdCount() > 0) {
                albumString.append(" (").append(localRelease.getCdCount()).append("CD)");
                break;
            }
        }

        return albumString.toString();
    }

    private void fillAlbumString(StringBuilder albumString, Medium medium) {
        String year;

        year = fillYearValue();
        if (year.isEmpty()) {
            year = localRelease.getYear();
        }

        if (localRelease.isVA()) {
            albumString.append(VA_VALUE).append(" - ");
        }

        if (year.contains("-")) {
            year = year.split("-")[0];
        }

        albumString.append(year)
                .append(" - ")
                .append(trimTitle(validateName(localRelease.getAlbumTitle())));

        if (outputLabelValue.isEmpty() &&
                outputCatNumValue.isEmpty()) {
            return;
        }

        albumString.append(" [");

        if (!outputAlbumYearReleasedValue.isEmpty() && !outputAlbumYearReleasedValue.equals("xxxx")) {
            if (outputAlbumYearReleasedValue.contains("-")) {
                outputAlbumYearReleasedValue = outputAlbumYearReleasedValue.split("-")[0];
            }

            albumString.append(outputAlbumYearReleasedValue).append(", ");
        }

        if (outputLabelValue.equals("[no label]")) {
            albumString.append("no label").append(", ");
        } else if (!outputLabelValue.isEmpty()) {
            String shortLabel = validateName(outputLabelValue)
                    .replaceAll(" Records", "")
                    .replaceAll(" Recordings", "");
            albumString.append(shortLabel).append(", ");
        }

        if (outputCatNumValue.isEmpty() || outputCatNumValue.equals("[none]")) {
            albumString.append("none");
        } else {
            albumString.append(validateName(outputCatNumValue));
        }

        albumString.append("]");
    }

    private String fillYearValue() {
        String year;

        if (!outputAlbumYearRecordedValue.isEmpty() || !outputAlbumYearRecordedValue.equals("xxxx")) {
            year = outputAlbumYearRecordedValue;
        } else {
            year = outputAlbumYearReleasedValue;
        }

        return year;
    }

    private boolean attachArtworkFileIfExists(Medium medium) {
        boolean exists = false;

        File artworkFile = null;

        if (medium.hasArtwork()) {
            artworkFile = medium.getArtwork();
        } else if (localRelease.hasArtwork()) {
            artworkFile = localRelease.getArtwork();
        }

        if (artworkFile != null) {
            attachArtwork(artworkFile);
            exists = true;
        }

        return exists;
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
    }

    private String trackToString(String string, int count) {
        if (count < 10) {
            string = "0" + Integer.toString(count);
        } else {
            string = Integer.toString(count);
        }
        return string;
    }

    private String validateName(String name) {
        return name.replaceAll("$", "")
                .replaceAll("�", "")
                .replaceAll("`", "'")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("/", "-")
                .replaceAll("\\\\", "")
                .replaceAll("\\\\", "")
                .replaceAll("\\*", "")
                .replaceAll(":", " -")
                .replaceAll("\"", "")
                .replaceAll("\\?", "");
    }


}
