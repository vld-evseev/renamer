package com.scwot.renamer.core.enums;

public enum ImageTypes {

    JPG {
        public String toString() {
            return "image/jpeg";
        }
    },

    PNG {
        public String toString() {
            return "image/png";
        }
    },

    GIF {
        public String toString() {
            return "image/gif";
        }
    }
}
