package com.scwot.renamer.core.converter;

import com.scwot.renamer.core.scope.DirectoryScope;
import com.scwot.renamer.core.scope.Mp3FileScope;
import com.scwot.renamer.core.utils.FileHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SystemDirToDirectoryWrapperConverter implements Converter<File, DirectoryScope> {

    private final SystemAudioFileToMp3WrapperConverter audioFileToMp3WrapperConverter;

    private static final String HTOA_FILE = "(HTOA)";

    public SystemDirToDirectoryWrapperConverter(SystemAudioFileToMp3WrapperConverter audioFileToMp3WrapperConverter) {
        this.audioFileToMp3WrapperConverter = audioFileToMp3WrapperConverter;
    }

    @Override
    public DirectoryScope convert(File dir) {
        final List<Mp3FileScope> listOfAudios = new ArrayList<>();
        final List<File> listOfImages = new ArrayList<>();
        final List<File> listOfOthers = new ArrayList<>();

        Arrays.stream(dir.listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> !file.getName().startsWith("."))
                .filter(file -> !file.getName().contains(HTOA_FILE))
                .forEach(file -> {
                    if (FileHelper.isAudioFile(file)) {
                        final Mp3FileScope audio = audioFileToMp3WrapperConverter.convert(file);
                        listOfAudios.add(audio);
                    } else if (FileHelper.isImageFile(file)) {
                        listOfImages.add(file);
                    } else {
                        listOfOthers.add(file);
                    }
                });

        return new DirectoryScope(
                listOfAudios,
                listOfImages,
                listOfOthers,
                dir
        );
    }
}
