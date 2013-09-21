package de.wegmann.wrapper.ffmpeg

import de.wegmann.config.DataObj
import de.wegmann.config.InfoChapter
import de.wegmann.wrapper.util.ToolsLocation
import groovy.util.logging.Log

import java.security.InvalidParameterException

/**
 * User: andy
 * Date: 21.06.13
 */
@Log
class Ffmpeg {

    /**
     * Tools locations / executables to run
     */
    protected ToolsLocation tools;

    /**
     * audio file to operate on.
     */
    protected String sourceAudioFile

    /**
     *
     */
    DataObj dataObject;

    /**
     * Constructor.
     * @param sourceAudioFile audio file to operate on.
     */
    public Ffmpeg(String sourceAudioFile) {
        super();
        this.dataObject = DataObj.getInstance()
        this.sourceAudioFile = sourceAudioFile;
        this.tools = ToolsLocation.getInstance()
    }

    public Ffmpeg() {
      super()
      this.dataObject = DataObj.getInstance()
      this.sourceAudioFile = this.dataObject.fileAudio;
      this.tools = ToolsLocation.getInstance()
    }

    /**
     * Converts the sourceAudioFile to the destinationAudioFile.
     *
     * @param destinationAudioFile Name of destination file. Must end with ".m4a".
     */
    public void convertToM4a(String destinationAudioFile) {
        if (!destinationAudioFile.endsWith(".m4a")) {
            throw new IllegalArgumentException("destination file name does not end with '.m4a' <$destinationAudioFile")
        }

        def process = [tools.ffmpegExecutable, "-i", sourceAudioFile, "-strict", "-2", "-y", destinationAudioFile].execute()
        process.err.eachLine {
            log.info "> $it"
        }
    }


    /**
     * Converts the sourceAudioFile to the destinationAudioFile.
     *
     * @param destinationAudioFile Name of destination file. Must end with ".m4b".
     */
    public void convertToM4b(String destinationAudioFile) {
        if (!destinationAudioFile.endsWith(".m4b")) {
            throw new IllegalArgumentException("destination file name does not end with '.m4b' <$destinationAudioFile")
        }
        //def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-codec", "aac", "-f","mp4","-strict", "-2", "-y", destinationAudioFile-".m4b"+".m4a" ].execute()
        def intermediateFile = destinationAudioFile - ".m4b" + "-intermediate.m4a"
        def process = [tools.ffmpegExecutable, "-i", sourceAudioFile, "-strict", "-2", "-y", intermediateFile].execute()
        process.err.eachLine {
            log.info "> $it"
        }
        new File(intermediateFile).renameTo(new File(destinationAudioFile))
    }

    /**
     *
     */
    public void convertToMP3() {

      Iterator<InfoChapter> infoChapterIterator = dataObject.getBookInfo().getChapterIterator();
      while (infoChapterIterator.hasNext()) {
        convertToMP3(infoChapterIterator.next())
      }

    }

    /**
     *
     * @param infoChapter
     */
    public void convertToMP3(InfoChapter infoChapter) {

      // ffmpeg -i /bla/Baldacci.flac -ss 00:01:00.00 -t 00:00:15.99 -acodec libmp3lame -ab 128k /bla/some.mp3
      def process

      if (infoChapter.tsEnd != null)
        process = [dataObject.execFfmpeg, "-i", dataObject.fileAudio,
              "-ss", infoChapter.tsStart, "-to", infoChapter.tsEnd,
                dataObject.getFileOutput(infoChapter.titleNo, 3, ".mp3")].execute()
      else
        process = [dataObject.execFfmpeg, "-i", dataObject.fileAudio,
                "-ss", infoChapter.tsStart,
                dataObject.getFileOutput(infoChapter.titleNo, 3, ".mp3")].execute()

      process.err.eachLine {
        log.info "> $it"
      }
    }

    /**
     * Converts the sourceAudioFile to the destinationAudioFile.
     *
     * @param destinationAudioFile Name of destination file. Must end with ".flac".
     */
    public void convertToFlac(String destinationAudioFile) {
        if (!destinationAudioFile.endsWith(".flac")) {
            throw new IllegalArgumentException("destination file name does not end with '.flac' <$destinationAudioFile")
        }
        def process = [tools.ffmpegExecutable, "-i", sourceAudioFile, "-y", destinationAudioFile].execute()
        process.err.eachLine {
            log.info "> $it"
        }

    }
}
