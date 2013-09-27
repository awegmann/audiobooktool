package de.wegmann.config;

import de.wegmann.wrapper.mp4v2.Mp4Chaps;
import de.wegmann.wrapper.util.ToolsLocation;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/**
 * This object contains all data to control the workflow
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 20:38
 */
public class DataObj {

  // instance variable
  private static DataObj instance;

  // information from config file
  private final XMLConfiguration config;
  private final String execMp4Art;
  private final String execMp4Info;
  private final String execMp4Tags;
  private final String execMp4Chaps;
  private final String execFfmpeg;

  // arguments from command line
  private String fileAudio, fileMetaData, fileOutputBase;
  private boolean dryRun;
  private OutputFormatEnum outputFormat;

  // some other information
  private final Date startOfConverting = new Date();

  private InfoBook bookInfo;

  /**
   * constructor
   *
   * @param fileAudio
   * @param fileMetaData
   * @param fileOutput
   */
  private DataObj(String fileAudio, String fileMetaData, String fileOutput) {

    this.fileAudio = fileAudio;
    this.fileMetaData = fileMetaData;
    this.fileOutputBase = fileOutput;

    final ToolsLocation tl = ToolsLocation.getInstance();
    this.config = tl.getXMLConfig();
    this.execFfmpeg = tl.getFfmpegExecutable();
    this.execMp4Art = tl.getMp4ArtExecutable();
    this.execMp4Chaps = tl.getMp4ChapsExecutable();
    this.execMp4Info = tl.getMp4InfoExecutable();
    this.execMp4Tags = tl.getMp4TagsExecutable();

  }

  /**
   *
   * @param fileAudio
   * @param fileMetaData
   * @param fileOutput
   * @param dryRun
   * @param outputFormat
   * @return
   */
  public static DataObj createInstance(String fileAudio, String fileMetaData, String fileOutput,
                               boolean dryRun, OutputFormatEnum outputFormat) {
    instance = new DataObj(fileAudio, fileMetaData, fileOutput);
    instance.dryRun = dryRun;
    instance.outputFormat = outputFormat;

    // data validation, correction and analyze
    instance.checkFileOutput();
    instance.analyzeMetaDataFile();

    return instance;
  }

  /**
   *
   * @return
   */
  public static DataObj getInstance() {
    return instance;
  }

  /**
   *
   */
  private void checkFileOutput() {

    if (this.fileOutputBase == null) {
      fileOutputBase = removeExtention(getFileMetaData().getAbsolutePath());
    } else {
      if (new File(fileOutputBase).isDirectory()) {
        fileOutputBase = new File(fileOutputBase,
                removeExtention(getFileMetaData().getName())).getAbsolutePath();
      } else { // so it should be a file reference
        fileOutputBase = removeExtention(fileOutputBase);
      }
    }

  }

  /**
   *
   */
  private void analyzeMetaDataFile() {
    // TODO: get other meta info
    Mp4Chaps mp4Chaps = new Mp4Chaps(getFileMetaData().getAbsolutePath());

    BufferedReader br = new BufferedReader(new StringReader(mp4Chaps.exportChapters()));

    bookInfo = new InfoBook(null);
    try {
      String cur = br.readLine();
      while (cur != null) {
        bookInfo.addChapter(cur);
        cur = br.readLine();
      }
    } catch (IOException ioe) {
      throw new RuntimeException("This can never happen!", ioe);
    }
    bookInfo.sortChaptersAndInsertTSEnd();
  }

  /***********************************************************************************************
   * Getter and setter
   ***********************************************************************************************/

  public String getExecMp4Art() {
    return execMp4Art;
  }

  public String getExecMp4Info() {
    return execMp4Info;
  }

  public String getExecMp4Tags() {
    return execMp4Tags;
  }

  public String getExecMp4Chaps() {
    return execMp4Chaps;
  }

  public String getExecFfmpeg() {
    return execFfmpeg;
  }

  public File getFileAudio() {
    return new File(fileAudio);
  }

  public File getFileMetaData() {
    return new File(fileMetaData);
  }

  /**
   *
   * @param no
   * @param noOfNumericChars
   * @param suffix
   * @return
   */
  public File getFileOutput(int no, int noOfNumericChars, String suffix) {
    File res = new File(fileOutputBase + "_" + fillWithZeros(no, noOfNumericChars) + suffix);
    if (res.exists()) {
      // just to be unique, add a timestamp to basename
      res = new File( fileOutputBase + "_" + this.startOfConverting.toString() + "_" + fillWithZeros(no, noOfNumericChars) + suffix);
    }
    return res;
  }

  /**
   *
   * @param suffix
   * @return
   */
  public File getFileOutput(String suffix) {
    File res = new File(fileOutputBase + suffix);
    if (res.exists()) {
      // just to be unique, add a timestamp to basename
      res = new File( fileOutputBase + "_" + this.startOfConverting.toString() + "_" + suffix);
    }
    return res;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public OutputFormatEnum getOutputFormat() {
    return outputFormat;
  }

  public Date getStartOfConverting() {
    return startOfConverting;
  }

  /***********************************************************************************************
   * Helper methods
   ***********************************************************************************************/

  /**
   *
   * @param str
   * @return
   */
  private String removeExtention(String str) {
    int lastIndexOfDot = str.lastIndexOf('.');
    if (lastIndexOfDot != -1) {
      return str.substring(0, lastIndexOfDot);
    } else {
      return str;
    }
  }

  /**
   *
   * @param no
   * @param noOfNumericChars
   * @return
   */
  private String fillWithZeros(int no, int noOfNumericChars) {
    StringBuffer sb = new StringBuffer(no);

    while(sb.length() < noOfNumericChars)
      sb.insert(0, "0");

    return sb.toString();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("*********************************************************************************************\n");
    sb.append("***** Executables                                                                       *****\n");
    sb.append("*********************************************************************************************\n");
    sb.append("ffmpeg: " + this.execFfmpeg + "\n");
    sb.append("mp4tags: " + this.execMp4Tags + "\n");
    sb.append("mp4chaps: " + this.execMp4Chaps + "\n");
    sb.append("mp4info: " + this.execMp4Info +"\n");
    sb.append("mp4art: " + this.execMp4Art + "\n");

    sb.append("*********************************************************************************************\n");
    sb.append("***** Command line arguments and configuration                                          *****\n");
    sb.append("*********************************************************************************************\n");
    sb.append("fileAudio: " + this.fileAudio + "\n");
    sb.append("fileMetaData: " + this.fileMetaData + "\n");
    sb.append("fileOutputBase: " + this.fileOutputBase + "\n");
    sb.append("dryRun: " + this.dryRun + "\n");
    sb.append("OutputFormatEnum: " + this.outputFormat + "\n");

    sb.append("*********************************************************************************************\n");
    sb.append("***** Other values                                                                      *****\n");
    sb.append("*********************************************************************************************\n");
    sb.append("startOfConverting: " + this.startOfConverting.toString() + "\n");

    return sb.toString();
  }
}