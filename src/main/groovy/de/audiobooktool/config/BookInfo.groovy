package de.audiobooktool.config

import de.audiobooktool.wrapper.ffmpeg.Ffmpeg
import de.audiobooktool.wrapper.mp4v2.Mp4Chaps
import de.audiobooktool.wrapper.mp4v2.Mp4Info
import de.audiobooktool.wrapper.mp4v2.Mp4Tag
import groovy.time.Duration
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.Artwork
/**
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 23:40
 */
public class BookInfo {

  String artist, albumArtist, title, comment, year;
  Artwork cover;
  int bookLength;

  private LinkedList<ChapterInfo> chapterList = new LinkedList<ChapterInfo>();

  /**
   * Create a new BookInfo object and initialize with all supported meta data from the given
   * file.
   * @param fileWithMetadata file to read metadata from.
   */
  BookInfo(File fileWithMetadata) {

    readInfoFromMetafile(fileWithMetadata)

    def mp4Chaps = new Mp4Chaps(fileWithMetadata.getAbsolutePath())
    def chapters = mp4Chaps.exportChapters()
    chapters.eachLine {
      addChapter(it)
    }
    sortChaptersAndFinalizeData();
  }

  /**
   * Read metadata from given audio file and set properties for artist, albumArtist, title,
   * comment, year, bookLength and cover.
   * Reading the metadata ist first done with jaudiotagger, if this fails, it is done with
   * Mp4Tools wrapper and FFmpeg.
   *
   * @param fileWithMetadata file which metadata is read from
   */
  void readInfoFromMetafile(File fileWithMetadata) {
    try {
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

      cover = tag.getFirstArtwork()
    }
    catch (CannotReadException e) {
      Ffmpeg ffmpeg = new Ffmpeg(fileWithMetadata.absolutePath)
      Duration duration = ffmpeg.getDuration()
      bookLength = duration.toMilliseconds()

      Mp4Info mp4Info = new Mp4Info(fileWithMetadata.absolutePath)
      List<Mp4Tag> tags = mp4Info.listTags()

      for (int i = 0; i < tags.size(); i++) {
        Mp4Tag tag = tags.get(i);

        if (tag.name == Mp4Tag.ARTIST) {
          artist = tag.value
        }

        if (tag.name == Mp4Tag.ALBUM_ARTIST) {
          albumArtist = tag.value
        }

        if (tag.name == Mp4Tag.NAME) {
          title = tags.value
        }

        if (tag.name == Mp4Tag.COMMENTS) {
          comment = tag.value
        }

        if (tag.name == Mp4Tag.RELEASE_DATE) {
          year = tag.value
        }
      }
    }
  }

  /**
   * Parses a line from chapter file and creates a new ChapterInfo from this line. This object it added
   * to chapterList.
   *
   * @param str one line from chapter file
   * @return ChapterInfo object which was added to list
   */
  public ChapterInfo addChapter(String str) {
    ChapterInfo chapter = new ChapterInfo(str);
    chapterList.add(chapter);
    return chapter;
  }

  /**
   *
   * @param index
   * @return
   */
  public ChapterInfo getChapter(int index) {
    return chapterList.get(index);
  }

  /**
   *
   * @return
   */
  public Iterator<ChapterInfo> getChapterIterator() {
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
  public void sortChaptersAndFinalizeData() {
    Collections.sort(chapterList);

    ChapterInfo oic = null;
    int cnt = 1;
    for (ChapterInfo ic : chapterList) {
      if (ic.titleNo == -1)
        ic.titleNo = cnt++;

      if (oic != null) {
        oic.tsEnd = ic.tsStart;
      }
      oic = ic;
    }

    ChapterInfo last = this.chapterList.getLast()
    last.tsEnd = timeMillisToString(this.bookLength)

    Map<Integer, Integer> maxTitleNo = getMaxTitleNo()
    int maxCDNo = getMaxCDNo()
    for (ChapterInfo ic : chapterList) {
      ic.cdNoTotal = maxCDNo
      ic.titleNoTotal = maxTitleNo.get(ic.cdNo)
    }

  }

  /**
   *
   * @return
   */
  def int getMaxCDNo() {
    int max = -1;
    for (ChapterInfo ic : chapterList) {
      max = Math.max(max, ic.cdNo)
    }
    return max
  }

  /**
   *
   * @return
   */
  def Map<Integer, Integer> getMaxTitleNo() {
    Map<Integer, Integer> result = new HashMap<Integer, Integer>()
    for (ChapterInfo ic : chapterList) {
      if (result.containsKey(ic.cdNo))
        result.put(ic.cdNo, Math.max(result.get(ic.cdNo), ic.titleNo))
      else
        result.put(ic.cdNo, ic.titleNo)
    }
    return result
  }

  def String timeMillisToString(int timeMillis) {
    int seconds = ((int) (this.bookLength / 1000)) % 60
    int minutes = ((int) (this.bookLength / (1000 * 60))) % 60
    int hours = ((int) (this.bookLength / (1000 * 60 * 60)))
    int millis = this.bookLength % 1000

    return "$hours".padLeft(2, '0') + ":" + "$minutes".padLeft(2, '0') + ":" + "$seconds".padLeft(2, '0') + "." + "$millis".padLeft(3, '0')
  }

  @Override
  String toString() {
    StringBuilder sb = new StringBuilder()
    sb.append("Title: $title (number of chapters: " + getNoOfChapters() + ")\n")
    sb.append("Author: $artist \n")
    sb.append("Comment: $comment \n")
    sb.append("Year: $year \n")
    for (int i = 0; i < getNoOfChapters(); i++) {
      int chapterNo = i + 1
      sb.append("  " + "$chapterNo".padLeft(3, ' ') + " Chapter: " + getChapter(i) + "\n")
    }
    return sb.toString()
  }
}
