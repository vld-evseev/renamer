package com.scwot.renamer.core.service;

import com.scwot.renamer.core.strategy.DefaultImportStrategy;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.converter.MediumsToReleaseConverter;
import com.scwot.renamer.core.scope.ReleaseScope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ImportService {

    private final DefaultImportStrategy importStrategy;
    private final MediumsToReleaseConverter toReleaseConverter;

    public ImportService(DefaultImportStrategy importStrategy, MediumsToReleaseConverter toReleaseConverter) {
        this.importStrategy = importStrategy;
        this.toReleaseConverter = toReleaseConverter;
    }

    public ReleaseScope runImport(File inputDir) {
        final List<MediumScope> mediumScopeList = importStrategy.execute(inputDir);
        return toReleaseConverter.convert(mediumScopeList);
    }


}
