package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.ReleaseScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ExportService {

    @Value("${app.destination}")
    private String destination;

    private final RearrangeService exportStrategy;

    public ExportService(RearrangeService exportStrategy) {
        this.exportStrategy = exportStrategy;
    }

    public void export(ReleaseScope releaseScope, boolean includeInArtistFolder) {
        exportStrategy.execute(releaseScope, new File(destination), includeInArtistFolder);
    }

}
