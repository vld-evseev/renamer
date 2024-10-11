package com.scwot.renamer;

import com.scwot.renamer.core.service.LaunchService;
import com.scwot.renamer.core.utils.enums.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StartupApplicationListener {

    @Value("${app.input}")
    private String input;

    private final LaunchService launchService;

    public StartupApplicationListener(LaunchService launchService) {
        this.launchService = launchService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        boolean includeInArtistFolder = true;

        File dir = new File(input);

        if (dir.exists()) {
            log.info("Processing " + dir.getAbsolutePath());
            launchService.start(dir, includeInArtistFolder);
        } else {
            log.info(dir.getAbsolutePath() + " doesn't exists");
        }
    }

}
