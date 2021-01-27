package com.scwot.renamer.controller;

import com.scwot.renamer.core.service.LaunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
public class StartController {

    @Value("${app.input}")
    private String input;

    private final LaunchService launchService;

    public StartController(LaunchService launchService) {
        this.launchService = launchService;
    }

    @GetMapping("/start")
    public void greeting() {
        boolean includeInArtistFolder = true;

        File dir = new File(input);

        if (dir.exists()) {
            log.info("Processing " + dir.getAbsolutePath());
            launchService.start(dir, includeInArtistFolder);
            log.info("##########    DONE    ##########");
        } else {
            log.info(dir.getAbsolutePath() + " doesn't exists");
        }
    }
}
