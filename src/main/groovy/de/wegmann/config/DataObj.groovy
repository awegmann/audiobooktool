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
  final XMLConfiguration config;
  final String execMp4Art;
  final String execMp4Info;
  final String execMp4Tags;
  final String execMp4Chaps;
  final String execFfmpeg;

  // arguments from command line
  String fileAudio, fileMetaData, fileOutputBase;
  boolean dryRun;
  OutputFormatEnum outputFormat;

  // some other information
  final Date startOfConverting = new Date();

  InfoBook bookInfo;

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
      fileOutputBase = removeExtention(new File(fileMetaData).getAbsolutePath());
    } else {
      if (new File(fileOutputBase).isDirectory()) {
        fileOutputBase = new File(fileOutputBase, removeExtention(new File(fileMetaData).getName())).getAbsolutePath();
      } else { // so it should be a file reference
        fileOutputBase = removeExtention(fileOutputBase);
      }
    }

  }

  /**
   *
   */
  private void analyzeMetaDataFile() {
    bookInfo = new InfoBook(new File(fileMetaData));
    bookInfo.sortChaptersAndInsertTSEnd();
  }

  /***********************************************************************************************
   * Getter and setter
   ***********************************************************************************************/

//  public String getExecMp4Art() {
//    return execMp4Art;
//  }
//
//  public String getExecMp4Info() {
//    return execMp4Info;
//  }
//
//  public String getExecMp4Tags() {
//    return execMp4Tags;
//  }
//
//  public String getExecMp4Chaps() {
//    return execMp4Chaps;
//  }
//
//  public String getExecFfmpeg() {
//    return execFfmpeg;
//  }
//
//  public File getFileAudio() {
//    return new File(fileAudio);
//  }
//
//  public File getFileMetaData() {
//    return new File(fileMetaData);
//  }

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

//  public boolean isDryRun() {
//    return dryRun;
//  }
//
//  public OutputFormatEnum getOutputFormat() {
//    return outputFormat;
//  }
//
//  public Date getStartOfConverting() {
//    return startOfConverting;
//  }
//
//  public InfoBook getBookInfo() {
//    return bookInfo;
//  }

  /***********************************************************************************************
   * Helper methods
   ***********************************************************************************************/

  /**
   *
   * @param str
   * @return
   */
  public static String removeExtention(String str) {
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
    // TODO: Info von Andreas
    // assert 'x'.padRight(3,'_') == 'x__'

    return "$no".padLeft(3, '0')
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
