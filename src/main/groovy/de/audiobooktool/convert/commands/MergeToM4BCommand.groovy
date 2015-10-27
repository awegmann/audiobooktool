package de.audiobooktool.convert.commands

import de.audiobooktool.config.OutputFormatEnum
import de.audiobooktool.wrapper.ffmpeg.Ffmpeg
import de.audiobooktool.wrapper.mp4v2.Mp4Art
import de.audiobooktool.wrapper.mp4v2.Mp4Chaps
import de.audiobooktool.wrapper.mp4v2.Mp4Info
import de.audiobooktool.wrapper.mp4v2.Mp4Tags
import groovy.time.Duration

/**
 * User: andy
 * Date: 03.09.13
 */
class MergeToM4BCommand extends AbstractToolCommand {

  String audioFile
  String metaFile
  String destFile
  OutputFormatEnum outputFormat

  /**
   * Constructor for command.
   * @param data conversion parameter container
   */
  MergeToM4BCommand(ConversionParameterObj data) {
    init(data)
  }

  /**
   * Constructor for command.
   * @param data conversion parameter container
   */
  MergeToM4BCommand() {
  }

  /**
   * init the command object
   */
  public void init(ConversionParameterObj data) {
    audioFile = data.fileAudio
    metaFile = data.fileMetaData
    if (data.outputFormat == OutputFormatEnum.M4A) {
      this.outputFormat = data.outputFormat
      destFile = data.fileOutputBase + ".m4a"
    } else if (data.outputFormat == OutputFormatEnum.M4B) {
      this.outputFormat = data.outputFormat
      destFile = data.fileOutputBase + ".m4b"
    } else {
      throw new IllegalArgumentException("can't handle other formats than M4A and M4B")
    }
    dryRun = data.dryRun
  }

  @Override
  void execute() {

    println "Converting from " + audioFile + "\n" +
            "Metadata from.. " + metaFile + "\n" +
            "Output to...... " + destFile

    if (dryRun) {
      println "Dry run selected. Stopping now."
      return
    }

    def audioFlac = new Ffmpeg(audioFile)

    // if audio and meta info is in different files, check if audio length of these files match
    if (audioFile != metaFile) {
      Duration audioFileDuration = audioFlac.getDuration()
      Duration metaFileDuration = new Ffmpeg(metaFile).getDuration()

      if (Math.abs(audioFileDuration.toMilliseconds() - metaFileDuration.toMilliseconds()) > 500 ) {
        println "Audio and meta file duration differs!"
        println " audio file:  ${audioFileDuration} from ${audioFile}"
        println " meta file:   ${metaFileDuration} form ${metaFile}"
        thrown new IllegalStateException("audio length differs")
      }
    }

    if (destFile.endsWith(".m4a")) {
      audioFlac.convertToM4a(destFile)
    } else if (destFile.endsWith(".m4b")) {
      audioFlac.convertToM4b(destFile)
    } else {
      throw new IllegalArgumentException("Output-File muss auf .m4a oder .m4b enden.")
    }

    def mp4art = new Mp4Art(metaFile)
    def imageFile = mp4art.extractImage(0)
    def mp4artDest = new Mp4Art(destFile)

    mp4artDest.addImage(imageFile)
    new File(imageFile).delete()

    def mp4info = new Mp4Info(metaFile)
    def tags = mp4info.listTags()
    def mp4tags = new Mp4Tags(destFile)
    mp4tags.setTags(tags)

    def mp4Chaps = new Mp4Chaps(metaFile)
    def chapters = mp4Chaps.exportChapters()

    def mp4ChapsDest = new Mp4Chaps(destFile)
    mp4ChapsDest.importChapters(chapters)
  }
}
