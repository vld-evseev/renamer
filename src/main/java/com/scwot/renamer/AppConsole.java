package com.scwot.renamer;

import com.scwot.renamer.core.service.LaunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@Slf4j
@SpringBootApplication
public class AppConsole implements CommandLineRunner {

    private final LaunchService launchService;

    public AppConsole(LaunchService launchService) {
        this.launchService = launchService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AppConsole.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length != 0) {
            String path = sanitizePath(args);
            boolean includeInArtistFolder = Boolean.parseBoolean(args[1]);

            File dir = new File(path);

            if (dir.exists()) {
                log.info("Processing " + dir.getAbsolutePath());
                launchService.start(dir, includeInArtistFolder);
            } else {
                throw new IllegalArgumentException(dir.getAbsolutePath() + " doesn't exists");
            }
        } else {
            log.info("No arguments");
        }

    }

    private static String sanitizePath(String[] args) {
        return args[0].replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "");
    }
}
