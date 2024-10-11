package com.scwot.renamer.core.io;

import com.scwot.renamer.core.converter.DirectoryToMediumConverter;
import com.scwot.renamer.core.converter.SystemDirToDirectoryWrapperConverter;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DefaultImportStrategy {

    private final DirectoryToMediumConverter directoryToMediumConverter;
    private final SystemDirToDirectoryWrapperConverter systemDirToDirectoryWrapperConverter;

    public DefaultImportStrategy(DirectoryToMediumConverter directoryToMediumConverter,
                                 SystemDirToDirectoryWrapperConverter systemDirToDirectoryWrapperConverter) {
        this.directoryToMediumConverter = directoryToMediumConverter;
        this.systemDirToDirectoryWrapperConverter = systemDirToDirectoryWrapperConverter;
    }

    public List<MediumScope> execute(File currentDir) {
        if (!currentDir.exists()) {
            log.warn(currentDir + " doesn't exist");
            return Collections.emptyList();
        }

        final List<MediumScope> mediumScopeList = walk(currentDir);
        return mediumScopeList;
    }

    private List<MediumScope> walk(File parent) {
        final List<MediumScope> mediumScopeList = new ArrayList<>();
        final boolean[] isFirstEntry = {true};
        final DirectoryScope[] rootScope = new DirectoryScope[1];

        try {
            Path startPath = parent.toPath();
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         BasicFileAttributes attrs) {
                    var currentDirScope = systemDirToDirectoryWrapperConverter.convert(dir.toFile());
                    if (isFirstEntry[0]) {
                        rootScope[0] = currentDirScope;
                        currentDirScope.setRoot(currentDirScope);
                        isFirstEntry[0] = false;
                    } else {
                        currentDirScope.setRoot(rootScope[0]);
                        rootScope[0].addChild(currentDirScope);
                    }

                    if (currentDirScope.hasAudio()) {
                        final MediumScope mediumScope = directoryToMediumConverter.convert(currentDirScope);
                        mediumScopeList.add(mediumScope);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while walking the directory: "
                    + parent.getAbsolutePath() + "\n" + e.getMessage(), e);
        }

        return mediumScopeList;
    }
}
