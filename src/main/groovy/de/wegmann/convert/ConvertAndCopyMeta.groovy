package de.wegmann.convert

import de.wegmann.wrapper.ffmpeg.Ffmpeg
import de.wegmann.wrapper.mp4v2.Mp4Art
import de.wegmann.wrapper.mp4v2.Mp4Chaps
import de.wegmann.wrapper.mp4v2.Mp4Info
import de.wegmann.wrapper.mp4v2.Mp4Tags

/**
 * User: andy
 * Date: 03.09.13
 */
class ConvertAndCopyMeta extends AbstractToolCommand {

    String audioFile
    String metaFile
    String destFile

    /**
     * Convert audiofile to destFile and copy meta info from metaFile to destFile.
     * @param audioFile audio file to convert
     * @param metaFile meta file to get info from
     * @param destFile destination file to create
     */


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
