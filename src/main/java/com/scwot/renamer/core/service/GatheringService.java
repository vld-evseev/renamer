package com.scwot.renamer.core.service;

import com.scwot.renamer.core.converter.DirectoryToMediumConverter;
import com.scwot.renamer.core.converter.SystemDirToDirectoryWrapperConverter;
import com.scwot.renamer.core.exception.DirectoryWalkException;
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
import java.util.List;

@Slf4j
@Service
public class GatheringService {

    private final DirectoryToMediumConverter directoryToMediumConverter;
    private final SystemDirToDirectoryWrapperConverter systemDirToDirectoryWrapperConverter;

    public GatheringService(DirectoryToMediumConverter directoryToMediumConverter,
                            SystemDirToDirectoryWrapperConverter systemDirToDirectoryWrapperConverter) {
        this.directoryToMediumConverter = directoryToMediumConverter;
        this.systemDirToDirectoryWrapperConverter = systemDirToDirectoryWrapperConverter;
    }

    public List<MediumScope> execute(File parent) {
        if (!parent.exists()) {
            log.warn(parent + " doesn't exist");
            return List.of();
        }

        try {
            var directories = collectDirectories(parent);
            return processDirectories(directories);
        } catch (IOException e) {
            throw new DirectoryWalkException("Failed to walk directory: " + parent.getAbsolutePath(), e);
        }
    }

    private List<Path> collectDirectories(File parent) throws IOException {
        List<Path> directories = new ArrayList<>();
        Files.walkFileTree(parent.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                directories.add(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        return directories;
    }

    private List<MediumScope> processDirectories(List<Path> directories) {
        List<MediumScope> mediumScopeList = new ArrayList<>();
        var rootScope = directories.isEmpty() ? null :
                setupRootScope(
                        systemDirToDirectoryWrapperConverter.convert(directories.getFirst().toFile())
                );

        directories.forEach(dir -> {
            DirectoryScope currentDirScope = systemDirToDirectoryWrapperConverter.convert(dir.toFile());
            if (rootScope != null && currentDirScope != rootScope) {
                currentDirScope.setRoot(rootScope);
                rootScope.addChild(currentDirScope);
            }

            if (currentDirScope.hasAudio()) {
                mediumScopeList.add(directoryToMediumConverter.convert(currentDirScope));
            }
        });

        return mediumScopeList;
    }


    private DirectoryScope setupRootScope(DirectoryScope scope) {
        scope.setRoot(scope);
        return scope;
    }
}
