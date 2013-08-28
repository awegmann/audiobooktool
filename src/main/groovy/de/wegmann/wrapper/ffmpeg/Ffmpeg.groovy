package de.wegmann.wrapper.ffmpeg

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
     * Constructor.
     * @param sourceAudioFile audio file to operate on.
     */
    public Ffmpeg(String sourceAudioFile) {
        super();
        this.sourceAudioFile = sourceAudioFile;
        this.tools = ToolsLocation.getInstance()
    }

    public void convertToM4a(String destinationAudioFile) {
        def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-strict", "-2", "-y", destinationAudioFile ].execute()
        process.err.eachLine {
            log.info "> $it"
        }
    }

    public void convertToM4b(String destinationAudioFile) {
        if (!destinationAudioFile.endsWith(".m4b")) {
            throw new IllegalArgumentException("destination file name does not end with '.m4b' <$destinationAudioFile")
        }
        //def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-codec", "aac", "-f","mp4","-strict", "-2", "-y", destinationAudioFile-".m4b"+".m4a" ].execute()
        def intermediateFile = destinationAudioFile - ".m4b" + "-intermediate.m4a"
        def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-strict", "-2", "-y", intermediateFile].execute()
        process.err.eachLine {
            log.info "> $it"
        }
        new File(intermediateFile).renameTo(new File(destinationAudioFile))
    }

    public void convertToFlac(String destinationAudioFile) {
        def process = [ tools.ffmpegExecutable, "-i", sourceAudioFile, "-y", destinationAudioFile ].execute()
        process.err.eachLine {
            log.info "> $it"
        }

    }
}
