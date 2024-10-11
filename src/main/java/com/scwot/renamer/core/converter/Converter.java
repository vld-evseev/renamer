package com.scwot.renamer.core.converter;

@FunctionalInterface
public interface Converter<I, O> {
    O convert(I input);
}
