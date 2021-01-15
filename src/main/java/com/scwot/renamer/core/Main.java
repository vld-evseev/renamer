package com.scwot.renamer.core;

import java.io.File;
import com.scwot.renamer.core.tasks.Process;

public class Main {

    public static void main(String[] args) {

        if (args.length != 0) {
            String path = args[0].replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "");
            boolean includeInArtistFolder = false;

            File dir = new File(path);

            if (dir.exists()) {
                System.out.println("Processing " + dir.getAbsolutePath());
                Process process = new Process(dir, includeInArtistFolder);
                try {
                    process.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(dir.getAbsolutePath() + " doesn't exists");
            }
        } else {
            System.out.println("No arguments");
        }


    }

}
