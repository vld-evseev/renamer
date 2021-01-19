package com.scwot.renamer.core.strategy;

import com.scwot.renamer.core.converter.DirectoryToMediumConverter;
import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.MediumScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.utils.FileHelper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultImportStrategy {

    private final DirectoryToMediumConverter directoryToMediumConverter;

    //private DirectoryScope root;

   /* private int cdCount = 0;
    private int cdNotProcessed = 0;*/

    public DefaultImportStrategy(DirectoryToMediumConverter directoryToMediumConverter) {
        this.directoryToMediumConverter = directoryToMediumConverter;
    }

    public List<MediumScope> execute(File currentDir) {
        if (!currentDir.exists()) {
            log.warn(currentDir + " not exists!");
            return Collections.emptyList();
        }

        //root = new DirectoryScope(currentDir);

        final List<MediumScope> mediumScopeList = walk(currentDir);

        /*log.debug("Processing dir: " + root.toString());
        for (DirectoryScope directoryScope : root.getChildren()) {
            log.debug("Dir properties: " + directoryScope.toString());
        }*/

        return mediumScopeList;
    }

    private List<MediumScope> walk(File parent) {
        final List<MediumScope> mediumScopeList = new ArrayList<>();
        //final DirectoryScope parentDirScope = buildDirectoryScope(parent);
        final boolean[] isFirstElement = {true};
        final DirectoryScope[] rootScope = new DirectoryScope[1];

        try {
            Path startPath = parent.toPath();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         BasicFileAttributes attrs) {
                    final DirectoryScope currentDirScope = new DirectoryScope(dir.toFile());
                    if (isFirstElement[0]){
                        rootScope[0] = currentDirScope;
                        isFirstElement[0] = false;
                    } else {
                        currentDirScope.setRoot(rootScope[0]);
                        rootScope[0].addChild(currentDirScope);
                    }

                    if (currentDirScope.hasAudio()) {
                        final MediumScope mediumScope = directoryToMediumConverter.convert(currentDirScope);
                        mediumScopeList.add(mediumScope);
                    }

                    /*final List<DirectoryScope> relevantDirScopes = mediumScopeList.stream()
                            .map(MediumScope::getDirectoryScope)
                            .filter(directoryScope -> directoryScope.isPartOf(currentDirScope))
                            .collect(Collectors.toList());

                    for (DirectoryScope relevantDirScope : relevantDirScopes) {
                        if (!relevantDirScopes.contains(currentDirScope)){
                            relevantDirScope.addChild(currentDirScope);
                        }
                    }*/

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while walking the directory: "
                    + parent.getAbsolutePath() + "\n" + e.getMessage(), e);
        }

        return mediumScopeList;
    }


    /*private int discNumber(DirectoryScope currentEntry) {
        int cdN = 0;
        if (cdCount > 0 && currentEntry.hasAudio()) {
            cdN = cdCount - --cdNotProcessed;
        }
        return cdN;
    }*/
}
