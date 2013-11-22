package de.wegmann.starter

import de.audiobooktool.config.BookInfo
import de.audiobooktool.config.ChapterInfo
import de.audiobooktool.wrapper.ffmpeg.Ffmpeg

/**
 * TODOs:
 *  2.) insert tests for
 *        - convert to mp3s
 *        - correct data model from m4a file
 *        - correct data model from created mp3 files
 *  3.) change parameters for call ...
 *
 * User: WagnerOl
 * Date: 16.09.13
 * Time: 09:17
 */
class StarterWagnerOl {

//  def testPlainFile = "src/test/resources/Audiobook_Test_plain.m4a"
//  def testPlainFile = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/CC5/audio.flac"
  def testPlainFile = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/EndersGame.m4b"

//  def testWithMetadata = "src/test/resources/Audiobook_Test_with_Metadata.m4a"
//  def testWithMetadata = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/CC5/original.aax"
  def testWithMetadata = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/EndersGame.m4b"

  def testCover = "src/test/resources/Cover.jpg"

//  def testOutputDirectory = "tmp/"
//  def testOutputDirectory = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/CC5/out/"
  def testOutputDirectory = "/Users/WagnerOl/noTimeMachine/Test-Dateien-AudiobookConvert/EndersGame/"

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
//    new StarterWagnerOl().createDataObjFromTestData();
    new StarterWagnerOl().createMP3sFromFLACandAAX()
//    new StarterWagnerOl().createMP3sFromM4B()
  }

  /**
   *
   */
  def createDataObjFromTestData() {

    BookInfo ib = new BookInfo(new File(testWithMetadata))
    println "$ib"

  }

  /**
   *
   */
  def createMP3sFromFLACandAAX() {

    BookInfo bookInfo = new BookInfo(new File(testWithMetadata))

    Ffmpeg conv = new Ffmpeg(this.testPlainFile)

    // TODO: test how long it takes if converting each chapter vs. converting all in one ...

    Iterator<ChapterInfo> it = bookInfo.getChapterIterator();
    int cnt = 0;
    while (it.hasNext() && cnt <= 1000) {
      ChapterInfo curChapterInfo = it.next()
      println "\nCreate chapter: " + ++cnt + " => " + curChapterInfo.tsStart + " - " + curChapterInfo.tsEnd
      if (cnt < 145)
        continue;
      conv.extractChapterToMP3(new File(this.testOutputDirectory + "/chapter_" + "$cnt".padLeft(3, '0') + ".mp3"), curChapterInfo)
      conv.extractChapterToMP3(new File(this.testOutputDirectory + "/chapter_" + "$cnt".padLeft(3, '0') + ".mp3"), curChapterInfo)
    }

//    conv.extractChaptersToMP3(new File(this.testOutputDirectory), bookInfo)

  }

  def createMP3sFromM4B() {
    DataObj.createInstance(this.testWithMetadata, this.testWithMetadata, this.testOutputDirectory, false, OutputFormatEnum.MP3)

    Ffmpeg conv = new Ffmpeg()
    conv.convertToMP3()
  }

}
