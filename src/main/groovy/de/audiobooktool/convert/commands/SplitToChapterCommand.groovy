package de.audiobooktool.convert.commands

import de.audiobooktool.config.BookInfo
import de.audiobooktool.config.ChapterInfo
import de.audiobooktool.wrapper.ffmpeg.Ffmpeg
import groovy.util.logging.Log4j
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.TagField
/**
 * User: andy
 * Date: 03.09.13
 */
@Log4j
class SplitToChapterCommand extends AbstractToolCommand {

    ConversionParameterObj conversionParameter

    BookInfo bookInfo;

    /**
     * Initialize the book info object with chapter info from metadata file.
     */
    private void initializeBookinfo() {
        bookInfo = new BookInfo(new File(conversionParameter.fileMetaData));
        bookInfo.sortChaptersAndFinalizeData();
    }

    /**
     * Write metadata from chapter info and book info to a chapter audio file.
     * @param chapterFile file, to write metadata to
     * @param chapter info from chapter
     */
    public void writeMetaDataToChapterFile(File chapterFile, ChapterInfo chapter, BookInfo bookInfo) {

        AudioFile f = AudioFileIO.read(chapterFile);
        Tag tag = f.getTag();

        // Tags direct from book info
        tag.setField(FieldKey.ARTIST, bookInfo.artist)
        tag.setField(FieldKey.ALBUM_ARTIST, bookInfo.albumArtist)
        tag.setField(FieldKey.COMMENT, bookInfo.comment)
        tag.setField(FieldKey.YEAR, bookInfo.year)
        tag.setField(FieldKey.ALBUM, bookInfo.title)

        tag.addField(bookInfo.cover)

        // Tags direct from chapter ...
        tag.setField(FieldKey.TITLE, chapter.title)
        tag.setField(FieldKey.DISC_NO, "" + chapter.cdNo)
        tag.setField(FieldKey.DISC_TOTAL, "" + chapter.cdNoTotal)
        tag.setField(FieldKey.TRACK, "" + chapter.titleNo)
        tag.setField(FieldKey.TRACK_TOTAL, "" + chapter.titleNoTotal)

        f.commit();
    }

    /**
     * Dump metadata for debug purpose.
     * @param fileWithMetadata file to read metadata from
     */
    def dumpAllMetaData(File fileWithMetadata) {
        AudioFile f = AudioFileIO.read(fileWithMetadata);
        Tag tag = f.getTag();
        AudioHeader header = f.getAudioHeader();
        // TODO: needs book length with milliseconds detail!
        //def bookLength = header.getTrackLength() * 1000;
        Iterator<TagField> it = tag.fields
        while (it.hasNext()) {
            TagField key = it.next()
            println "key: " + key.getId() + "; First Value: " + tag.getFirst(key.getId())
        }
    }

    /**
     * Constructor for command.
     * @param data conversion parameter container
     */
    SplitToChapterCommand(ConversionParameterObj data) {
        init(data)
    }

    public void init(ConversionParameterObj data) {
        this.conversionParameter = data
    }

    /**
     * Convert audiofile to splitted chapter mp3 files and copy meta info from metaFile to destFile.
     */
    @Override
    void execute() {

        println "Converting from " + conversionParameter.fileAudio + "\n" +
                "Metadata from.. " + conversionParameter.fileMetaData + "\n" +
                "Output to...... " + conversionParameter.fileOutputBase

        if (conversionParameter.dryRun) {
            println "Dry run selected. Stopping now."
            return
        }

        initializeBookinfo()

        Ffmpeg ffmpegTool = new Ffmpeg(conversionParameter.fileAudio)

        Iterator<ChapterInfo> infoChapterIterator = bookInfo.getChapterIterator();
        while (infoChapterIterator.hasNext()) {
            ChapterInfo cur = infoChapterIterator.next()
            File outFile = this.conversionParameter.getFileOutput(cur.titleNo, 3, conversionParameter.outputFormat.extension)
            // convert chapter ...
            ffmpegTool.extractChapterToMP3(outFile, cur)
            // set tag information ...
            writeMetaDataToChapterFile(outFile, cur, bookInfo)
        }
    }
}
