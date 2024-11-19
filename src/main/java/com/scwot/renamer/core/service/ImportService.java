package com.scwot.renamer.core.service;

import com.scwot.renamer.core.converter.MediumsToReleaseConverter;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.ReleaseScope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ImportService {

    private final GatheringService gatheringService;
    private final MediumsToReleaseConverter toReleaseConverter;

    public ImportService(GatheringService gatheringService, MediumsToReleaseConverter toReleaseConverter) {
        this.gatheringService = gatheringService;
        this.toReleaseConverter = toReleaseConverter;
    }

    public ReleaseScope runImport(File inputDir) {
        final List<MediumScope> mediumScopeList = gatheringService.execute(inputDir);
        return toReleaseConverter.convert(mediumScopeList);
    }
}
