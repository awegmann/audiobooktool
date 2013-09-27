package de.wegmann.wrapper.util

/**
 * Class to supply native tools locations.
 */
class ToolsLocation {
    private static final INSTANCE = new ToolsLocation()

    static ToolsLocation getInstance() {
        return INSTANCE
    }

    private ToolsLocation() {
        def homeConfig = new File(System.getProperty("user.home") + '/.audibletools.config')
        if (homeConfig.exists()) {
            def config = new ConfigSlurper().parse(homeConfig.toURL())
            mp4ArtExecutable = config.locations.mp4art
            mp4InfoExecutable = config.locations.mp4info
            mp4TagsExecutable = config.locations.mp4tags
            mp4ChapsExecutable = config.locations.mp4chaps
            ffmpegExecutable = config.locations.ffmpeg
        }
    }

    String mp4ArtExecutable = "mp4art"
    String mp4InfoExecutable = "mp4info"
    String mp4TagsExecutable = "mp4tags"
    String mp4ChapsExecutable = "mp4chaps"

<<<<<<< HEAD
    String ffmpegExecutable = "ffmpeg"
=======
  /**
   *
   * @return the xml configuration itself
   */
  def XMLConfiguration getXMLConfig() {
    return XML_CONFIGURATION;
  }

>>>>>>> 1fa65fe9feaabe0a18bfe42de8bc808c6e5b2d74
}
