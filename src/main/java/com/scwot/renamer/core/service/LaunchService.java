package com.scwot.renamer.core.service;

import com.scwot.renamer.core.scope.ReleaseScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
public class LaunchService {

    private final DirectoryScanner directoryScanner;
    private final ImportService importService;
    private final ExportService exportService;
    private final TaskExecutor taskExecutor;

    public LaunchService(DirectoryScanner directoryScanner,
                         ImportService importService,
                         ExportService exportService,
                         TaskExecutor taskExecutor) {
        this.directoryScanner = directoryScanner;
        this.importService = importService;
        this.exportService = exportService;
        this.taskExecutor = taskExecutor;
    }

    public void start(File inputDir, boolean includeInArtistFolder) {
        final List<Path> candidates = directoryScanner.scan(inputDir);

        for (List<Path> batch : ListUtils.partition(candidates, 4)) {
            taskExecutor.execute(() -> {
                for (Path dir : batch) {
                    final ReleaseScope releaseScope = importService.runImport(dir.toFile());
                    exportService.export(releaseScope, includeInArtistFolder);
                }
            });
        }

        /*for (Path dir : candidates) {
            final ReleaseScope releaseScope = importService.runImport(dir.toFile());
            System.out.println();
            exportService.export(releaseScope, includeInArtistFolder);
        }*/
    }

}
