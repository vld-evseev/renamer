package com.scwot.renamer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ResourceProvider {

    public static Resource getRegularSimple() {
        return new ClassPathResource("test_collection/regular_albums/simple");
    }

    public static Resource getRegularMultiDiskWithCovers() {
        return new ClassPathResource("test_collection/regular_albums/multi_disk_with_covers");
    }

    public static Resource getRegularSimpleWithCovers() {
        return new ClassPathResource("test_collection/regular_albums/simple_with_covers");
    }

    public static Resource getRegularSimpleWithOthers() {
        return new ClassPathResource("test_collection/regular_albums/simple_with_others");
    }

    public static Resource getVaSimple() {
        return new ClassPathResource("test_collection/va_albums/simple");
    }

    public static Resource getCompilationSimpleWithCovers() {
        return new ClassPathResource("test_collection/compilations/simple_with_covers");
    }

    public static Resource getVaMultiDiskWithCovers() {
        return new ClassPathResource("test_collection/va_albums/multi_disk_with_covers");
    }

    public static Resource getNonRelease() {
        return new ClassPathResource("test_collection/non_release");
    }

}
