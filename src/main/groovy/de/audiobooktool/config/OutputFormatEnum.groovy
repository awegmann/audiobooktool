package de.audiobooktool.config;

/**
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 22:50
 */
public enum OutputFormatEnum {

    M4B(".m4b"),
    MP3(".mp3"),
    M4A(".m4a")

    String extension
    OutputFormatEnum(String extension) {
        this.extension = extension
    }
}
