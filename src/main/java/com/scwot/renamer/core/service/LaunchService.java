package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.ReleaseScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class LaunchService {

    private final DirectoryScanner directoryScanner;
    private final ImportService importService;
    private final ExportService exportService;

    public LaunchService(DirectoryScanner directoryScanner,
                         ImportService importService, ExportService exportService) {
        this.directoryScanner = directoryScanner;
        this.importService = importService;
        this.exportService = exportService;
    }

    public void start(File inputDir, boolean includeInArtistFolder) {
        final List<Path> candidates = directoryScanner.scan(inputDir);
        //final List<File> candidates = Arrays.asList(inputDir.listFiles(File::isDirectory));
        for (Path dir : candidates) {
            final ReleaseScope releaseScope = importService.runImport(dir.toFile());
            System.out.println();
            exportService.export(releaseScope, includeInArtistFolder);
        }
        //candidates.forEach(file -> log.info(file.getAbsolutePath()));
    }

}
