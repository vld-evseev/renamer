package com.scwot.renamer.core.utils;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ImageHelper {


    @SneakyThrows
    public static void saveImage(byte[] imageData, File dest) {
        // Convert byte[] to BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(bais);

        // Identify the image format by reading the format from the byte array
        String format = null;
        if (bufferedImage != null) {
            // Determine the format by reading the byte[] through ImageIO
            for (String formatName : ImageIO.getWriterFormatNames()) {
                if (ImageIO.getImageWritersByFormatName(formatName).hasNext()) {
                    format = formatName;
                    break;
                }
            }
        }

        // Save the image to the disk
        if (format != null) {
            ImageIO.write(bufferedImage, format, new File(dest.getPath() + "/folder." + format.toLowerCase()));
        } else {
            throw new IOException("Unknown image format");
        }
    }
}
