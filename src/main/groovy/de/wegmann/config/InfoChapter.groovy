package de.wegmann.config;

/**
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 23:38
 */
public class InfoChapter implements Comparable<InfoChapter> {

  int cdNo = 1, titleNo = -1;
  int cdNoTotal = -1, titleNoTotal = -1;

  String tsStart, tsEnd;
  String title;

  public InfoChapter(String str) {

    tsStart = str.substring(0, str.indexOf(" "));
    title = str.substring(str.indexOf(" ") + 1);

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


  /********************************************************************************
   * Getter and setter
   ********************************************************************************/


  /********************************************************************************
   * Overridden methods
   ********************************************************************************/

  @Override
  String toString() {
    return "$tsStart - $tsEnd : CD: $cdNo; TitleNo: $titleNo; Titel: $title"
  }
}
