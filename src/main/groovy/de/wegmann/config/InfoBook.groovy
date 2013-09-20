package de.wegmann.config

import de.wegmann.wrapper.mp4v2.Mp4Chaps
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.TagField

/**
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 23:40
 */
public class InfoBook {

  private String artist, albumArtist, title, comment, year;
  private String[] cover;
  private int bookLength;

  private LinkedList<InfoChapter> chapterList = new LinkedList<InfoChapter>();

  /**
   *
   * @param fileWithMetadata
   */
  InfoBook(File fileWithMetadata) {

    readInfoFromMetafile(fileWithMetadata)

    def mp4Chaps = new Mp4Chaps(fileWithMetadata.getAbsolutePath())
    def chapters = mp4Chaps.exportChapters()
    chapters.eachLine {
      addChapter(it)
    }
    sortChaptersAndInsertTSEnd();
  }

  def dumpAllMetaData(File fileWithMetadata) {
    AudioFile f = AudioFileIO.read(fileWithMetadata);
    Tag tag = f.getTag();
    AudioHeader header = f.getAudioHeader();
    // TODO: needs book length with milliseconds detail!
    bookLength = header.getTrackLength() * 1000;
    Iterator<TagField> it = tag.fields
    while (it.hasNext()) {
      TagField key = it.next()
      println "key: " + key.getId() + "; First Value: " + tag.getFirst(key.getId())
    }
  }

  def readInfoFromMetafile(File fileWithMetadata) {
    AudioFile f = AudioFileIO.read(fileWithMetadata);

    AudioHeader header = f.getAudioHeader();
    // TODO: needs book length with milliseconds detail!
    bookLength = header.getTrackLength() * 1000;

    Tag tag = f.getTag();
    artist = tag.getFirst(FieldKey.ARTIST)
    albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST)
    title = tag.getFirst(FieldKey.TITLE)
    comment = tag.getFirst(FieldKey.COMMENT)
    year = tag.getFirst(FieldKey.YEAR)

    this.cover = tag.getFirst(FieldKey.COVER_ART).getBytes()

  }

  /**
   *
   * @param str
   * @return
   */
  public InfoChapter addChapter(String str) {
    InfoChapter chapter = new InfoChapter(str);
    chapterList.add(chapter);
    return chapter;
  }

  /**
   *
   * @param number
   * @return
   */
  public InfoChapter getChapter(int number) {
    return chapterList.get(number);
  }

  /**
   *
   * @return
   */
  public Iterator<InfoChapter> getChapterIterator() {
    return chapterList.iterator();
  }

  /**
   *
   * @return
   */
  public int getNoOfChapters() {
    return chapterList.size();
  }

  /**
   *
   */
  public void sortChaptersAndInsertTSEnd() {
    Collections.sort(chapterList);
    InfoChapter oic = null;
    int cnt = 1;
    for(InfoChapter ic: chapterList) {
      if (ic.titleNo == -1)
        ic.titleNo = cnt++;

      if (oic != null) {
        oic.tsEnd = ic.tsStart;
      }
      oic = ic;
    }

    InfoChapter last = this.chapterList.getLast()
    last.tsEnd = timeMillisToString(this.bookLength)

  }

  def String timeMillisToString(int timeMillis) {
    int seconds = ((int)(this.bookLength / 1000)) % 60
    int minutes = ((int)(this.bookLength / (1000*60))) % 60
    int hours   = ((int)(this.bookLength / (1000*60*60)))
    int millis = this.bookLength % 1000

    return "$hours".padLeft(2, '0') + ":" + "$minutes".padLeft(2, '0') + ":" + "$seconds".padLeft(2, '0') + "." + "$millis".padLeft(3, '0')
  }

  @Override
  String toString() {
    StringBuilder sb = new StringBuilder()
    sb.append("Title: $title (number of chapters: " + getNoOfChapters() + ")\n")
    sb.append("Author: $artist \n")
    sb.append("Comment: $comment \n")
    for (int i = 0; i < getNoOfChapters(); i++) {
      sb.append("  $i Chapter: " + getChapter(i) + "\n")
    }
    return sb.toString()
  }
}
