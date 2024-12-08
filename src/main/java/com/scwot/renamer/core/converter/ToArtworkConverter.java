package com.scwot.renamer.core.converter;

import com.scwot.renamer.core.scope.Artwork;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

@Component
public class ToArtworkConverter {

    public Artwork fromFile(File file) {
        if (file == null) {
            return null;
        }

        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            Artwork artwork = fromBytes(raw);
            return new Artwork(raw, file, artwork.format(), artwork.width(), artwork.height());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Artwork fromBytes(byte[] raw) {
        if (raw == null) {
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(raw);

        int width;
        int height;
        String format;

        try {
            BufferedImage image = ImageIO.read(bais);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
            } else {
                throw new RuntimeException("Invalid image data");
            }

            // Reset the stream for format detection
            bais.reset();
            ImageInputStream iis = ImageIO.createImageInputStream(bais);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                format = reader.getFormatName();
                reader.dispose();
            } else {
                throw new RuntimeException("Unknown image format");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return new Artwork(raw, null, format, width, height);

    }
}
