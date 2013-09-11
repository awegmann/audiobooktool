package de.wegmann.config;

import de.wegmann.exceptions.NotImplementedException;

import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */
public class InfoChapter implements Comparable<InfoChapter> {

  protected String tsStart, tsEnd;
  private String title;

  public InfoChapter(String str) {

    tsStart = str.substring(0, str.indexOf(" "));
    title = str.substring(str.indexOf(" "));

  }

  /**
   * should check if tsStart before tsEnd ... a.s.o.
   * @return
   */
  private boolean checkDataValid() {
    return true;
  }

  /**
   * should sort by tsStart
   * @param infoChapter
   * @return
   */
  @Override
  public int compareTo(InfoChapter infoChapter) {
    return this.tsStart.compareTo(infoChapter.tsStart);
  }
}
