package de.wegmann.convert

import de.wegmann.wrapper.ffmpeg.Ffmpeg
import de.wegmann.wrapper.mp4v2.Mp4Art
import de.wegmann.wrapper.mp4v2.Mp4Chaps
import de.wegmann.wrapper.mp4v2.Mp4Info
import de.wegmann.wrapper.mp4v2.Mp4Tags
import de.wegmann.wrapper.mp4v2.WrapperException

/**
 * User: andy
 * Date: 05.07.13
 */
class ConvertTool {
    public void usage() {
        println("""
Usage:
    Convert Flac encoded file to a .m4a file and copy all metadata from third file to the new one.

Options:
    -a file, --audioFrom=file
        use file as audio source
    -m file, --metaFrom=file
        use file as metainfo source

""")
    }

    public static void main(String[] args) {
        def cli = new CliBuilder(usage: 'ConvertTool -a file -m file [-o file]\nconverts an audio file to a ".m4b" audio' +
                ' book and copies all metadata from another file to it.')
        cli.h(longOpt: 'help', 'usage information')
        cli.a(longOpt: 'audioFrom', args: 1, required: true,
                argName: 'audiofile', 'use audiofile as audio source to convert from')
        cli.m(longOpt: 'metaFrom', args: 1, required: true,
                argName: 'audiofile', 'use audiofile as meta info source to copy from')
        cli.o(longOpt: 'outputTo', args: 1, required: false,
                argName: 'file', 'if file is a directory, put the converted file in here, if it\' a file name use filename ' +
                'for output (default is base name of metaFrom-file plus .m4b extension)')
        cli.d(longOpt: 'dryRun', args: 0, required: false,
                'do not convert, just print what files are affected')

        def opt = cli.parse(args)

        if (!opt) {
            return
        }

        if (opt.h) {
            cli.usage()
        }

        String outputFilename
        if (opt.o) {
            outputFilename = opt.o
            if (new File(outputFilename).isDirectory()) {
                outputFilename = getDefaultOutputFilename(outputFilename, opt.m)
            }
        } else {
            // cut of extension and add ".m4b"
            def metaFile = new File(opt.m)
            outputFilename = getDefaultOutputFilename(metaFile.getParent(), metaFile.getName())
        }

        println "Converting from " + opt.a + "\n" +
                "Metadata from.. " + opt.m + "\n" +
                "Output to...... " + outputFilename;

        if (opt.d) {
            println "Dry run selected. Stopping now."
            return
        }

        doConvertAndCopyMeta(opt.a, opt.m, outputFilename)
    }

    /**
     * calculates the default output filename
     * @param p
     * @return
     */
    static String getDefaultOutputFilename(String outputDirectory, String metaFilename) {
        String outputFilename

        metaFilename = new File(metaFilename).getName()
        def lastIndexOfDot = metaFilename.lastIndexOf('.')
        if (lastIndexOfDot != -1) {
            outputFilename = metaFilename.substring(0, lastIndexOfDot)
        }
        outputFilename += ".m4b"
        File outputFile

        if (outputDirectory != null) {
            outputFile = new File(outputDirectory, outputFilename)
        } else {
            outputFile = new File(outputFilename)
        }


        if (outputFile.exists()) {
            // just to be unique, add a timestamp to basename
            outputFilename = metaFilename.substring(0, lastIndexOfDot) + "-" + (new Date().getDateTimeString()) + ".m4b"
        }
        return outputFile.getPath()
    }

    /**
     * Convert audiofile to destFile and copy meta info from metaFile to destFile.
     * @param audioFile audio file to convert
     * @param metaFile meta file to get info from
     * @param destFile destination file to create
     */
    static void doConvertAndCopyMeta(String audioFile, String metaFile, String destFile) {
        def audioFlac = new Ffmpeg(audioFile)
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
