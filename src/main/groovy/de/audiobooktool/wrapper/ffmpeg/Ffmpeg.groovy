package de.audiobooktool.wrapper.ffmpeg

import de.audiobooktool.config.BookInfo
import de.audiobooktool.config.ChapterInfo
import de.audiobooktool.convert.commands.AbstractToolCommand
import de.audiobooktool.wrapper.util.ToolsLocation
import groovy.time.Duration
import groovy.util.logging.Log4j
/**
 * Wrapper around ffmpeg tool to get info about audio files, convert, merge and split them.
 * User: Andreas Wegmann
 * Date: 21.06.13
 */
@Log4j
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
     * Constructor.
     * @param sourceAudioFile audio file to operate on.
     */
    public Ffmpeg(String sourceAudioFile) {
        super();
        this.sourceAudioFile = sourceAudioFile;
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
            log.info "$it"
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
        //def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-codec", "aac", "-f","mp4","-strict", "-2",
        //                 "-y", destinationAudioFile-".m4b"+".m4a" ].execute()
        def intermediateFile = destinationAudioFile - ".m4b" + "-intermediate.m4a"
        def process = [tools.ffmpegExecutable, "-i", sourceAudioFile, "-strict", "-2", "-y", intermediateFile].execute()
        process.err.eachLine {
            log.info "$it"
        }
        new File(intermediateFile).renameTo(new File(destinationAudioFile))
    }


  /**
   * Extract all chapters from source audio file. This should be much
   * faster than starting ffmpeg for each chapter.
   *
   * @param outDirectory directory where to place the mp3 files, Name is chapter_001.mp3 a.s.o.
   * @param bookInfo contains all information about the book, especially the information about chapters
   */
  public void extractChaptersToMP3(File outDirectory, BookInfo bookInfo) {

    boolean runDebug = true;

    // Take care that the outDirectory is created
    if (!outDirectory.exists())
      outDirectory.mkdirs();

    // TODO: if outDirctory is null ... use default ... create directory from sourceAudioFile without extension!

    // TODO: @Andreas => is there any shorter possibility to use ffmpeg with one call? Because the command is often very long ...

    // example from internet:
    // ffmpeg -v quiet -y -i input.ts
    //        -vcodec copy -acodec copy -ss 00:00:00 -t 00:30:00 -sn test3.mkv
    //        -vcodec copy -acodec copy -ss 00:30:00 -t 01:00:00 -sn test4.mkv

    // changed syntax to our needs:
    // ffmpeg -i /bla/Baldacci.flac
    //        -ss 00:00:00.00 -t 00:00:15.99 -acodec libmp3lame -ab 128k /bla/Baldacci/chapter_001.mp3
    //        -ss 00:00:15.99 -t 00:01:20.64 -acodec libmp3lame -ab 128k /bla/Baldacci/chapter_002.mp3
    //        .....
    //        -ss 06:42:45.56 -acodec libmp3lame -ab 128k /bla/Baldacci/chapter_156.mp3

    List<String> commandParts = new LinkedList<>();
    commandParts.add(tools.getFfmpegExecutable())

    Iterator<ChapterInfo> chapIt = bookInfo.getChapterIterator();
    int cnt = 0;
    while (chapIt.hasNext()) {
      ChapterInfo cur = chapIt.next()
      cnt++;
      if (!runDebug || ((cnt > 0 && cnt <= 145))) { // || !chapIt.hasNext())) {

        File outFile = new File(outDirectory.getAbsolutePath() + "/chapter_" + "$cnt".padLeft(3, '0') + ".mp3");
        // if mp3 already exists the program ask if it should override file ... never exit command!!!
        if (outFile.exists())
          outFile.delete();

        if (chapIt.hasNext()) {
          commandParts.add("-ss")
          commandParts.add(cur.tsStart)
          commandParts.add("-to")
          commandParts.add(cur.tsEnd)
          commandParts.add("-acodec")
          commandParts.add("libmp3lame")
          commandParts.add("-ab")
          commandParts.add("128k")
          commandParts.add(outFile.getAbsolutePath())
        } else {
          commandParts.add("-ss")
          commandParts.add(cur.tsStart)
          commandParts.add("-acodec")
          commandParts.add("libmp3lame")
          commandParts.add("-ab")
          commandParts.add("128k")
          commandParts.add(outFile.getAbsolutePath())
        }

      }
    }

    commandParts.add("-i")
    commandParts.add(sourceAudioFile)

    println "executing command:"
    StringBuffer commandPartsAsString = new StringBuffer();
    String[] commandPartsAsArray = commandParts.toArray(new String[0]);
    for (String curStr: commandPartsAsArray)
      commandPartsAsString.append(curStr + " ")

    println commandPartsAsString
    println "Number of characters used by command: " + commandPartsAsString.toString().length()

    // TODO: @ Andreas => why is the command not executed??? The mp3 files will be created, but converting not begins!
    //                 => Take the command from output and insert to command line will convert the file to mp3s!
    //   1.) with 152 chapters it occours also a error at command line:
    //          => Stream #0:3 -> #151:0 (png -> png)
    //             Stream #0:0 -> #151:1 (aac -> libmp3lame)
    //             Error while opening encoder for output stream #131:0 - maybe incorrect parameters such as bit_rate, rate, width or height
    //   2.) 1 to 146 ... Number of characters used by command: 21854 => groovy is NOT stopping directly but not encoding
    //       1 to 145 ... Number of characters used by command: 21705 => start encoding ...
    //       1 to 147 ... Number of characters used by command: ????? => grovy is stopping the process at once!


//    AbstractToolCommand.runCommand(commandPartsAsArray, true, null)
    AbstractToolCommand.runCommand(commandPartsAsArray)
//    AbstractToolCommand.runCommand(commandPartsAsString)

    // TODO: add meta information to mp3 file ... especially to track number!
  }

    /**
     * Extract this chapter from source audio file. Take care this takes a quite
     * long time to find the beginning of the part to extract, because the file
     * is analyzed from begin to the position chapter begins.
     *
     * @param outFile the file to write mp3
     * @param infoChapter the chapter information to extract from input audio file
     */
    public void extractChapterToMP3(File outFile, ChapterInfo infoChapter) {

      // TODO: Maybe a single command can be much faster ??? ... not really!!! :-(
      // http://stackoverflow.com/questions/6984628/ffmpeg-ss-weird-behaviour

      // ffmpeg -i /bla/Baldacci.flac -ss 00:01:00.00 -t 00:00:15.99 -acodec libmp3lame -ab 128k /bla/some.mp3
      def command

      // Take care that the outDirectory is created
      if (!outFile.getParentFile().exists())
        outFile.getParentFile().mkdirs();

      // if mp3 already exists the program ask if it should override file ... never exit command!!!
      if (outFile.exists())
        outFile.delete();

      List<String> commandParts = new LinkedList<>();
      commandParts.add(tools.getFfmpegExecutable())

      if (infoChapter.tsEnd != null) {
        commandParts.add("-ss")
        commandParts.add(infoChapter.tsStart)
        commandParts.add("-to")
        commandParts.add(infoChapter.tsEnd)
        commandParts.add("-acodec")
        commandParts.add("libmp3lame")
        commandParts.add("-ab")
        commandParts.add("128k")
        commandParts.add(outFile.getAbsolutePath())
      } else {
        commandParts.add("-ss")
        commandParts.add(infoChapter.tsStart)
        commandParts.add("-acodec")
        commandParts.add("libmp3lame")
        commandParts.add("-ab")
        commandParts.add("128k")
        commandParts.add(outFile.getAbsolutePath())
      }

      commandParts.add("-i")
      commandParts.add(sourceAudioFile)

      println "executing command:"
      StringBuffer commandPartsAsString = new StringBuffer();
      String[] commandPartsAsArray = commandParts.toArray(new String[0]);
      for (String curStr: commandPartsAsArray)
        commandPartsAsString.append(curStr + " ")

      println commandPartsAsString
      println "Number of characters used by command: " + commandPartsAsString.toString().length()

//      if (infoChapter.tsEnd != null) {
//        command = [tools.ffmpegExecutable,
//                "-ss", infoChapter.tsStart, "-to", infoChapter.tsEnd,
//                "-i", sourceAudioFile,
//                "-acodec", "libmp3lame", "-ab", "128k",
//                outFile]
//      } else {
//        command = [tools.ffmpegExecutable,
//                "-ss", infoChapter.tsStart,
//                "-i", sourceAudioFile,
//                "-acodec", "libmp3lame", "-ab", "128k",
//                outFile]
//      }

//      AbstractToolCommand.runCommand(commandParts.toArray(new String[0]))
      AbstractToolCommand.runCommand(commandParts)
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
            log.info it
        }
    }

    /**
     * Get duration from audio file.
     *
     * ffmpeg is startet with "-i" for the output. Without setting an output file, just the information about
     * the file is written to stderr. Duration is grabbed from this output.
     *
     * @param audiofile the audio file to get duration from
     * @return duration of audio file
     */
    public Duration getDuration() {
        def file = new File(sourceAudioFile)
        if (!file.exists()) {
            throw new IllegalArgumentException("file $sourceAudioFile does not exists")
        }

        def returnDuration = null
        def durationFindRegex = /.*Duration: (\d{2}):(\d{2}):(\d{2}).(\d{2}).*/
        def process = [tools.ffmpegExecutable, "-i", sourceAudioFile].execute()
        process.err.eachLine {
            log.debug it
            def matcher = (it =~ durationFindRegex)
            if (matcher.matches()) {
                log.info "found duration"
                returnDuration = new Duration(0, matcher[0][1].toInteger(), matcher[0][2].toInteger(),
                        matcher[0][3].toInteger(), matcher[0][4].toInteger() * 10)
            }
        }
        return returnDuration

    }
}
