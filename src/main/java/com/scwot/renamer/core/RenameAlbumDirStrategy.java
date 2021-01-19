package com.scwot.renamer.core;

import com.scwot.renamer.core.scope.MediumScope;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RenameAlbumDirStrategy {

    /*private LocalRelease localRelease;
    private String albumTitle;
    private String yearRecorded;
    private String yearReleased;
    private String label = "";
    private String catNum = "";

    public RenameAlbumDirStrategy(LocalRelease localRelease) {
        this.localRelease = localRelease;
    }

    @Override
    public void execute() {
        fillOutputValues();

        System.out.println(localRelease.getArtistTitle());
        System.out.println(localRelease.getAlbumTitle());
        System.out.println(localRelease.getMBReleaseID());

        File currentPath = localRelease.getRoot().getCurrentDir().getValue();
        File newPath = new File(currentPath.getParentFile().getAbsolutePath() + "\\" + buildAlbumString());

        boolean success = rename(currentPath, newPath);

        if (success) {
            System.out.println("Dir renamed from: ");
            System.out.println(currentPath.getAbsolutePath() + " to ");
            System.out.println(newPath.getAbsolutePath());
        } else {
            System.out.println("Renaming failure: ");
            System.out.println(newPath.getAbsolutePath());
        }
    }

    private boolean rename(File from, File to) {
        return from.renameTo(to);
    }

    private String buildAlbumString() {
        StringBuilder albumString = new StringBuilder();

        for (MediumScope mediumScope : localRelease.getMediums()) {
            fillAlbumString(albumString, mediumScope);
            if (localRelease.getCdCount() > 0) {
                albumString.append(" (").append(localRelease.getCdCount()).append("CD)");
                break;
            }
        }

        return albumString.toString();
    }

    private void fillAlbumString(StringBuilder albumString, MediumScope mediumScope) {
        *//*if (localRelease.isVA()) {
            albumString.append("VA")
                    .append(" - ")
                    .append(validateName(medium.getAlbum()))
                    .append(" (")
                    .append(yearReleased)
                    .append(")");
        } else {*//*
        albumString.append(yearRecorded)
                .append(" - ")
                .append(validateName(albumTitle));


        if (label.isEmpty() &&
                catNum.isEmpty()) {
            return;
        }

        albumString.append(" [");

        if (!yearReleased.isEmpty() && !yearReleased.equals("xxxx")) {
            albumString.append(yearReleased).append(", ");
        }

        if (label.equals("[no label]")) {
            albumString.append("no label").append(", ");
        } else if (!label.isEmpty()) {
            albumString.append(label).append(", ");
        }

        if (catNum.isEmpty() || catNum.equals("[none]")) {
            albumString.append("none");
        } else {
            albumString.append(catNum);
        }

        albumString.append("]");
        //}
    }

    private void fillOutputValues() {
        *//*MBParser mbParser = new MBParser(localRelease);
        try {
            if (mbParser.parseFromReleaseID()) {
                yearReleased = mbParser.getReleaseYear();
                label = validateName(mbParser.getLabel()).replaceAll(" Records", "").replaceAll(" Recordings", "");
                catNum = validateName(mbParser.getCatNum());
            } else {
                yearReleased = localRelease.getYear();
                label = localRelease.getMediums().get(0).getLabel();
                catNum = localRelease.getMediums().get(0).getCatNum();
            }
        } catch (MBWS2Exception e) {
            e.printStackTrace();
        }*//*

        albumTitle = localRelease.getAlbumTitle();

        yearRecorded = localRelease.getOrigYear();

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

    private String validateName(String name) {
        return name.replaceAll("$", "")
//                .replaceAll("?", "")
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
    }*/
}
