package de.audiobooktool.wrapper.mp4v2

/**
 * Holds one tag info with a given name as String an value.
 *
 * Date: 20.05.13
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
class Mp4Tag {

  public static final String NAME = "Name"
  public static final String ARTIST = "Artist"
  public static final String RELEASE_DATE = "Release Date"
  public static final String ALBUM = "Album"
  public static final String GENRE = "Genre"
  public static final String COMMENTS = "Comments"
  public static final String COVER_ART_PIECES = "Cover Art pieces"
  public static final String ALBUM_ARTIST = "Album Artist"
  public static final String COPYRIGHT = "Copyright"

  String name
  def value
}
