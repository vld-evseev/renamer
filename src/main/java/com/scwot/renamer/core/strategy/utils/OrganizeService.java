package com.scwot.renamer.core.strategy.utils;

import com.scwot.renamer.core.scope.Mp3FileScope;
import org.jaudiotagger.audio.AudioFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.scwot.renamer.core.strategy.utils.ExportRenameUtils.normalizeName;
import static com.scwot.renamer.core.strategy.utils.ExportRenameUtils.trimTitle;
import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.jaudiotagger.tag.FieldKey.*;

@Service
public class OrganizeService {

    public void renameAudio(List<Mp3FileScope> audioList, boolean isVA) {
        File newFile;
        List<Mp3FileScope> renamedAudioList = new ArrayList<>();

        for (int i = 0; i < audioList.size(); i++) {
            AudioFile oldFile = audioList.get(i).getAudioFile();

            if (!oldFile.getTag().getFirst(TRACK).isEmpty()
                    && !oldFile.getTag().getFirst(TITLE).isEmpty()) {
                if (isVA) {
                    newFile = new File(oldFile.getFile().getParentFile()
                            .getAbsolutePath()
                            + File.separator
                            + buildTrackNumber(oldFile.getTag().getFirst(TRACK))
                            + " - "
                            + normalizeName(oldFile.getTag().getFirst(ARTIST))
                            + " - "
                            + trimTitle(normalizeName(oldFile.getTag().getFirst(TITLE)))
                            + EXTENSION_SEPARATOR
                            + getExtension(oldFile.getFile().getName()));
                } else {
                    newFile = new File(oldFile.getFile().getParentFile()
                            .getAbsolutePath()
                            + File.separator
                            + buildTrackNumber(oldFile.getTag().getFirst(TRACK))
                            + " - "
                            + trimTitle(normalizeName(oldFile.getTag().getFirst(TITLE)))
                            + EXTENSION_SEPARATOR
                            + getExtension(oldFile.getFile().getName()));
                }

                final Mp3FileScope audio = new Mp3FileScope();
                audio.readAudio(newFile);
                renamedAudioList.add(audio);

                if (!oldFile.getFile().getName().equals(newFile.getName())) {
                    System.out.println("Renamed from \"" + oldFile.getFile().getName() + "\" to \"" + newFile.getName() + "\"");

                    try {
                        oldFile.getFile().renameTo(newFile);
                    } catch (Exception e) {
                        System.out.println("Exception while renaming file "
                                + oldFile.getFile().getName() + " to "
                                + newFile.getName() + ";\n"
                                + e.getMessage());
                    }
                }
            }
        }

        /*if (!renamedAudioList.isEmpty()) {
            mediumScope.setAudioList(renamedAudioList);
            mediumScope.getDirectoryScope().setListOfAudios(renamedAudioList);
        }*/
    }

    private String buildTrackNumber(String track) {
        if (track.length() == 1) {
            return "0" + track;
        }
        return track;
    }



}
