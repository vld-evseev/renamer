package com.scwot.renamer;

import com.scwot.renamer.core.service.LaunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class StartupApplicationListener {

    @Value("${app.input}")
    private String input;

    private final LaunchService launchService;

    public StartupApplicationListener(LaunchService launchService) {
        this.launchService = launchService;
    }

    //@EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        File dir = new File(input);

        if (dir.exists()) {
            log.info("Processing " + dir.getAbsolutePath());
            launchService.start(dir, true);
        } else {
            log.info(dir.getAbsolutePath() + " doesn't exists");
        }
    }
}
