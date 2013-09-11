package de.wegmann.config;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class InfoBook {

  private String bookTitle, bookAuthor, bookReader;

  private LinkedList<InfoChapter> chapterList = new LinkedList<InfoChapter>();

  public InfoBook(File fileWithMetadata) {
    // TODO:
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

  public void sortChaptersAndInsertTSEnd() {
    Collections.sort(chapterList);
    InfoChapter oic = null;
    for(InfoChapter ic: chapterList) {
      if (oic != null) {
        oic.tsEnd = ic.tsStart;
      }
      oic = ic;
    }
  }

}
