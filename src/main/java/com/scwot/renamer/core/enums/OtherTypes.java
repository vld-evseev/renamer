package com.scwot.renamer.core.enums;

public enum OtherTypes {
    TXT {
        public String toString() {
            return "text/plain";
        }
    },

    M3U {
        public String toString() {
            return "audio/x-mpegurl";
        }
    },

    NULL {
        public String toString() {
            return "null";
        }
    }
}
