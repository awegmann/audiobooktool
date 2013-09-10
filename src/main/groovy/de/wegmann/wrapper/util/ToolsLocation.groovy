package de.wegmann.wrapper.util

import groovy.util.logging.Log;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * Class to supply native tools locations.
 */
@Log
class ToolsLocation {

  private static INSTANCE = null

  private static XML_CONFIGURATION = null
  private static final String CONFIG_NAME = ".abtool.conf"
  private static final File CONFIG_FILE_USER_HOME = new File(System.getProperty("user.home") + "/" + CONFIG_NAME)
  private static final File CONFIG_FILE_WORK_DIR = new File(CONFIG_NAME)

  private final static String CONF_MP4ART_EXEC = "executables.mp4art"
  String mp4ArtExecutable = "mp4art"
  private final static String CONF_MP4INFO_EXEC = "executables.mp4info"
  String mp4InfoExecutable = "mp4info"
  private final static String CONF_MP4TAGS_EXEC = "executables.mp4tags"
  String mp4TagsExecutable = "mp4tags"
  private final static String CONF_MP4CHAPS_EXEC = "executables.mp4chaps"
  String mp4ChapsExecutable = "mp4chaps"
  private final static String CONF_FFMPEG_EXEC = "executables.ffmpeg"
  String ffmpegExecutable = "ffmpeg"

  /**
   * Return the instance of ToolsLocation
   * @return
   */
  static ToolsLocation getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ToolsLocation()
    return INSTANCE
  }

  /**
   * Private constructor
   */
  private ToolsLocation() {
    loadConfigData()
  }

  /**
   * Load the configuration. First try to get the values from config file. If no configuration for
   * this command/ option is found use the default value from variable. Try if the command is
   * executable - if not search inside directories.
   */
  private void loadConfigData() {

    readConfigDefault()
//		readConfigWorkDir()

    // mp4art
    mp4ArtExecutable = XML_CONFIGURATION.getString(CONF_MP4ART_EXEC, mp4ArtExecutable)
    if (!checkIfCanBeExecuted(mp4ArtExecutable)) {
      File executable = findExecutable(new File(mp4ArtExecutable).getName())
      if (executable != null) {
        writeConfigDefault(CONF_MP4ART_EXEC, executable.getAbsolutePath())
        mp4ArtExecutable = executable.getAbsolutePath()
      } else {
        log.info "Abort program execution. Can't find executable: ${mp4ArtExecutable}"
        System.exit(1)
      }
    }

    // mp4info
    mp4InfoExecutable = XML_CONFIGURATION.getString(CONF_MP4INFO_EXEC, mp4InfoExecutable)
    if (!checkIfCanBeExecuted(mp4InfoExecutable)) {
      File executable = findExecutable(new File(mp4InfoExecutable).getName())
      if (executable != null) {
        writeConfigDefault(CONF_MP4INFO_EXEC, executable.getAbsolutePath())
        mp4InfoExecutable = executable.getAbsolutePath()
      } else {
        log.info "Abort program execution. Can't find executable: ${mp4InfoExecutable}"
        System.exit(1)
      }
    }

    // mp4tags
    mp4TagsExecutable = XML_CONFIGURATION.getString(CONF_MP4TAGS_EXEC, mp4TagsExecutable)
    if (!checkIfCanBeExecuted(mp4TagsExecutable)) {
      File executable = findExecutable(new File(mp4TagsExecutable).getName())
      if (executable != null) {
        writeConfigDefault(CONF_MP4TAGS_EXEC, executable.getAbsolutePath())
        mp4TagsExecutable = executable.getAbsolutePath()
      } else {
        log.info "Abort program execution. Can't find executable: ${mp4TagsExecutable}"
        System.exit(1)
      }
    }

    // mp4chaps
    mp4ChapsExecutable = XML_CONFIGURATION.getString(CONF_MP4CHAPS_EXEC, mp4ChapsExecutable)
    if (!checkIfCanBeExecuted(mp4ChapsExecutable)) {
      File executable = findExecutable(new File(mp4ChapsExecutable).getName())
      if (executable != null) {
        writeConfigDefault(CONF_MP4CHAPS_EXEC, executable.getAbsolutePath())
        mp4ChapsExecutable = executable.getAbsolutePath()
      } else {
        log.info "Abort program execution. Can't find executable: ${mp4ChapsExecutable}"
        System.exit(1)
      }
    }

    // ffmpeg
    ffmpegExecutable = XML_CONFIGURATION.getString(CONF_FFMPEG_EXEC, ffmpegExecutable)
    if (!checkIfCanBeExecuted(ffmpegExecutable)) {
      File executable = findExecutable(new File(ffmpegExecutable).getName())
      if (executable != null) {
        writeConfigDefault(CONF_FFMPEG_EXEC, executable.getAbsolutePath())
        ffmpegExecutable = executable.getAbsolutePath()
      } else {
        log.info "Abort program execution. Can't find executable: ${ffmpegExecutable}"
        System.exit(1)
      }
    }

    dumpExecutables()
  }

  /**
   *
   */
  private void dumpExecutables() {
    log.info "********************************************"
    log.info "* Use the following executables: "
    log.info "*  => mp4art: ${mp4ArtExecutable}"
    log.info "*  => mp4chaps: ${mp4ChapsExecutable}"
    log.info "*  => mp4info: ${mp4InfoExecutable}"
    log.info "*  => mp4tags: ${mp4TagsExecutable}"
    log.info "*  => ffmpeg: ${ffmpegExecutable}"
    log.info "********************************************"
  }

  /**
   *
   * @param progRef
   * @return
   */
  private boolean checkIfCanBeExecuted(String progRef) {
    try {
      def process = [progRef].execute()
      process.waitFor()
    } catch (IOException ioe) {
      return false
    }
    return true
  }

  /**
   * Search for the binary at possible executable directories
   *
   * @param progName the name of the executable
   * @return a file reference to the binary or null if it is not found
   */
  private File findExecutable(String progName) {

    log.info "search for executable ${progName}: "

    def possiblePath = [ "/usr/bin/", "/usr/local/bin/", "/opt/bin/" ]
    def result = possiblePath.findResult { new File(it, progName).exists() ? it+progName : null }
    return new File(result)
  }

  /**
   * Write the key/ value pair into the default config file
   *
   * @param key
   * @param value
   */
  private void writeConfigDefault(String key, String value) {
    if (XML_CONFIGURATION == null)
      XML_CONFIGURATION = new XMLConfiguration()
    XML_CONFIGURATION.setFile(CONFIG_FILE_USER_HOME)
    XML_CONFIGURATION.setProperty(key, value);
    XML_CONFIGURATION.save();
  }

  /**
   * Read the default config file at users home directory
   */
  private void readConfigDefault() {
    if (XML_CONFIGURATION == null) {
      XML_CONFIGURATION = new XMLConfiguration();
    }

    if (CONFIG_FILE_USER_HOME.exists()) {
      XML_CONFIGURATION.setFile(CONFIG_FILE_USER_HOME);
      XML_CONFIGURATION.load();
    }
  }

  /**
   * Read the configuration from working directory. The directory the program
   * was started.
   */
  private void readConfigWorkDir() {
    if (XML_CONFIGURATION == null) {
      XML_CONFIGURATION = new XMLConfiguration();
    }

    if (CONFIG_FILE_WORK_DIR.exists()) {
      XML_CONFIGURATION.setFile(CONFIG_FILE_WORK_DIR);
      XML_CONFIGURATION.load();
    }
  }

}
