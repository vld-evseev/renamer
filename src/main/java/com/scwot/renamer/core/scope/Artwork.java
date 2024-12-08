package com.scwot.renamer.core.scope;

import java.io.File;

public record Artwork(byte[] raw, File dest, String format, int width, int height) {
}
