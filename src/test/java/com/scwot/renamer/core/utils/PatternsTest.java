package com.scwot.renamer.core.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class PatternsTest {

    @Test
    public void testCdPattern() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("cd1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testCdPatternWithSpace() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("cd 1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testCdPatternWithHyphen() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("cd-1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiscPattern() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disc1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiscPatternWithSpace() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disc 1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiscPatternWithHyphen() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disc-1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiskPattern() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disk1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiskPatternWithSpace() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disk 1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testDiskPatternWithHyphen() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("disk-1 folder");
        assertTrue(matcher.matches());
    }

    @Test
    public void testNegativeCase() {
        Matcher matcher = Patterns.getMultiDiskPattern().matcher("folder1");
        assertFalse(matcher.matches());
    }
}